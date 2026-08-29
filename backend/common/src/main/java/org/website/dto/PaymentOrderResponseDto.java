package org.website.dto;

public record PaymentOrderResponseDto(
    String orderId,
    String bookingReference,
    Long amountInCents,
    String currency,
    String status
) {}
