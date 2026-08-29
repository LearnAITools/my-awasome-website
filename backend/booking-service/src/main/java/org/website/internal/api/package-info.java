/**
 * Internal service APIs for booking-service-owned integration points.
 *
 * <p>These contracts define the boundary for cross-service calls. The only allowed
 * transport objects are DTOs from the common module; domain entities remain local to
 * their owning service and are never shared across the process boundary.</p>
 */
package org.website.internal.api;
