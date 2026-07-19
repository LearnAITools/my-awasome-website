package org.website.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating Razorpay payment order.
 * 
 * Domain: Payment
 * Type: Request
 * 
 * Request to create payment order on Razorpay.
 * Includes booking reference to link to existing booking.
 * 
 * Example:
 * {
 *   "bookingReference": "BK-ABC123DEF"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderRequestDto {
    private String bookingReference;

    /**
     * Validate request data
     * @throws IllegalArgumentException if invalid
     */
    public void validate() {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking reference is required");
        }
    }
}
