/**
 * Payment Domain Package.
 * 
 * This domain encapsulates all payment processing and verification logic.
 * Integrates with Razorpay for payment processing in production.
 * 
 * Layers:
 * - dto: Data Transfer Objects for payment operations
 *   * PaymentOrderRequestDto: Request to create Razorpay order
 *   * PaymentOrderResponseDto: Response with order details
 *   * PaymentVerifyRequestDto: Signature verification request
 * 
 * - service: Payment processing logic
 *   * PaymentDomainService: Two-step payment flow
 *   * Step 1: createPaymentOrder() - Create Razorpay order
 *   * Step 2: verifyPayment() - Verify HMAC-SHA256 signature
 *   * SECURITY CRITICAL: Signature verification prevents fraud
 * 
 * - controller: HTTP endpoints
 *   * POST /api/payments/order - Create payment order
 *   * POST /api/payments/verify - Verify payment
 * 
 * - repository: Database access layer
 *   * Interface for Payment entity CRUD operations
 *   * Custom queries: findByRazorpayOrderId, findByBookingId, etc.
 * 
 * - model: Domain entity
 *   * Payment entity with JPA annotations
 *   * PaymentStatus enum: PENDING, COMPLETED, FAILED
 * 
 * Payment Flow:
 * 1. Frontend: GET /order → Backend: Create Razorpay order
 * 2. Frontend: Open Razorpay checkout modal
 * 3. User: Enter payment details
 * 4. Razorpay: Process payment
 * 5. Frontend: POST /verify with order/payment IDs
 * 6. Backend: Verify HMAC-SHA256 signature
 * 7. On success: Booking CONFIRMED, Seats BOOKED
 * 
 * Error Codes:
 * - ERR105: Payment not found
 * - ERR301: Invalid payment signature
 * - ERR300: Payment failed
 * - ERR504: Razorpay API error
 * 
 * @since 2026-05-30
 */
package org.website.domain.payment;
