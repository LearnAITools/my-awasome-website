package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.website.model.Booking;
import org.website.model.BookingStatus;
import org.website.model.Seat;
import org.website.model.SeatStatus;
import org.website.repository.BookingRepository;
import org.website.repository.SeatRepository;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public BookingExpirationScheduler(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @Scheduled(fixedRateString = "${booking.expiration.check-rate-ms:60000}")
    @Transactional
    public void expirePendingBookings() {
        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);
        LocalDateTime expirationThreshold = LocalDateTime.now().minusMinutes(5);

        for (Booking booking : pendingBookings) {
            if (booking.getBookingTime().isBefore(expirationThreshold)) {
                log.info("Expiring pending booking: {}", booking.getBookingReference());

                for (Seat seat : booking.getSeats()) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seatRepository.save(seat);
                }

                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);
            }
        }
    }
}
