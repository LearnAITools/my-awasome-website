package org.website.client;

import org.website.dto.PaymentOrderRequestDto;
import org.website.dto.PaymentOrderResponseDto;

public interface PaymentClient {
    PaymentOrderResponseDto createOrder(PaymentOrderRequestDto request);
}
