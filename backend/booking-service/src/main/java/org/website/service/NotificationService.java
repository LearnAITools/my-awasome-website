package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.model.Booking;
import org.website.model.Seat;
import org.website.repository.BookingRepository;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NotificationService {

    private BookingRepository bookingRepository;

    NotificationService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void sendBookingConfirmation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        String qrCode = generateQRCode(booking);
        booking.setQrCode(qrCode);
        bookingRepository.save(booking);

        log.info("Booking Confirmed! Movie: {}, Seats: {}, QR: {}", 
            booking.getShow().getMovie().getTitle(),
            booking.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.toList()),
            qrCode
        );
    }

    private String generateQRCode(Booking booking) {
        return "QR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "_BK" + booking.getId();
    }
}
