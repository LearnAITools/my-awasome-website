/**
 * Payment service response DTOs
 * Matches backend payment-service response DTOs
 */

/**
 * Payment status enum
 */
export enum PaymentStatus {
  INITIATED = 'INITIATED',
  PENDING = 'PENDING',
  CAPTURED = 'CAPTURED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
  PARTIALLY_REFUNDED = 'PARTIALLY_REFUNDED',
}

/**
 * Payment order response for creating orders
 * Matches PaymentOrderResponse in backend payment-service
 */
export interface PaymentOrderResponse {
  orderId: string
  amountInCents: number
  currency: string
  keyId: string // Razorpay key for frontend
}

/**
 * Payment order response DTO for inter-service communication
 * Matches PaymentOrderResponseDto in backend common module
 */
export interface PaymentOrderResponseDto {
  orderId: string
  bookingReference: string
  amountInCents: number
  currency: string
  status: PaymentStatus
}

/**
 * Payment verification response
 */
export interface PaymentVerificationResponse {
  success: boolean
  message: string
  paymentId?: string
  orderId?: string
  bookingReference?: string
}

/**
 * Refund response
 */
export interface RefundResponse {
  success: boolean
  message: string
  refundId?: string
  bookingReference?: string
  refundAmountInCents?: number
}

/**
 * Payment transaction details
 */
export interface PaymentTransaction {
  paymentId: string
  orderId: string
  amount: number
  currency: string
  status: PaymentStatus
  createdAt: string
  updatedAt: string
}
