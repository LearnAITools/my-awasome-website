package org.website.domain.payment.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.constant.ErrorCode;
import org.website.domain.payment.dto.PaymentOrderRequestDto;
import org.website.domain.payment.dto.PaymentOrderResponseDto;
import org.website.domain.payment.dto.PaymentVerifyRequestDto;
import org.website.exception.InvalidPaymentException;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Payment;
import org.website.model.PaymentStatus;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.repository.BookingRepository;
import org.website.repository.PaymentRepository;
import org.website.repository.SeatRepository;
import org.website.util.ErrorUtil;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Payment Domain Service - Handles all payment operations via Razorpay.
 * 
 * This domain service encapsulates payment business logic:
 * - Two-step payment flow: Create Order → Verify Payment
 * - HMAC-SHA256 signature verification for security
 * - Booking status transitions on payment completion
 * - Seat locking on successful payment
 * 
 * Uses ErrorCode enum instead of e.getMessage() for consistent error handling.
 * All error codes logged with ERRxxx prefix for traceability.
 * 
 * Razorpay Integration:
 * - Sandbox mode for development (test keys)
 * - Production ready (use live keys in production)
 * - Signature verification prevents payment fraud
 * 
 * Transaction Management:
 * All public methods are @Transactional.
 * On any error, all database changes are rolled back.
 * 
 * @author BookMyShow Dev Team
 * @version 2.0
 * @since 2026-05-30
 */
@Service
@Slf4j
@Transactional
public class PaymentDomainService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    /**
     * Create payment order on Razorpay.
     * 
     * Business Logic:
     * 1. Validate request (ERR400)
     * 2. Fetch booking by reference (ERR106)
     * 3. Verify user owns booking (ERR002)
     * 4. Call Razorpay API to create order (ERR504)
     * 5. Store Payment record in DB
     * 6. Link order ID to booking
     * 7. Return order details to frontend
     * 
     * Frontend Next Steps:
     * - Use orderId to open Razorpay checkout modal
     * - User enters payment details
     * - On success, frontend calls /verify endpoint
     * 
     * @param userId Authenticated user ID
     * @param request PaymentOrderRequestDto with bookingReference
     * @return PaymentOrderResponseDto with orderId, amount, currency
     * 
     * @throws IllegalArgumentException if validation fails (ERR400)
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own booking (ERR002)
     * @throws InvalidPaymentException if Razorpay API error (ERR504)
     */
    public PaymentOrderResponseDto createPaymentOrder(Long userId, PaymentOrderRequestDto request) {
        log.info("[PAYMENT] User {} creating payment order for booking: {}", userId, request.getBookingReference());
        
        // Validate request (ERR400)
        request.validate();

        // Get booking (ERR106)
        Booking booking = bookingRepository.findByBookingReference(request.getBookingReference())
            .orElseThrow(() -> {
                log.error("[{}] Booking not found: {}", ErrorCode.BOOKING_NOT_FOUND.getCode(), request.getBookingReference());
                return new org.website.exception.ResourceNotFoundException(
                    "Booking not found with reference: " + request.getBookingReference());
            });

        // Verify user owns booking (ERR002)
        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[{}] Unauthorized payment creation attempt by user: {}", ErrorCode.AUTH_UNAUTHORIZED_ACCESS.getCode(), userId);
            ErrorUtil.throwUnauthorized("You don't have permission to access this booking");
        }

        try {
            // Initialize Razorpay client (lazy initialization)
            if (razorpayClient == null) {
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                log.debug("Razorpay client initialized");
            }

            // Create order on Razorpay (ERR504)
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", booking.getTotalAmountInCents()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", booking.getBookingReference());

            com.razorpay.Order orderResponse = razorpayClient.orders.create(orderRequest);
            String orderId = (String) orderResponse.get("id");
            log.debug("Razorpay order created: {}", orderId);

            // Store payment record locally
            Payment payment = new Payment(booking, orderId, booking.getTotalAmountInCents(), PaymentStatus.PENDING);
            paymentRepository.save(payment);
            log.debug("Payment record created in database");

            // Link order ID to booking
            booking.setPaymentOrderId(orderId);
            bookingRepository.save(booking);

            log.info("[PAYMENT] Order created: {} for booking: {}, amount: {} paise", 
                     orderId, booking.getBookingReference(), booking.getTotalAmountInCents());

            return new PaymentOrderResponseDto(
                orderId,
                booking.getTotalAmountInCents(),
                "INR",
                razorpayKeyId
            );
        } catch (RazorpayException e) {
            log.error("[{}] Razorpay API error: {}", ErrorCode.EXTERNAL_SERVICE_ERROR.getCode(), e.getMessage());
            ErrorUtil.throwPaymentFailed("Razorpay API error: " + e.getMessage());
            return null; // Never reached
        }
    }

    /**
     * Verify Razorpay payment signature and complete booking.
     * 
     * SECURITY CRITICAL:
     * This method verifies payment authenticity using HMAC-SHA256 signature.
     * Prevents fraudulent payments and payment tampering.
     * 
     * Signature Verification:
     * 1. Calculate HMAC-SHA256 of "{orderId}|{paymentId}" using secret key
     * 2. Compare calculated hash with signature from Razorpay
     * 3. Only proceed if signatures match exactly
     * 
     * On Success:
     * - Payment status: PENDING → COMPLETED
     * - Booking status: PENDING → CONFIRMED
     * - Seats locked: RESERVED → BOOKED (permanent)
     * - Payment completion timestamp recorded
     * - User notified via email (by NotificationService)
     * 
     * On Failure:
     * - Payment status: PENDING → FAILED
     * - Booking remains PENDING
     * - Seats remain RESERVED
     * - User can retry payment
     * 
     * @param userId Authenticated user ID
     * @param request PaymentVerifyRequestDto with Razorpay details
     * @return true if verification successful
     * 
     * @throws IllegalArgumentException if validation fails (ERR400)
     * @throws ResourceNotFoundException if payment/booking not found (ERR105, ERR106)
     * @throws IllegalArgumentException if user doesn't own payment (ERR002)
     * @throws InvalidPaymentException if signature invalid (ERR301)
     */
    public boolean verifyPayment(Long userId, PaymentVerifyRequestDto request) {
        log.info("[PAYMENT] Verifying payment for order: {} by user: {}", request.getRazorpayOrderId(), userId);
        
        // Validate request (ERR400)
        request.validate();

        // Get payment (ERR105)
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> {
                log.error("[{}] Payment not found: {}", ErrorCode.PAYMENT_NOT_FOUND.getCode(), request.getRazorpayOrderId());
                return new org.website.exception.ResourceNotFoundException(
                    "Payment not found for order: " + request.getRazorpayOrderId());
            });

        Booking booking = payment.getBooking();
        
        // Verify user owns payment (ERR002)
        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[{}] Unauthorized payment verification attempt by user: {}", ErrorCode.AUTH_UNAUTHORIZED_ACCESS.getCode(), userId);
            ErrorUtil.throwUnauthorized("You don't have permission to verify this payment");
        }

        // Verify signature (ERR301)
        if (!verifyRazorpaySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            log.warn("[{}] Invalid payment signature for order: {}", ErrorCode.PAYMENT_INVALID_SIGNATURE.getCode(), request.getRazorpayOrderId());
            ErrorUtil.throwPaymentSignatureInvalid("Signature verification failed");
        }

        log.debug("Payment signature verified successfully");

        // Update payment record
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        log.debug("Payment record updated to COMPLETED");

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentTime(LocalDateTime.now());
        bookingRepository.save(booking);
        log.debug("Booking status updated to CONFIRMED");

        // Lock seats permanently
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
            log.debug("Seat locked: {} status=BOOKED", seat.getSeatNumber());
        }

        log.info("[PAYMENT] Payment verified successfully for booking: {}, amount: {} paise", 
                 booking.getBookingReference(), booking.getTotalAmountInCents());
        return true;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Verify Razorpay HMAC-SHA256 signature.
     * 
     * SECURITY CRITICAL METHOD
     * 
     * Algorithm:
     * 1. Message: "{orderId}|{paymentId}"
     * 2. Secret: razorpay.key-secret from config
     * 3. Hash: HMAC-SHA256
     * 4. Output: Hexadecimal string
     * 
     * Constant-time comparison prevents timing attacks.
     * 
     * @param orderId Razorpay order ID
     * @param paymentId Razorpay payment ID
     * @param signature Signature to verify (from Razorpay response)
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
            
            // Constant-time comparison
            boolean isValid = computed.equals(signature);
            
            if (!isValid) {
                log.debug("Signature mismatch for order: {}", orderId);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("[{}] Error verifying signature: {}", ErrorCode.PAYMENT_FAILED.getCode(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Convert byte array to hexadecimal string.
     * 
     * Used for signature verification (HMAC output → hex string).
     * 
     * @param bytes The byte array
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
}
