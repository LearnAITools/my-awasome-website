package org.website.internal.api;

import org.website.dto.UserIdentitySummary;

/**
 * Strict internal contract for identity lookups from Booking to Auth.
 *
 * Only DTOs from the shared common module are allowed across service boundaries.
 * Domain entities must never cross process boundaries.
 */
public interface UserIdentityGateway {
    UserIdentitySummary getUserSummary(Long userId);
}
