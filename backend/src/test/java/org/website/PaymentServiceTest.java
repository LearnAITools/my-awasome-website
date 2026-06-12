package org.website;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.website.dto.PaymentOrderRequest;
import org.website.dto.PaymentOrderResponse;
import org.website.dto.PaymentVerifyRequest;
import org.website.exception.InvalidPaymentException;
import org.website.exception.ResourceNotFoundException;
import org.website.model.*;
import org.website.repository.*;
import org.website.service.PaymentService;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Unit tests for PaymentService.
 * Tests payment order creation and verification flow.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests")
public class PaymentServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private Movie testMovie;
    private Theater testTheater;
    private Show testShow;
    private Seat testSeat;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password", "Test User", UserRole.ROLE_USER);
        testUser.setId(1L);

        ReflectionTestUtils.setField(paymentService, "razorpayKeyId", "test_key_id");
        ReflectionTestUtils.setField(paymentService, "razorpayKeySecret", "test_secret");

        testMovie = new Movie("Test Movie", "A test movie", "Action", 120, "English", 
                "https://example.com/poster.jpg", "2024-01-01", 8.5);
        testMovie.setId(1L);

        testTheater = new Theater("Test Theater", "Test City", "123 Test St", 5);
        testTheater.setId(1L);

        testShow = new Show(testMovie, testTheater, LocalDateTime.now().plusDays(1), 100, 50000L, 1);
        testShow.setId(1L);

        testSeat = new Seat(testShow, 1, 1, 1, SeatStatus.AVAILABLE);
        testSeat.setId(1L);

        testBooking = new Booking(testUser, testShow, "BK1234567890", BookingStatus.PENDING, 100000L);
        testBooking.setId(1L);
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    void testCreateOrderBookingNotFound() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest("BK_INVALID");

        when(bookingRepository.findByBookingReference("BK_INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.createPaymentOrder(1L, request);
        });
    }

    @Test
    @DisplayName("Should throw exception for unauthorized access to booking")
    void testCreateOrderUnauthorizedAccess() {
        // Arrange
        PaymentOrderRequest request = new PaymentOrderRequest("BK1234567890");

        User otherUser = new User("other@example.com", "password", "Other User", UserRole.ROLE_USER);
        otherUser.setId(999L);

        when(bookingRepository.findByBookingReference("BK1234567890")).thenReturn(Optional.of(testBooking));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.createPaymentOrder(999L, request);
        });
    }

    @Test
    @DisplayName("Should throw exception when payment not found during verification")
    void testVerifyPaymentNotFound() {
        // Arrange
        PaymentVerifyRequest request = new PaymentVerifyRequest();
        request.setRazorpayOrderId("INVALID_ORDER_ID");

        when(paymentRepository.findByRazorpayOrderId("INVALID_ORDER_ID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.verifyPayment(1L, request);
        });
    }

    @Test
    @DisplayName("Should verify payment with valid signature")
    void testVerifyPaymentWithValidSignature() throws Exception {
        // Arrange - Compute a valid HMAC signature using known secret
        testBooking.setSeats(Collections.singletonList(testSeat));
        Payment payment = new Payment(testBooking, "ORDER_ID_123", 100000L, PaymentStatus.PENDING);
        payment.setId(1L);

        PaymentVerifyRequest request = new PaymentVerifyRequest();
        request.setRazorpayOrderId("ORDER_ID_123");
        request.setRazorpayPaymentId("PAY_ID_123");
        request.setRazorpaySignature(computeHmac("ORDER_ID_123|PAY_ID_123", "test_secret"));

        when(paymentRepository.findByRazorpayOrderId("ORDER_ID_123")).thenReturn(Optional.of(payment));

        // Act
        boolean verified = paymentService.verifyPayment(1L, request);

        // Assert
        assertTrue(verified);
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("Should fail verification with invalid signature")
    void testVerifyPaymentWithInvalidSignature() {
        // Arrange
        Payment payment = new Payment(testBooking, "ORDER_ID_123", 100000L, PaymentStatus.PENDING);
        payment.setId(1L);

        PaymentVerifyRequest request = new PaymentVerifyRequest();
        request.setRazorpayOrderId("ORDER_ID_123");
        request.setRazorpayPaymentId("PAY_ID_123");
        request.setRazorpaySignature("invalid_signature");

        when(paymentRepository.findByRazorpayOrderId("ORDER_ID_123")).thenReturn(Optional.of(payment));

        // Act & Assert - Should throw InvalidPaymentException due to invalid signature
        // (Implementation depends on the actual signature verification logic)
        assertThrows(InvalidPaymentException.class, () -> {
            paymentService.verifyPayment(1L, request);
        });
    }

    @Test
    @DisplayName("Should throw exception for unauthorized payment verification")
    void testVerifyPaymentUnauthorized() {
        // Arrange
        Payment payment = new Payment(testBooking, "ORDER_ID_123", 100000L, PaymentStatus.PENDING);
        payment.setId(1L);

        PaymentVerifyRequest request = new PaymentVerifyRequest();
        request.setRazorpayOrderId("ORDER_ID_123");

        when(paymentRepository.findByRazorpayOrderId("ORDER_ID_123")).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.verifyPayment(999L, request);
        });
    }

    private String computeHmac(String message, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
