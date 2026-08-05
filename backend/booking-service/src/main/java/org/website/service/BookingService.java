package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.BookingRequest;
import org.website.dto.BookingResponse;
import org.website.exception.ResourceNotFoundException;
import org.website.exception.SeatAlreadyBookedException;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.model.Show;
import org.website.model.User;
import org.website.repository.BookingRepository;
import org.website.repository.SeatRepository;
import org.website.repository.ShowRepository;
import org.website.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Booking Service - Manages all booking operations and seat reservations.
 * 
 * This service handles the core business logic for ticket bookings including:
 * - Creating new bookings (reserving seats for shows)
 * - Retrieving booking details and user's booking history
 * - Canceling bookings and releasing reserved seats
 * - Handling concurrency conflicts using optimistic locking
 * 
 * Concurrency Control:
 * The Seat entity uses @Version for optimistic locking. When multiple users
 * try to book the same seat simultaneously, the second/later attempts fail
 * with ObjectOptimisticLockingFailureException, which is converted to
 * SeatAlreadyBookedException for user-friendly error messages.
 * 
 * Seat Status Lifecycle:
 * AVAILABLE → (user selects) → RESERVED → (payment success) → BOOKED
 *         → (reservation expires) → AVAILABLE
 *         → (payment fails) → AVAILABLE
 * 
 * Booking Status Lifecycle:
 * PENDING → (payment verified) → COMPLETED
 *        → (user cancels) → CANCELLED
 *        → (expires after 5 min) → EXPIRED
 * 
 * Transaction Management:
 * All public methods are @Transactional to ensure ACID properties.
 * On failure, all seat updates and booking creation are rolled back.
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@Service
@Slf4j
@Transactional
public class BookingService {

    private BookingRepository bookingRepository;
    private SeatRepository seatRepository;
    private ShowRepository showRepository;
    private UserRepository userRepository;

    BookingService(BookingRepository bookingRepository, SeatRepository seatRepository, ShowRepository showRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new booking (reserve seats for a show).
     * 
     * This is the main booking operation. It:
     * 1. Validates user and show exist
     * 2. Reserves requested seats (marks as RESERVED)
     * 3. Calculates total amount based on seat count and show price
     * 4. Generates unique booking reference (BK-xxxxxxxxxx)
     * 5. Creates booking record with PENDING status
     * 6. Returns booking details to frontend
     * 
     * Concurrency Handling:
     * If another user books same seat simultaneously, ObjectOptimisticLockingFailureException
     * is caught and converted to SeatAlreadyBookedException with user-friendly message.
     * 
     * CRITICAL: Seats must be marked as RESERVED immediately to prevent
     * other users from selecting them in UI (via real-time socket updates).
     * 
     * @param userId The user making the booking (extracted from JWT)
     * @param request BookingRequest with showId and selectedSeats
     * 
     * @return BookingResponse with booking details including ID, reference, seats, amount
     * @throws ResourceNotFoundException if user or show not found (ERR006, ERR102)
     * @throws SeatAlreadyBookedException if seat already booked (ERR200)
     * @throws SeatAlreadyBookedException if concurrent booking detected (ERR204)
     * @throws IllegalArgumentException if seat validation fails (ERR400)
     * 
     * @see BookingResponse
     * @see BookingRequest
     */
    public BookingResponse createBooking(Long userId, BookingRequest request) {
        log.debug("Starting booking creation for user: {} on show: {}", userId, request.showId());
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Show show = showRepository.findById(request.showId())
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + request.showId()));

        List<Seat> selectedSeats = new ArrayList<>();
        long totalAmount = 0;

        try {
            for (Integer seatNumber : request.selectedSeats()) {
                Seat seat = seatRepository.findByShowIdAndSeatNumber(show.getId(), seatNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatNumber));

                if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
                    throw new SeatAlreadyBookedException("Seat " + seatNumber + " is already booked");
                }

                seat.setStatus(SeatStatus.RESERVED);
                seatRepository.save(seat);
                selectedSeats.add(seat);
                totalAmount += show.getPriceInCents();
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("[ERR204] Seat selection conflict due to concurrent booking for user: {}", userId);
            throw new SeatAlreadyBookedException("Someone else just booked these seats. Please try again.");
        }

        String bookingReference = "BK" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Booking booking = new Booking(user, show, bookingReference, BookingStatus.PENDING, totalAmount);
        booking.setSeats(selectedSeats);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("[BOOKING] Created: {} for user: {} with {} seats, amount: {}", 
                 bookingReference, user.getEmail(), selectedSeats.size(), totalAmount);

        return convertToDTO(savedBooking);
    }

    /**
     * Retrieve a specific booking by ID.
     * 
     * Fetches full booking details including show information and seat list.
     * Enforces user isolation: users can only access their own bookings.
     * 
     * @param bookingId The booking ID to retrieve
     * @param userId The requesting user (must own the booking)
     * 
     * @return BookingResponse with complete booking details
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own this booking (ERR002)
     */
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        log.debug("Retrieving booking: {} for user: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[ERR002] Unauthorized access attempt to booking: {} by user: {}", bookingId, userId);
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        return convertToDTO(booking);
    }

    /**
     * Retrieve a booking by its reference number.
     * 
     * Alternative lookup method using booking reference (BK-xxxxxxxxxx).
     * Useful when user has reference but not ID.
     * Enforces user isolation like getBookingById().
     * 
     * @param reference The unique booking reference string
     * @param userId The requesting user (must own the booking)
     * 
     * @return BookingResponse with complete booking details
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own this booking (ERR002)
     */
    public BookingResponse getBookingByReference(String reference, Long userId) {
        log.debug("Retrieving booking by reference: {} for user: {}", reference, userId);
        
        Booking booking = bookingRepository.findByBookingReference(reference)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[ERR002] Unauthorized access attempt to booking reference: {} by user: {}", reference, userId);
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        return convertToDTO(booking);
    }

    /**
     * Retrieve all bookings for a user.
     * 
     * Returns complete booking history for the user in descending chronological order.
     * Used for "My Bookings" page showing past and upcoming tickets.
     * 
     * @param userId The user ID to fetch bookings for
     * 
     * @return List<BookingResponse> with all user's bookings (empty if none)
     * @throws ResourceNotFoundException if user doesn't exist (ERR006)
     */
    public List<BookingResponse> getUserBookings(Long userId) {
        log.debug("Fetching all bookings for user: {}", userId);
        
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<BookingResponse> bookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(userId).stream()
            .map(this::convertToDTO)
            .toList();
        
        log.debug("Retrieved {} bookings for user: {}", bookings.size(), userId);
        return bookings;
    }

    /**
     * Cancel an existing booking and release reserved seats.
     * 
     * Cancels a PENDING booking and frees up all reserved seats (mark as AVAILABLE).
     * Only PENDING bookings can be cancelled; BOOKED/COMPLETED need refund processing.
     * 
     * Process:
     * 1. Verify booking exists
     * 2. Verify user owns the booking
     * 3. Verify booking is in PENDING state
     * 4. Release all reserved seats back to AVAILABLE
     * 5. Mark booking as CANCELLED
     * 6. Log cancellation
     * 
     * @param bookingId The booking ID to cancel
     * @param userId The requesting user (must own the booking)
     * 
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own this booking (ERR002)
     * @throws IllegalStateException if booking not in PENDING state (ERR203)
     */
    public void cancelBooking(Long bookingId, Long userId) {
        log.debug("Canceling booking: {} for user: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[ERR002] Unauthorized cancellation attempt for booking: {} by user: {}", bookingId, userId);
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        if (!BookingStatus.PENDING.equals(booking.getStatus())) {
            log.error("[ERR203] Cannot cancel booking in {} state: {}", booking.getStatus(), bookingId);
            throw new IllegalStateException("Can only cancel pending bookings");
        }

        // Release reserved seats back to AVAILABLE
        for (Seat seat : Optional.ofNullable(booking.getSeats()).orElse(List.of())) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
            log.debug("Released seat: {} from booking: {}", seat.getSeatNumber(), bookingId);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("[BOOKING] Cancelled: {} by user: {}", booking.getBookingReference(), userId);
    }

    /**
     * Retrieve all bookings (admin only).
     * 
     * Returns complete list of all bookings in the system for analytics.
     * Used by admin dashboard to display booking statistics and revenue.
     * 
     * SECURITY: This method should only be called with @PreAuthorize("hasRole('ADMIN')") check.
     * 
     * @return List<BookingResponse> with all bookings
     */
    public List<BookingResponse> getAllBookings() {
        log.info("Admin retrieving all bookings for analytics");
        List<BookingResponse> bookings = bookingRepository.findAll().stream()
            .map(this::convertToDTO)
            .toList();
        log.debug("Retrieved {} total bookings", bookings.size());
        return bookings;
    }

    /**
     * Convert Booking entity to BookingResponse DTO.
     * 
     * Private helper method for transforming JPA entity to API response.
     * Extracts seat numbers from Seat entities and formats response data.
     * 
     * @param booking The Booking entity to convert
     * @return BookingResponse DTO suitable for API response
     */
    private BookingResponse convertToDTO(Booking booking) {
        List<Integer> seatNumbers = Optional.ofNullable(booking.getSeats()).orElse(List.of()).stream()
            .map(Seat::getSeatNumber)
            .toList();

        return new BookingResponse(
            booking.getId(),
            booking.getBookingReference(),
            booking.getStatus().toString(),
            booking.getTotalAmountInCents(),
            booking.getBookingTime(),
            booking.getPaymentOrderId(),
            booking.getShow().getMovie().getTitle(),
            booking.getShow().getShowTime(),
            booking.getShow().getTheater().getName(),
            seatNumbers
        );
    }
}
