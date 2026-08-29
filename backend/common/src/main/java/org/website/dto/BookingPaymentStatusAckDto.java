package org.website.dto;

public record BookingPaymentStatusAckDto(
    String bookingReference,
    boolean accepted,
    String status
) {}
