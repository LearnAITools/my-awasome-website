package org.website.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.website.dto.BookingPaymentStatusAckDto;
import org.website.dto.BookingPaymentStatusUpdateDto;
import org.website.internal.api.BookingStatusGateway;

@Component
public class HttpBookingStatusAdapter implements BookingStatusGateway {

    private final RestClient restClient;

    public HttpBookingStatusAdapter(
            @Value("${booking.service.base-url:http://localhost:8082}") String bookingServiceBaseUrl,
            @Value("${booking.service.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${booking.service.read-timeout-ms:5000}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
            .baseUrl(bookingServiceBaseUrl)
            .requestFactory(requestFactory)
            .build();
    }

    @Override
    public BookingPaymentStatusAckDto updatePaymentStatus(BookingPaymentStatusUpdateDto request) {
        return restClient.post()
            .uri("/internal/bookings/payment-status")
            .body(request)
            .retrieve()
            .body(BookingPaymentStatusAckDto.class);
    }
}
