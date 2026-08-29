package org.website.service;

import org.website.dto.BookingRequest;
import org.website.dto.BookingResponse;

import java.util.List;

public interface BookingServicePort {
    BookingResponse createBooking(Long userId, BookingRequest request);

    BookingResponse getBookingById(Long bookingId, Long userId);

    BookingResponse getBookingByReference(String reference, Long userId);

    List<BookingResponse> getUserBookings(Long userId);

    void cancelBooking(Long bookingId, Long userId);

    List<BookingResponse> getAllBookings();
}
