package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.service.PaymentService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Payment webhook receiver for Razorpay events.
 *
 * This controller receives webhook events from Razorpay and validates the
 * signature using the shared webhook secret. It is intentionally exposed
 * without JWT authentication so Razorpay can deliver event payloads.
 *
 * Supported events:
 * - payment.captured
 * - payment.failed
 * - refund.processed
 */
@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(path = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signatureHeader,
            @RequestBody String payload) {

        log.info("Received Razorpay webhook event");

        if (!verifyWebhookSignature(signatureHeader, payload)) {
            log.warn("Invalid Razorpay webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        log.info("Webhook signature verified successfully");

        paymentService.handleWebhookEvent(payload);
        return ResponseEntity.ok("Webhook processed");
    }

    private boolean verifyWebhookSignature(String signature, String payload) {
        try {
            String computed = computeHmacSha256(payload, webhookSecret);
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("Failed to verify webhook signature", e);
            return false;
        }
    }

    private String computeHmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
