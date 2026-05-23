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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    public BookingResponse createBooking(Long userId, BookingRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Show show = showRepository.findById(request.getShowId())
            .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + request.getShowId()));

        List<Seat> selectedSeats = new ArrayList<>();
        long totalAmount = 0;

        try {
            for (Integer seatNumber : request.getSelectedSeats()) {
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
            log.warn("Seat selection conflict due to concurrent booking for user: {}", userId);
            throw new SeatAlreadyBookedException("Someone else just booked these seats. Please try again.");
        }

        String bookingReference = "BK" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        Booking booking = new Booking(user, show, bookingReference, BookingStatus.PENDING, totalAmount);
        booking.setSeats(selectedSeats);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created: {} for user: {} with {} seats", bookingReference, user.getEmail(), selectedSeats.size());

        return convertToDTO(savedBooking);
    }

    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        return convertToDTO(booking);
    }

    public BookingResponse getBookingByReference(String reference, Long userId) {
        Booking booking = bookingRepository.findByBookingReference(reference)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        return convertToDTO(booking);
    }

    public List<BookingResponse> getUserBookings(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return bookingRepository.findByUserIdOrderByBookingTimeDesc(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to booking");
        }

        if (!BookingStatus.PENDING.equals(booking.getStatus())) {
            throw new IllegalStateException("Can only cancel pending bookings");
        }

        // Release reserved seats
        for (Seat seat : booking.getSeats()) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        log.info("Booking cancelled: {}", booking.getBookingReference());
    }

    private BookingResponse convertToDTO(Booking booking) {
        List<Integer> seatNumbers = booking.getSeats().stream()
            .map(Seat::getSeatNumber)
            .collect(Collectors.toList());

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
