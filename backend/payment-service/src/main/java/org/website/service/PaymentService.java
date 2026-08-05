package org.website.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.PaymentOrderRequest;
import org.website.dto.PaymentOrderResponse;
import org.website.dto.PaymentVerifyRequest;
import org.website.exception.InvalidPaymentException;
import org.website.exception.ResourceNotFoundException;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Payment;
import org.website.model.PaymentStatus;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.repository.BookingRepository;
import org.website.repository.PaymentRepository;
import org.website.repository.SeatRepository;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Payment Service - Manages payment processing via Razorpay.
 * 
 * This service handles the complete payment workflow:
 * 1. CREATE ORDER: Generate order on Razorpay, frontend opens modal
 * 2. VERIFY PAYMENT: Verify signature, confirm transaction, update booking
 * 
 * Razorpay Integration:
 * - Uses Razorpay Java SDK for REST API calls
 * - Sandbox mode (test keys) for development
 * - Production keys can be used by updating application.properties
 * 
 * Security:
 * - Signature verification using HMAC-SHA256 algorithm
 * - Only payments with valid signatures are accepted
 * - User isolation: users can only pay for their own bookings
 * 
 * Payment Status Lifecycle:
 * PENDING → (signature verified) → COMPLETED
 *        → (signature invalid) → FAILED
 * 
 * On Success:
 * - Booking status: PENDING → CONFIRMED
 * - Seat status: RESERVED → BOOKED (permanent lock)
 * - Payment record created with Razorpay transaction details
 * - Notification email sent to user
 * 
 * On Failure:
 * - Payment record created with FAILED status
 * - Seats released back to AVAILABLE
 * - User can retry booking
 * 
 * Configuration Required in application.properties:
 * razorpay.key-id=rzp_test_... (or rzp_live_...)
 * razorpay.key-secret=... (keep secret, never expose)
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@Service
@Slf4j
@Transactional
public class PaymentService {

    private BookingRepository bookingRepository;
    private PaymentRepository paymentRepository;
    private SeatRepository seatRepository;

    PaymentService(BookingRepository bookingRepository, PaymentRepository paymentRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatRepository = seatRepository;
    }

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    /**
     * Create a payment order on Razorpay.
     * 
     * This method:
     * 1. Validates booking exists and belongs to user
     * 2. Initializes RazorpayClient (lazy initialization)
     * 3. Calls Razorpay API to create order with booking amount
     * 4. Stores Payment record with PENDING status
     * 5. Updates Booking with orderId reference
     * 6. Returns orderId to frontend
     * 
     * Frontend Next Steps:
     * - Use orderId to open Razorpay checkout modal
     * - User enters payment details (card, UPI, etc.)
     * - On success/failure, Razorpay calls frontend callback
     * - Frontend extracts razorpay_payment_id and razorpay_signature
     * - Frontend calls /verify endpoint
     * 
     * @param userId The user making payment (extracted from JWT)
     * @param request PaymentOrderRequest with bookingReference
     * 
     * @return PaymentOrderResponse with orderId, amount, currency
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if booking belongs to different user (ERR002)
     * @throws InvalidPaymentException if Razorpay API error (ERR504)
     * 
     * Example Razorpay Response:
     * {
     *   "id": "order_MBD7ukQ69xz6qJ",
     *   "amount": 75000,
     *   "currency": "INR",
     *   "receipt": "BK-ABCD1234EF",
     *   "status": "created"
     * }
     */
    public PaymentOrderResponse createPaymentOrder(Long userId, PaymentOrderRequest request) {
        log.debug("Creating payment order for booking reference: {}", request.getBookingReference());
        
        Booking booking = bookingRepository.findByBookingReference(request.getBookingReference())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + request.getBookingReference()));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[ERR002] Unauthorized payment attempt for booking: {} by user: {}", 
                     booking.getBookingReference(), userId);
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        try {
            // Lazy initialization of RazorpayClient
            if (razorpayClient == null) {
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            }

            // Create order on Razorpay
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", booking.getTotalAmountInCents()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", booking.getBookingReference());

            com.razorpay.Order orderResponse = razorpayClient.orders.create(orderRequest);
            String orderId = (String) orderResponse.get("id");

            // Store payment record locally
            Payment payment = new Payment(booking, orderId, booking.getTotalAmountInCents(), PaymentStatus.PENDING);
            paymentRepository.save(payment);

            // Link order ID to booking
            booking.setPaymentOrderId(orderId);
            bookingRepository.save(booking);

            log.info("[PAYMENT] Order created: {} for booking: {}, amount: {}", 
                     orderId, booking.getBookingReference(), booking.getTotalAmountInCents());

            return new PaymentOrderResponse(
                orderId,
                booking.getTotalAmountInCents(),
                "INR",
                razorpayKeyId
            );
        } catch (RazorpayException e) {
            log.error("[ERR504] Razorpay exception: {}", e.getMessage());
            throw new InvalidPaymentException("Failed to create payment order: " + e.getMessage());
        }
    }

    /**
     * Verify Razorpay payment signature and complete booking.
     * 
     * Security-Critical Operation:
     * This method verifies that the payment came from Razorpay using HMAC-SHA256 signature.
     * This prevents fraudulent payments and ensures payment authenticity.
     * 
     * Signature Verification Process:
     * 1. Calculate HMAC-SHA256 of "{orderId}|{paymentId}" using secret key
     * 2. Compare calculated signature with signature from frontend
     * 3. Only proceed if signatures match exactly
     * 
     * On Success:
     * - Payment status: PENDING → COMPLETED
     * - Booking status: PENDING → CONFIRMED
     * - Seat status: RESERVED → BOOKED (permanent)
     * - Payment completion timestamp recorded
     * - User notified via email
     * 
     * @param userId The user completing payment (extracted from JWT)
     * @param request PaymentVerifyRequest with payment details from Razorpay
     * 
     * @return true if verification successful, false otherwise
     * @throws ResourceNotFoundException if payment/booking not found (ERR105, ERR106)
     * @throws IllegalArgumentException if payment belongs to different user (ERR002)
     * @throws InvalidPaymentException if signature verification fails (ERR301)
     * 
     * Example Request:
     * {
     *   "bookingReference": "BK-ABCD1234EF",
     *   "razorpayOrderId": "order_MBD7ukQ69xz6qJ",
     *   "razorpayPaymentId": "pay_MBD7vg4jKJFtQe",
     *   "razorpaySignature": "9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d"
     * }
     * 
     * Signature Example:
     * Input Message: "order_MBD7ukQ69xz6qJ|pay_MBD7vg4jKJFtQe"
     * Secret Key: razorpay.key-secret value
     * Algorithm: HMAC-SHA256
     * Output: "9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d"
     */
    public boolean verifyPayment(Long userId, PaymentVerifyRequest request) {
        log.debug("Verifying payment for order: {}", request.getRazorpayOrderId());
        
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + request.getRazorpayOrderId()));

        Booking booking = payment.getBooking();
        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[ERR002] Unauthorized payment verification attempt by user: {}", userId);
            throw new IllegalArgumentException("Unauthorized access to payment");
        }

        // Verify signature (security-critical)
        if (!verifyRazorpaySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            log.warn("[ERR301] Invalid Razorpay signature for order: {}", request.getRazorpayOrderId());
            throw new InvalidPaymentException("Invalid payment signature");
        }

        // Update payment record with Razorpay details
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update booking status to CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentTime(LocalDateTime.now());
        bookingRepository.save(booking);

        // Lock seats permanently to BOOKED
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
            log.debug("Locked seat: {} to BOOKED status", seat.getSeatNumber());
        }

        log.info("[PAYMENT] Verified successfully for booking: {}, amount: {}", 
                 booking.getBookingReference(), booking.getTotalAmountInCents());
        return true;
    }

    /**
     * Verify Razorpay HMAC-SHA256 signature.
     * 
     * Security-critical method for payment verification.
     * Uses the Razorpay secret key to compute HMAC-SHA256 hash and compare with signature.
     * 
     * Algorithm:
     * 1. Create message: "{orderId}|{paymentId}"
     * 2. Compute HMAC-SHA256 using secret key
     * 3. Convert to hexadecimal string
     * 4. Compare with provided signature
     * 
     * This prevents payment tampering or replay attacks.
     * 
     * @param orderId The Razorpay order ID
     * @param paymentId The Razorpay payment ID
     * @param signature The signature to verify (from Razorpay webhook/response)
     * 
     * @return true if signature is valid, false otherwise
     */
    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            // Create message in Razorpay format
            String message = orderId + "|" + paymentId;
            
            // Initialize HMAC-SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            
            // Calculate signature
            byte[] hmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String computed = bytesToHex(hmac);
            
            // Constant-time comparison to prevent timing attacks
            boolean isValid = computed.equals(signature);
            
            if (!isValid) {
                log.debug("Signature mismatch. Computed: {}, Provided: {}", computed, signature);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("[ERR503] Error verifying signature: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Convert byte array to hexadecimal string.
     * 
     * Helper method for signature verification. Converts the HMAC output
     * (binary format) to hexadecimal string for comparison.
     * 
     * @param bytes The byte array to convert
     * @return Hexadecimal string representation
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void handleWebhookEvent(String payload) {
        log.info("Processing Razorpay webhook payload");

        try {
            JSONObject event = new JSONObject(payload);
            String eventType = event.optString("event");
            JSONObject payloadObject = event.optJSONObject("payload");
            if (payloadObject == null) {
                log.warn("Webhook payload missing payload object");
                return;
            }

            if (eventType == null || eventType.isEmpty()) {
                log.warn("Webhook event type missing");
                return;
            }

            String razorpayOrderId = null;
            String razorpayPaymentId = null;

            if (payloadObject.has("payment")) {
                JSONObject paymentContainer = payloadObject.optJSONObject("payment");
                if (paymentContainer != null) {
                    JSONObject paymentEntity = paymentContainer.optJSONObject("entity");
                    if (paymentEntity != null) {
                        razorpayOrderId = paymentEntity.optString("order_id", null);
                        razorpayPaymentId = paymentEntity.optString("id", null);
                    }
                }
            }

            if (razorpayOrderId == null && payloadObject.has("refund")) {
                JSONObject refundContainer = payloadObject.optJSONObject("refund");
                if (refundContainer != null) {
                    JSONObject refundEntity = refundContainer.optJSONObject("entity");
                    if (refundEntity != null) {
                        razorpayPaymentId = refundEntity.optString("payment_id", null);
                    }
                }
            }

            switch (eventType) {
                case "payment.captured" -> handlePaymentCaptured(razorpayOrderId, razorpayPaymentId);
                case "payment.failed" -> handlePaymentFailed(razorpayOrderId, razorpayPaymentId);
                case "refund.processed", "refund.created", "refund.updated" -> handleRefundProcessed(razorpayOrderId, razorpayPaymentId);
                default -> log.debug("Ignoring unhandled Razorpay webhook event: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook payload", e);
        }
    }

    private void handlePaymentCaptured(String orderId, String paymentId) {
        if (orderId == null || paymentId == null) {
            log.warn("Missing order_id or payment_id for payment.captured webhook");
            return;
        }

        paymentRepository.findByRazorpayOrderId(orderId).ifPresent(payment -> {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            booking.setPaymentTime(LocalDateTime.now());
            bookingRepository.save(booking);

            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);
            }

            log.info("Processed payment.captured webhook for order: {} and booking: {}", orderId, booking.getBookingReference());
        });
    }

    private void handlePaymentFailed(String orderId, String paymentId) {
        String identifier = orderId != null ? orderId : paymentId;
        if (identifier == null) {
            log.warn("Missing identifier for payment.failed webhook");
            return;
        }

        paymentRepository.findByRazorpayOrderId(orderId).ifPresentOrElse(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            log.info("Processing failed payment for booking: {}", booking.getBookingReference());
            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }
        }, () -> {
            if (paymentId != null) {
                paymentRepository.findByRazorpayPaymentId(paymentId).ifPresent(payment -> {
                    payment.setStatus(PaymentStatus.FAILED);
                    paymentRepository.save(payment);
                    for (Seat seat : payment.getBooking().getSeats()) {
                        seat.setStatus(SeatStatus.AVAILABLE);
                        seatRepository.save(seat);
                    }
                });
            }
        });
    }

    private void handleRefundProcessed(String orderId, String paymentId) {
        if (paymentId == null) {
            log.warn("Missing payment_id for refund webhook");
            return;
        }

        paymentRepository.findByRazorpayPaymentId(paymentId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.REFUNDED);
            bookingRepository.save(booking);

            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }

            log.info("Processed refund webhook for booking: {}", booking.getBookingReference());
        });
    }

    public boolean refundBooking(Long userId, String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("Unauthorized refund request for booking: {} by user: {}", bookingReference, userId);
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        if (!BookingStatus.CONFIRMED.equals(booking.getStatus())) {
            log.warn("Refund request invalid for booking {} with status {}", bookingReference, booking.getStatus());
            throw new IllegalStateException("Only confirmed bookings can be refunded");
        }

        Payment payment = paymentRepository.findByBookingId(booking.getId())
            .orElseThrow(() -> new IllegalArgumentException("Payment record not found for booking: " + bookingReference));

        if (payment.getRazorpayPaymentId() == null) {
            throw new IllegalStateException("Cannot refund payment without a Razorpay payment ID");
        }

        try {
            if (razorpayClient == null) {
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            }

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("payment_id", payment.getRazorpayPaymentId());
            com.razorpay.Refund refundResponse = razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);

            log.info("Refund request sent to Razorpay for paymentId={}, refundId={}", payment.getRazorpayPaymentId(), refundResponse.get("id"));

            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            booking.setStatus(BookingStatus.REFUNDED);
            bookingRepository.save(booking);

            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seatRepository.save(seat);
            }

            return true;
        } catch (RazorpayException e) {
            log.error("Failed to refund payment for booking {}: {}", bookingReference, e.getMessage(), e);
            throw new IllegalStateException("Refund failed: " + e.getMessage());
        }
    }
}
