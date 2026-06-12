package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.BookingRequest;
import org.website.dto.BookingResponse;
import org.website.service.BookingService;
import java.util.List;

/**
 * Booking Controller - Handles movie ticket booking operations.
 * 
 * This controller manages all booking-related operations including:
 * - Creating new bookings (reserving seats)
 * - Retrieving booking details and history
 * - Canceling bookings
 * 
 * Endpoints:
 * - POST /api/bookings - Create new booking
 * - GET /api/bookings/{id} - Get booking details
 * - GET /api/bookings/reference/{ref} - Get booking by reference
 * - GET /api/bookings/user - Get user's bookings
 * - DELETE /api/bookings/{id} - Cancel booking
 * 
 * Security: ALL endpoints require authentication (JWT token).
 * Users can only access their own bookings (user isolation enforced).
 * 
 * Concurrency Handling: Uses optimistic locking to prevent double-booking.
 * If two users try to book the same seat simultaneously, second attempt fails.
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private BookingService bookingService;

    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Create a new booking (reserve seats for a show).
     * 
     * Reserves selected seats for a movie show. Seats are marked as RESERVED and
     * held for 5 minutes. After payment completion, they transition to BOOKED.
     * If payment fails or timeout occurs, reservation is automatically cancelled.
     * 
     * CRITICAL: Uses optimistic locking to prevent concurrent booking conflicts.
     * If another user books same seat simultaneously, request returns error ERR204.
     * 
     * @param userId Extracted from JWT token via RequestAttribute
     * @param request BookingRequest containing:
     *                - showId (required, must exist)
     *                - selectedSeats (required, list of seat identifiers like "A1", "B2")
     *                - quantity (required, must match selectedSeats count)
     * 
     * @return ResponseEntity containing BookingResponse with:
     *         - bookingId, bookingReference, showId, seats, totalPrice, status
     * @return HTTP 201 CREATED with booking details
     * @return HTTP 404 NOT_FOUND if show doesn't exist (error_code: ERR102)
     * @return HTTP 409 CONFLICT if seat already booked (error_code: ERR200)
     * @return HTTP 409 CONFLICT if concurrent booking conflict (error_code: ERR204)
     * @return HTTP 400 BAD_REQUEST if validation fails (error_code: ERR400)
     * 
     * @throws SeatAlreadyBookedException if seat is already booked/reserved
     * @throws ResourceNotFoundException if show not found
     * @throws ObjectOptimisticLockingFailureException if concurrent booking detected
     * 
     * Example Request:
     * {
     *   "showId": 1,
     *   "selectedSeats": ["A1", "A2", "B1"],
     *   "quantity": 3
     * }
     * 
     * Example Response (201):
     * {
     *   "bookingId": 101,
     *   "bookingReference": "BK-20260530-001",
     *   "showId": 1,
     *   "selectedSeats": ["A1", "A2", "B1"],
     *   "quantity": 3,
     *   "totalPrice": 75000,
     *   "status": "RESERVED",
     *   "expiryTime": "2026-05-30T10:35:00"
     * }
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestAttribute("userId") Long userId,
            @RequestBody BookingRequest request) {
        log.info("Creating booking for user: {} for show: {}", userId, request.getShowId());
        BookingResponse response = bookingService.createBooking(userId, request);
        log.info("Booking created with reference: {}", response.getBookingReference());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve booking details by booking ID.
     * 
     * Fetches complete details of a specific booking.
     * Enforces user isolation: users can only view their own bookings.
     * 
     * @param id The booking ID to retrieve
     * @param userId Extracted from JWT token (used to verify ownership)
     * 
     * @return ResponseEntity containing BookingResponse with all booking details
     * @return HTTP 200 OK with booking details
     * @return HTTP 404 NOT_FOUND if booking not found (error_code: ERR106)
     * @return HTTP 403 FORBIDDEN if booking belongs to different user (error_code: ERR002)
     * 
     * @throws ResourceNotFoundException if booking not found
     * @throws UnauthorizedException if user doesn't own this booking
     * 
     * Example Response (200):
     * {
     *   "bookingId": 101,
     *   "bookingReference": "BK-20260530-001",
     *   "showId": 1,
     *   "movieTitle": "Kalki 2898 AD",
     *   "theaterName": "AMB Cinemas",
     *   "showTime": "2026-06-01T18:00:00",
     *   "selectedSeats": ["A1", "A2", "B1"],
     *   "quantity": 3,
     *   "totalPrice": 75000,
     *   "status": "BOOKED",
     *   "bookingDate": "2026-05-30T10:30:00",
     *   "expiryTime": null
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        log.info("Fetching booking: {} for user: {}", id, userId);
        BookingResponse response = bookingService.getBookingById(id, userId);
        log.debug("Booking found: {}", response.getBookingReference());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve booking details by reference number.
     * 
     * Alternative way to fetch booking using the booking reference (BK-20260530-001).
     * Useful when user has the reference but not the ID.
     * 
     * @param reference The unique booking reference string
     * @param userId Extracted from JWT token (used to verify ownership)
     * 
     * @return ResponseEntity containing BookingResponse with all booking details
     * @return HTTP 200 OK with booking details
     * @return HTTP 404 NOT_FOUND if booking not found (error_code: ERR106)
     * @return HTTP 403 FORBIDDEN if booking belongs to different user (error_code: ERR002)
     * 
     * @throws ResourceNotFoundException if booking not found
     * @throws UnauthorizedException if user doesn't own this booking
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(
            @PathVariable String reference,
            @RequestAttribute("userId") Long userId) {
        log.info("Fetching booking by reference: {} for user: {}", reference, userId);
        BookingResponse response = bookingService.getBookingByReference(reference, userId);
        log.debug("Booking found: {}", reference);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all bookings for the authenticated user.
     * 
     * Returns complete booking history for the current user.
     * Useful for "My Bookings" page showing past and upcoming bookings.
     * 
     * @param userId Extracted from JWT token
     * 
     * @return ResponseEntity containing List<BookingResponse> with all user's bookings
     * @return HTTP 200 OK with bookings list (empty list if no bookings)
     * 
     * Example Response (200):
     * [
     *   {
     *     "bookingId": 101,
     *     "bookingReference": "BK-20260530-001",
     *     "movieTitle": "Kalki 2898 AD",
     *     "showTime": "2026-06-01T18:00:00",
     *     "selectedSeats": ["A1", "A2"],
     *     "totalPrice": 50000,
     *     "status": "BOOKED",
     *     "bookingDate": "2026-05-30T10:30:00"
     *   },
     *   {
     *     "bookingId": 102,
     *     "bookingReference": "BK-20260527-005",
     *     "movieTitle": "Inception",
     *     "showTime": "2026-05-28T20:30:00",
     *     "selectedSeats": ["C5"],
     *     "totalPrice": 25000,
     *     "status": "COMPLETED",
     *     "bookingDate": "2026-05-27T14:15:00"
     *   }
     * ]
     */
    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @RequestAttribute("userId") Long userId) {
        log.info("Fetching all bookings for user: {}", userId);
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        log.debug("Found {} bookings for user", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /**
     * Cancel an existing booking.
     * 
     * Cancels a booking and frees up reserved seats. 
     * Only RESERVED bookings can be cancelled; BOOKED bookings require refund processing.
     * Seat status transitions from RESERVED back to AVAILABLE.
     * 
     * @param id The booking ID to cancel
     * @param userId Extracted from JWT token (must own the booking)
     * 
     * @return ResponseEntity with no content
     * @return HTTP 204 NO_CONTENT on successful cancellation
     * @return HTTP 404 NOT_FOUND if booking not found (error_code: ERR106)
     * @return HTTP 403 FORBIDDEN if booking belongs to different user (error_code: ERR002)
     * @return HTTP 409 CONFLICT if booking in invalid state for cancellation (error_code: ERR203)
     * 
     * @throws ResourceNotFoundException if booking not found
     * @throws UnauthorizedException if user doesn't own this booking
     * @throws IllegalStateException if booking cannot be cancelled in current state
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        log.info("Canceling booking: {} for user: {}", id, userId);
        bookingService.cancelBooking(id, userId);
        log.info("Booking canceled successfully: {}", id);
        return ResponseEntity.noContent().build();
    }
}
