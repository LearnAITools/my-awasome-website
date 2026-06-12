package org.website.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for verifying Razorpay payment.
 * 
 * Domain: Payment
 * Type: Request
 * 
 * Verification request with signature details from Razorpay.
 * Uses HMAC-SHA256 signature verification for security.
 * 
 * Example:
 * {
 *   "bookingReference": "BK-ABC123DEF",
 *   "razorpayOrderId": "order_MBD7ukQ69xz6qJ",
 *   "razorpayPaymentId": "pay_MBD7vg4jKJFtQe",
 *   "razorpaySignature": "9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequestDto {
    private String bookingReference;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    /**
     * Validate all required fields are present
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking reference is required");
        }
        if (razorpayOrderId == null || razorpayOrderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Razorpay order ID is required");
        }
        if (razorpayPaymentId == null || razorpayPaymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Razorpay payment ID is required");
        }
        if (razorpaySignature == null || razorpaySignature.trim().isEmpty()) {
            throw new IllegalArgumentException("Razorpay signature is required");
        }
    }
}
