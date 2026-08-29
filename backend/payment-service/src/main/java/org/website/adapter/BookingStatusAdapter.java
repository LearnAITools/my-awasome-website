package org.website.adapter;

import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;
import org.website.internal.api.BookingStatusGateway;

@Deprecated(forRemoval = true)
public interface BookingStatusAdapter extends BookingStatusGateway {
    @Override
    BookingPaymentStatusAckDto updatePaymentStatus(BookingPaymentStatusUpdateDto request);
}
