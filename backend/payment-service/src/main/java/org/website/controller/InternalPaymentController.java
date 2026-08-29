package org.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.PaymentOrderRequestDto;
import org.website.dto.PaymentOrderResponseDto;

@RestController
@RequestMapping("/internal/payments")
public class InternalPaymentController {

    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponseDto> createPaymentOrder(@RequestBody PaymentOrderRequestDto request) {
        return ResponseEntity.ok(new PaymentOrderResponseDto(
            "internal_order_" + request.bookingReference(),
            request.bookingReference(),
            request.amountInCents(),
            request.currency(),
            "CREATED"
        ));
    }
}
