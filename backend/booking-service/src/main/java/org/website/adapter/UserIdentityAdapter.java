package org.website.adapter;

import org.website.dto.UserIdentitySummary;
import org.website.internal.api.UserIdentityGateway;

@Deprecated(forRemoval = true)
public interface UserIdentityAdapter extends UserIdentityGateway {
    @Override
    UserIdentitySummary getUserSummary(Long userId);
}
