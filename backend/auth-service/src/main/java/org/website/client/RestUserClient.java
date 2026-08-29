package org.website.client;

import org.springframework.stereotype.Component;
import org.website.dto.UserIdentitySummary;
import org.website.dto.UserSummaryDto;

@Component
public class RestUserClient implements UserClient {

    @Override
    public UserIdentitySummary getUserSummary(Long userId) {
        return new UserIdentitySummary(userId, "user@example.com", "System User", "ROLE_USER", true);
    }

    @Override
    public UserSummaryDto getUserSummaryForBooking(Long userId) {
        return new UserSummaryDto(userId, "user@example.com", "System User", "ROLE_USER");
    }
}
