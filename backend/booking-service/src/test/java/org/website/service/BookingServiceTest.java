package org.website.service;

import org.junit.jupiter.api.Test;
import org.website.dto.BookingRequest;
import org.website.dto.BookingResponse;
import org.website.internal.api.UserIdentityGateway;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Movie;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.model.Show;
import org.website.model.Theater;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.BookingRepository;
import org.website.repository.SeatRepository;
import org.website.repository.ShowRepository;
import org.website.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final ShowRepository showRepository = mock(ShowRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserIdentityGateway userIdentityGateway = mock(UserIdentityGateway.class);
    private final BookingService bookingService = new BookingService(
        bookingRepository, seatRepository, showRepository, userRepository, userIdentityGateway
    );

    @Test
    void createsPendingBookingAndReservesSelectedSeats() {
        User user = new User("user@example.com", "password", "Test User", UserRole.ROLE_USER);
        user.setId(10L);
        Show show = show(3L);
        Seat firstSeat = seat(show, 11);
        Seat secondSeat = seat(show, 12);
        Booking savedBooking = new Booking(user, show, "BK-TEST", BookingStatus.PENDING, 150000L);
        savedBooking.setId(20L);
        savedBooking.setSeats(List.of(firstSeat, secondSeat));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(showRepository.findById(3L)).thenReturn(Optional.of(show));
        when(seatRepository.findByShowIdAndSeatNumber(3L, 11)).thenReturn(Optional.of(firstSeat));
        when(seatRepository.findByShowIdAndSeatNumber(3L, 12)).thenReturn(Optional.of(secondSeat));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingResponse response = bookingService.createBooking(
            10L, new BookingRequest(3L, List.of(11, 12))
        );

        assertEquals("PENDING", response.getStatus());
        assertEquals(150000L, response.getTotalAmountInCents());
        assertEquals(List.of(11, 12), response.getSeatNumbers());
        assertEquals(SeatStatus.RESERVED, firstSeat.getStatus());
        assertEquals(SeatStatus.RESERVED, secondSeat.getStatus());
        verify(seatRepository, org.mockito.Mockito.times(2)).save(any(Seat.class));
    }

    @Test
    void cancelsPendingBookingAndReleasesSeats() {
        Booking booking = new Booking();
        booking.setId(20L);
        booking.setBookingReference("BK-TEST");
        booking.setStatus(BookingStatus.PENDING);
        Seat seat = new Seat();
        seat.setSeatNumber(11);
        seat.setStatus(SeatStatus.RESERVED);
        booking.setSeats(List.of(seat));
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(bookingRepository.existsByIdAndUserId(20L, 10L)).thenReturn(true);

        bookingService.cancelBooking(20L, 10L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        verify(seatRepository).save(seat);
        verify(bookingRepository).save(booking);
    }

    @Test
    void rejectsUnavailableSeat() {
        User user = new User("user@example.com", "password", "Test User", UserRole.ROLE_USER);
        Show show = show(3L);
        Seat seat = seat(show, 11);
        seat.setStatus(SeatStatus.BOOKED);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(showRepository.findById(3L)).thenReturn(Optional.of(show));
        when(seatRepository.findByShowIdAndSeatNumber(3L, 11)).thenReturn(Optional.of(seat));

        assertThrows(
            RuntimeException.class,
            () -> bookingService.createBooking(10L, new BookingRequest(3L, List.of(11)))
        );
    }

    private Show show(Long id) {
        Movie movie = new Movie();
        movie.setTitle("Dune");
        Theater theater = new Theater();
        theater.setName("Central");
        Show show = new Show(movie, theater, LocalDateTime.now().plusDays(1), 100, 75000L, 1);
        show.setId(id);
        return show;
    }

    private Seat seat(Show show, int number) {
        return new Seat(show, number, number / 10, number % 10, SeatStatus.AVAILABLE);
    }
}
