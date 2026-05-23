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
    @Autowired
    private BookingRepository bookingRepository;

    public void sendBookingConfirmation(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        String qrCode = generateQRCode(booking);
        booking.setQrCode(qrCode);
        bookingRepository.save(booking);

        String emailBody = buildEmailTemplate(booking, qrCode);
        log.info("Booking Confirmed! Movie: {}, Seats: {}, QR: {}", 
            booking.getShow().getMovie().getTitle(),
            booking.getSeats().stream().map(Seat::getSeatNumber).collect(Collectors.toList()),
            qrCode
        );
    }

    private String generateQRCode(Booking booking) {
        return "QR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "_BK" + booking.getId();
    }

    private String buildEmailTemplate(Booking booking, String qrCode) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Booking Confirmation</h2>");
        html.append("<p><strong>Booking Reference:</strong> ").append(booking.getBookingReference()).append("</p>");
        html.append("<p><strong>Movie:</strong> ").append(booking.getShow().getMovie().getTitle()).append("</p>");
        html.append("<p><strong>Theater:</strong> ").append(booking.getShow().getTheater().getName()).append("</p>");
        html.append("<p><strong>Show Time:</strong> ").append(booking.getShow().getShowTime()).append("</p>");
        html.append("<p><strong>Seats:</strong> ");
        
        for (Seat seat : booking.getSeats()) {
            html.append("Row ").append(seat.getRow()).append(", Seat ").append(seat.getColumn()).append(" | ");
        }
        
        html.append("</p>");
        html.append("<p><strong>Total Amount:</strong> ₹").append(booking.getTotalAmountInCents() / 100.0).append("</p>");
        html.append("<p><strong>QR Code:</strong> ").append(qrCode).append("</p>");
        html.append("<p>Thank you for booking with us!</p>");
        
        return html.toString();
    }
}
