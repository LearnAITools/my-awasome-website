package org.website.dto;

public record BookingPaymentStatusUpdateDto(
    String bookingReference,
    String paymentId,
    String orderId,
    String paymentStatus,
    Long amountInCents,
    String updatedAt
) {}
