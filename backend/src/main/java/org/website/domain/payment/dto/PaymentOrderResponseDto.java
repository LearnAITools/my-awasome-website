package org.website.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Razorpay payment order response.
 * 
 * Domain: Payment
 * Type: Response
 * 
 * Response from Razorpay when order is created.
 * Contains order ID and amount for checkout modal.
 * 
 * Example Response:
 * {
 *   "orderId": "order_MBD7ukQ69xz6qJ",
 *   "amount": 75000,
 *   "currency": "INR",
 *   "keyId": "rzp_test_..."
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponseDto {
    private String orderId;
    private Long amount;
    private String currency;
    private String keyId;
}
