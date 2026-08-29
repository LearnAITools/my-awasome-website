package org.website.client;

import org.website.dto.UserIdentitySummary;
import org.website.dto.UserSummaryDto;

public interface UserClient {
    UserIdentitySummary getUserSummary(Long userId);

    UserSummaryDto getUserSummaryForBooking(Long userId);
}
