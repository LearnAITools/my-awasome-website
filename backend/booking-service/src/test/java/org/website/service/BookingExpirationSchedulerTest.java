package org.website.service;

import org.junit.jupiter.api.Test;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.repository.BookingRepository;
import org.website.repository.SeatRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingExpirationSchedulerTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final SeatRepository seatRepository = mock(SeatRepository.class);
    private final BookingExpirationScheduler scheduler = new BookingExpirationScheduler(bookingRepository, seatRepository);

    @Test
    void expiresOldPendingBookingsAndReleasesSeats() {
        Booking booking = new Booking();
        booking.setBookingReference("BK-OLD");
        booking.setBookingTime(LocalDateTime.now().minusMinutes(6));
        booking.setStatus(BookingStatus.PENDING);
        Seat seat = new Seat();
        seat.setStatus(SeatStatus.RESERVED);
        booking.setSeats(List.of(seat));
        when(bookingRepository.findByStatus(BookingStatus.PENDING)).thenReturn(List.of(booking));

        scheduler.expirePendingBookings();

        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertEquals(BookingStatus.EXPIRED, booking.getStatus());
        verify(seatRepository).save(seat);
        verify(bookingRepository).save(booking);
    }

    @Test
    void leavesRecentPendingBookingsUnchanged() {
        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now().minusMinutes(4));
        booking.setStatus(BookingStatus.PENDING);
        when(bookingRepository.findByStatus(BookingStatus.PENDING)).thenReturn(List.of(booking));

        scheduler.expirePendingBookings();

        assertEquals(BookingStatus.PENDING, booking.getStatus());
    }
}
