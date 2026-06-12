package org.website.domain.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.constant.ErrorCode;
import org.website.domain.booking.dto.BookingRequestDto;
import org.website.domain.booking.dto.BookingResponseDto;
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
import org.website.util.ErrorUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Booking Service - Domain-driven service for booking operations.
 * 
 * This service encapsulates ALL booking business logic in the booking domain.
 * Ensures single responsibility, SOLID principles, and proper error handling.
 * 
 * All methods use ErrorCode enum instead of e.getMessage() for consistency.
 * 
 * Key Responsibilities:
 * - Create bookings with seat reservations
 * - Retrieve bookings (by ID, reference, or user)
 * - Cancel bookings and release seats
 * - Enforce optimistic locking for concurrency control
 * - User isolation (users only access own bookings)
 * 
 * Transaction Management:
 * All public methods are @Transactional to ensure ACID properties.
 * Failures automatically roll back all database changes.
 * 
 * @author BookMyShow Dev Team
 * @version 2.0
 * @since 2026-05-30
 */
@Service
@Slf4j
@Transactional
public class BookingDomainService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create a new booking with seat reservations.
     * 
     * Business Logic:
     * 1. Validate user exists (ERR006)
     * 2. Validate show exists (ERR102)
     * 3. Validate and reserve each seat (ERR200, ERR204)
     * 4. Calculate total amount (seats × show price)
     * 5. Generate unique booking reference
     * 6. Create booking in PENDING status
     * 7. Return booking details
     * 
     * Concurrency Handling:
     * If another user books same seat simultaneously:
     * - ObjectOptimisticLockingFailureException caught
     * - Converted to SeatAlreadyBookedException (ERR204)
     * - User gets friendly error: "Someone else booked these seats"
     * 
     * @param userId The authenticated user ID
     * @param request BookingRequestDto with showId and selectedSeats
     * @return BookingResponseDto with booking details
     * 
     * @throws ResourceNotFoundException if user/show not found (ERR006, ERR102)
     * @throws SeatAlreadyBookedException if seat booked/reserved (ERR200, ERR204)
     * @throws IllegalArgumentException if validation fails (ERR400)
     */
    public BookingResponseDto createBooking(Long userId, BookingRequestDto request) {
        log.info("[BOOKING] User {} creating booking for show {}", userId, request.getShowId());
        
        // Validate request
        request.validate();
        
        // Get user (ERR006)
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("[{}] User not found: {}", ErrorCode.AUTH_USER_NOT_FOUND.getCode(), userId);
                return new org.website.exception.ResourceNotFoundException(
                    "User not found with ID: " + userId);
            });

        // Get show (ERR102)
        Show show = showRepository.findById(request.getShowId())
            .orElseThrow(() -> {
                log.error("[{}] Show not found: {}", ErrorCode.SHOW_NOT_FOUND.getCode(), request.getShowId());
                return new org.website.exception.ResourceNotFoundException(
                    "Show not found with ID: " + request.getShowId());
            });

        List<Seat> selectedSeats = new ArrayList<>();
        long totalAmount = 0;

        try {
            // Reserve each seat (ERR200)
            for (Integer seatNumber : request.getSelectedSeats()) {
                Seat seat = seatRepository.findByShowIdAndSeatNumber(show.getId(), seatNumber)
                    .orElseThrow(() -> {
                        log.error("[{}] Seat not found: {} in show {}", 
                                 ErrorCode.SEAT_NOT_FOUND.getCode(), seatNumber, request.getShowId());
                        return new org.website.exception.ResourceNotFoundException(
                            "Seat not found: " + seatNumber);
                    });

                if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
                    log.warn("[{}] Seat {} already booked/reserved", ErrorCode.SEAT_ALREADY_BOOKED.getCode(), seatNumber);
                    throw new SeatAlreadyBookedException(
                        "Seat " + seatNumber + " is not available");
                }

                seat.setStatus(SeatStatus.RESERVED);
                seatRepository.save(seat);
                selectedSeats.add(seat);
                totalAmount += show.getPriceInCents();
                log.debug("Reserved seat: {} for show: {}", seatNumber, show.getId());
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("[{}] Concurrency conflict: Seat booked by another user", 
                     ErrorCode.BOOKING_CONCURRENCY_CONFLICT.getCode());
            ErrorUtil.throwSeatAlreadyBooked(
                "Someone else just booked these seats. Please select different seats.");
        }

        // Generate booking reference
        String bookingReference = "BK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        // Create booking
        Booking booking = new Booking(user, show, bookingReference, BookingStatus.PENDING, totalAmount);
        booking.setSeats(selectedSeats);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("[BOOKING] Created: {} for user: {} with {} seats, amount: {} paise", 
                 bookingReference, user.getEmail(), selectedSeats.size(), totalAmount);

        return convertToDto(savedBooking);
    }

    /**
     * Retrieve booking by ID with user verification.
     * 
     * @param bookingId The booking ID
     * @param userId The requesting user (must own booking)
     * @return BookingResponseDto with booking details
     * 
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own booking (ERR002)
     */
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.debug("[BOOKING] Fetching booking {} for user {}", bookingId, userId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> {
                log.error("[{}] Booking not found: {}", ErrorCode.BOOKING_NOT_FOUND.getCode(), bookingId);
                return new org.website.exception.ResourceNotFoundException(
                    "Booking not found with ID: " + bookingId);
            });

        enforceUserOwnership(booking, userId);
        return convertToDto(booking);
    }

    /**
     * Retrieve booking by reference with user verification.
     * 
     * @param reference The booking reference (e.g., "BK-ABC123DEF")
     * @param userId The requesting user (must own booking)
     * @return BookingResponseDto with booking details
     * 
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own booking (ERR002)
     */
    public BookingResponseDto getBookingByReference(String reference, Long userId) {
        log.debug("[BOOKING] Fetching booking by reference: {} for user {}", reference, userId);
        
        Booking booking = bookingRepository.findByBookingReference(reference)
            .orElseThrow(() -> {
                log.error("[{}] Booking not found: {}", ErrorCode.BOOKING_NOT_FOUND.getCode(), reference);
                return new org.website.exception.ResourceNotFoundException(
                    "Booking not found with reference: " + reference);
            });

        enforceUserOwnership(booking, userId);
        return convertToDto(booking);
    }

    /**
     * Retrieve all bookings for a user.
     * 
     * @param userId The user ID
     * @return List of BookingResponseDto sorted by date (newest first)
     * 
     * @throws ResourceNotFoundException if user not found (ERR006)
     */
    public List<BookingResponseDto> getUserBookings(Long userId) {
        log.debug("[BOOKING] Fetching all bookings for user: {}", userId);
        
        // Verify user exists (ERR006)
        userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("[{}] User not found: {}", ErrorCode.AUTH_USER_NOT_FOUND.getCode(), userId);
                return new org.website.exception.ResourceNotFoundException(
                    "User not found with ID: " + userId);
            });

        List<BookingResponseDto> bookings = bookingRepository
            .findByUserIdOrderByBookingTimeDesc(userId)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());

        log.debug("[BOOKING] Retrieved {} bookings for user: {}", bookings.size(), userId);
        return bookings;
    }

    /**
     * Cancel a pending booking and release seats.
     * 
     * Business Logic:
     * 1. Verify booking exists (ERR106)
     * 2. Verify user owns booking (ERR002)
     * 3. Verify booking is PENDING (ERR203)
     * 4. Release all seats to AVAILABLE
     * 5. Mark booking as CANCELLED
     * 
     * @param bookingId The booking ID to cancel
     * @param userId The requesting user (must own booking)
     * 
     * @throws ResourceNotFoundException if booking not found (ERR106)
     * @throws IllegalArgumentException if user doesn't own booking (ERR002)
     * @throws IllegalStateException if booking not in PENDING state (ERR203)
     */
    public void cancelBooking(Long bookingId, Long userId) {
        log.info("[BOOKING] User {} canceling booking: {}", userId, bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> {
                log.error("[{}] Booking not found: {}", ErrorCode.BOOKING_NOT_FOUND.getCode(), bookingId);
                return new org.website.exception.ResourceNotFoundException(
                    "Booking not found with ID: " + bookingId);
            });

        enforceUserOwnership(booking, userId);

        // Verify PENDING status (ERR203)
        if (!BookingStatus.PENDING.equals(booking.getStatus())) {
            log.error("[{}] Cannot cancel booking in {} state: {}", 
                     ErrorCode.BOOKING_INVALID_STATE.getCode(), booking.getStatus(), bookingId);
            throw new IllegalStateException(
                "Can only cancel PENDING bookings");
        }

        // Release seats
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
            log.debug("Released seat: {} from booking: {}", seat.getSeatNumber(), bookingId);
        }

        // Mark as cancelled
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("[BOOKING] Cancelled: {} by user: {}", booking.getBookingReference(), userId);
    }

    /**
     * Retrieve all bookings (admin only).
     * 
     * Used for analytics and reporting by admin dashboard.
     * 
     * @return List of all BookingResponseDto
     */
    public List<BookingResponseDto> getAllBookings() {
        log.info("[BOOKING] Admin retrieving all bookings for analytics");
        return bookingRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    /**
     * Enforce user ownership of booking.
     * Throws exception if user doesn't own the booking (ERR002).
     * 
     * @param booking The booking to verify
     * @param userId The user ID to check
     * 
     * @throws IllegalArgumentException if user doesn't own booking
     */
    private void enforceUserOwnership(Booking booking, Long userId) {
        if (!booking.getUser().getId().equals(userId)) {
            log.warn("[{}] Unauthorized access to booking: {} by user: {}", 
                     ErrorCode.AUTH_UNAUTHORIZED_ACCESS.getCode(), booking.getId(), userId);
            ErrorUtil.throwUnauthorized("You don't have permission to access this booking");
        }
    }

    /**
     * Convert Booking entity to BookingResponseDto.
     * 
     * Extracts seat numbers and formats for API response.
     * 
     * @param booking The entity to convert
     * @return BookingResponseDto suitable for API response
     */
    private BookingResponseDto convertToDto(Booking booking) {
        List<Integer> seatNumbers = booking.getSeats().stream()
            .map(Seat::getSeatNumber)
            .collect(Collectors.toList());

        return new BookingResponseDto(
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
