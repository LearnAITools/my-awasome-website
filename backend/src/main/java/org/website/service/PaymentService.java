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

@Service
@Slf4j
@Transactional
public class PaymentService {
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

    public PaymentOrderResponse createPaymentOrder(Long userId, PaymentOrderRequest request) {
        Booking booking = bookingRepository.findByBookingReference(request.getBookingReference())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + request.getBookingReference()));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        try {
            if (razorpayClient == null) {
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            }

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", booking.getTotalAmountInCents());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", booking.getBookingReference());

            JSONObject orderResponse = razorpayClient.Orders.create(orderRequest);
            String orderId = orderResponse.getString("id");

            Payment payment = new Payment(booking, orderId, booking.getTotalAmountInCents(), PaymentStatus.PENDING);
            paymentRepository.save(payment);

            booking.setPaymentOrderId(orderId);
            bookingRepository.save(booking);

            log.info("Payment order created: {} for booking: {}", orderId, booking.getBookingReference());

            return new PaymentOrderResponse(
                orderId,
                booking.getTotalAmountInCents(),
                "INR",
                razorpayKeyId
            );
        } catch (RazorpayException e) {
            log.error("Razorpay exception: {}", e.getMessage());
            throw new InvalidPaymentException("Failed to create payment order: " + e.getMessage());
        }
    }

    public boolean verifyPayment(Long userId, PaymentVerifyRequest request) {
        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + request.getRazorpayOrderId()));

        Booking booking = payment.getBooking();
        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to payment");
        }

        // Verify signature
        if (!verifyRazorpaySignature(request.getRazorpayOrderId(), request.getRazorpayPaymentId(), request.getRazorpaySignature())) {
            log.warn("Invalid Razorpay signature for order: {}", request.getRazorpayOrderId());
            throw new InvalidPaymentException("Invalid payment signature");
        }

        // Update payment record
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentTime(LocalDateTime.now());
        bookingRepository.save(booking);

        // Update seat status
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }

        log.info("Payment verified successfully for booking: {}", booking.getBookingReference());
        return true;
    }

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            String message = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String computed = bytesToHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature: {}", e.getMessage());
            return false;
        }
    }

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
