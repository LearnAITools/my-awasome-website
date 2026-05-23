package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.PaymentOrderRequest;
import org.website.dto.PaymentOrderResponse;
import org.website.dto.PaymentVerifyRequest;
import org.website.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createPaymentOrder(
            @RequestAttribute("userId") Long userId,
            @RequestBody PaymentOrderRequest request) {
        PaymentOrderResponse response = paymentService.createPaymentOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestAttribute("userId") Long userId,
            @RequestBody PaymentVerifyRequest request) {
        boolean verified = paymentService.verifyPayment(userId, request);
        return verified ? ResponseEntity.ok("Payment verified successfully") : ResponseEntity.badRequest().body("Payment verification failed");
    }
}
