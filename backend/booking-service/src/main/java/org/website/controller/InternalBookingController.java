package org.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;
import org.website.service.BookingServicePort;

@RestController
@RequestMapping("/internal/bookings")
public class InternalBookingController {

    private final BookingServicePort bookingService;

    public InternalBookingController(BookingServicePort bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/payment-status")
    public ResponseEntity<BookingPaymentStatusAckDto> updatePaymentStatus(@RequestBody BookingPaymentStatusUpdateDto request) {
        return ResponseEntity.ok(new BookingPaymentStatusAckDto(
            request.bookingReference(),
            true,
            request.paymentStatus()
        ));
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<BookingPaymentStatusAckDto> getBookingStatus(@PathVariable String bookingReference) {
        return ResponseEntity.ok(new BookingPaymentStatusAckDto(
            bookingReference,
            true,
            "ACTIVE"
        ));
    }
}
