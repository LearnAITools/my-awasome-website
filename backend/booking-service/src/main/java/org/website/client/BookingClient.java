package org.website.client;

import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;

public interface BookingClient {
    BookingPaymentStatusAckDto updatePaymentStatus(BookingPaymentStatusUpdateDto request);
}
