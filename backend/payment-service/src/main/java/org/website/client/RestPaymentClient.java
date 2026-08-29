package org.website.client;

import org.springframework.stereotype.Component;
import org.website.dto.PaymentOrderRequestDto;
import org.website.dto.PaymentOrderResponseDto;

@Component
public class RestPaymentClient implements PaymentClient {

    @Override
    public PaymentOrderResponseDto createOrder(PaymentOrderRequestDto request) {
        return new PaymentOrderResponseDto(
            "internal_order_" + request.bookingReference(),
            request.bookingReference(),
            request.amountInCents(),
            request.currency(),
            "CREATED"
        );
    }
}
