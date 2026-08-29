package org.website.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.website.dto.UserIdentitySummary;
import org.website.internal.api.UserIdentityGateway;

@Component
public class HttpUserIdentityAdapter implements UserIdentityGateway {

    private final RestClient restClient;

    public HttpUserIdentityAdapter(
            @Value("${auth.service.base-url:http://localhost:8081}") String authServiceBaseUrl,
            @Value("${auth.service.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${auth.service.read-timeout-ms:5000}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
            .baseUrl(authServiceBaseUrl)
            .requestFactory(requestFactory)
            .build();
    }

    @Override
    public UserIdentitySummary getUserSummary(Long userId) {
        return restClient.get()
            .uri("/internal/users/{userId}", userId)
            .retrieve()
            .body(UserIdentitySummary.class);
    }
}
