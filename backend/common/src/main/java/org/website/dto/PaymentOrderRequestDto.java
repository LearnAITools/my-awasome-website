package org.website.dto;

public record PaymentOrderRequestDto(
    String bookingReference,
    Long userId,
    Long amountInCents,
    String currency,
    String bookingStatus
) {}
