/**
 * Booking service response DTOs
 * Matches backend booking-service response DTOs
 */

/**
 * Booking status enum
 */
export enum BookingStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  EXPIRED = 'EXPIRED',
  REFUNDED = 'REFUNDED',
}

/**
 * Seat status enum
 */
export enum SeatStatus {
  AVAILABLE = 'AVAILABLE',
  RESERVED = 'RESERVED',
  BLOCKED = 'BLOCKED',
  SOLD = 'SOLD',
}

/**
 * Seat information in a show
 */
export interface Seat {
  seatNumber: number
  status: SeatStatus
  price?: number
}

/**
 * Show details with seat information
 */
export interface ShowDetails {
  showId: number
  movieTitle: string
  movieId?: string | number
  showTime: string // ISO 8601 datetime
  theaterName: string
  totalSeats: number
  availableSeats: number
  seats: Seat[]
  pricePerSeat: number
}

/**
 * Booking response
 * Matches BookingResponse in backend
 */
export interface BookingResponse {
  id: number
  bookingReference: string
  status: BookingStatus
  totalAmountInCents: number
  bookingTime: string // ISO 8601 datetime
  paymentOrderId?: string
  movieTitle: string
  showTime: string // ISO 8601 datetime
  theaterName: string
  seatNumbers: number[]
}

/**
 * User's booking list item
 */
export interface UserBooking extends BookingResponse {
  // Can extend with additional fields if needed
}

/**
 * Payment status update for booking
 * Used for inter-service communication
 * Matches BookingPaymentStatusUpdateDto in backend common
 */
export interface BookingPaymentStatusUpdate {
  bookingReference: string
  paymentId: string
  orderId: string
  paymentStatus: string
  amountInCents: number
  updatedAt: string // ISO 8601 datetime
}

/**
 * Acknowledgment for payment status update
 * Matches BookingPaymentStatusAckDto in backend
 */
export interface BookingPaymentStatusAck {
  bookingReference: string
  accepted: boolean
  status: string
}
