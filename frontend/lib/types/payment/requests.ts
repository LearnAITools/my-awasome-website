/**
 * Payment service request DTOs
 * Matches backend payment-service DTOs
 */

/**
 * Create payment order request
 * Matches PaymentOrderRequest in backend payment-service
 */
export interface PaymentOrderRequest {
  bookingReference: string
}

/**
 * Payment verification request (Razorpay)
 * Matches PaymentVerifyRequest in backend
 */
export interface PaymentVerifyRequest {
  razorpayPaymentId: string
  razorpayOrderId: string
  razorpaySignature: string
}

/**
 * Refund request
 * Matches RefundRequest in backend
 */
export interface RefundRequest {
  bookingReference: string
}

/**
 * Payment order request for inter-service communication
 * Matches PaymentOrderRequestDto in backend common module
 */
export interface PaymentOrderRequestDto {
  bookingReference: string
  userId: number
  amountInCents: number
  currency: string
  bookingStatus: string
}
