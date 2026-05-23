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

@RestController
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @RequestAttribute("userId") Long userId,
            @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        BookingResponse response = bookingService.getBookingById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(
            @PathVariable String reference,
            @RequestAttribute("userId") Long userId) {
        BookingResponse response = bookingService.getBookingByReference(reference, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<BookingResponse>> getUserBookings(
            @RequestAttribute("userId") Long userId) {
        List<BookingResponse> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        bookingService.cancelBooking(id, userId);
        return ResponseEntity.noContent().build();
    }
}
