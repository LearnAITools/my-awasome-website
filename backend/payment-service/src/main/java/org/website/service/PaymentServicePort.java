package org.website.service;

import org.website.dto.PaymentOrderRequest;
import org.website.dto.PaymentOrderResponse;
import org.website.dto.PaymentVerifyRequest;

public interface PaymentServicePort {
    PaymentOrderResponse createPaymentOrder(Long userId, PaymentOrderRequest request);

    boolean verifyPayment(Long userId, PaymentVerifyRequest request);

    void handleWebhookEvent(String payload);

    boolean refundBooking(Long userId, String bookingReference);
}
