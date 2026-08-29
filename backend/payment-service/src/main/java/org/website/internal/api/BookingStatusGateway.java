package org.website.internal.api;

import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;

/**
 * Strict internal contract for payment-status synchronisation from Payment to Booking.
 *
 * This boundary is intentionally DTO-only. The owning services remain responsible
 * for their own persistence and domain model lifecycle.
 */
public interface BookingStatusGateway {
    BookingPaymentStatusAckDto updatePaymentStatus(BookingPaymentStatusUpdateDto request);
}
