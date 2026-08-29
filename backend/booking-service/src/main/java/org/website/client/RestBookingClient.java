package org.website.client;

import org.springframework.stereotype.Component;
import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;

@Component
public class RestBookingClient implements BookingClient {

    @Override
    public BookingPaymentStatusAckDto updatePaymentStatus(BookingPaymentStatusUpdateDto request) {
        return new BookingPaymentStatusAckDto(
            request.bookingReference(),
            true,
            request.paymentStatus()
        );
    }
}
