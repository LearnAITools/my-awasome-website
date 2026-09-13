/**
 * Booking service request DTOs
 * Matches backend booking-service DTOs
 */

/**
 * Create booking request payload
 * Matches BookingRequest in backend
 */
export interface BookingRequest {
  showId: number
  selectedSeats: number[]
}

/**
 * Request to get show details and seats
 */
export interface GetShowRequest {
  showId: string | number
}

/**
 * Request to get user's bookings
 */
export interface GetUserBookingsRequest {
  // No parameters needed, uses auth token
}

/**
 * Booking cancellation request
 */
export interface CancelBookingRequest {
  bookingReference: string
}
