# Backend Architecture

## 1. Target state: true microservice-oriented design

This repository is being aligned to a true microservice-oriented architecture with explicit ownership boundaries.

The guiding rule is simple:
- One service owns one business domain.
- One service owns one database or one well-defined persistence boundary.
- Cross-service communication happens through explicit contracts, not shared entity reuse.
- Shared infrastructure remains in the common module, but business entities must not be duplicated across services.

This is the correct target state for a production-grade distributed system.

## 2. Service ownership model

### Auth Service
Owner: user identity, authentication, profile lifecycle

Owns:
- user profile records
- login and registration flows
- JWT generation and identity validation
- account lifecycle operations

Does not own:
- seats
- bookings
- payment ledger
- movie show inventory

### Booking Service
Owner: booking lifecycle and seat reservation state

Owns:
- booking records
- seat availability and reservation status
- show and movie metadata required for booking flows
- booking cancellation and expiry logic

Does not own:
- payment authorization or settlement
- user credentials
- identity state

### Payment Service
Owner: payment processing, refunds, and transaction records

Owns:
- payment transactions
- Razorpay integration
- refund operations
- payment status transitions

Does not own:
- user signup data
- booking creation logic
- seat inventory ownership

### Common Module
Owner: cross-cutting infrastructure

Owns:
- JWT utilities
- authentication filters
- shared exception handling contracts
- global error model
- common security configuration

Does not own:
- business domain data
- service-specific logic
- CRUD persistence for domain entities

## 3. Explicit ownership boundaries

The service boundary rule is:

- Auth service owns identity and user profile data.
- Booking service owns reservation state and booking lifecycle.
- Payment service owns transaction and refund state.
- Common owns infrastructure only.

This prevents the earlier design smell where multiple modules reused the same entity definitions and blurred ownership.

## 4. Integration contract rules

Communication between services should follow these patterns:

- Auth -> Booking: user identity lookup through a client contract or event message
- Booking -> Payment: booking reference and amount for payment initiation
- Payment -> Booking: payment verification result and status update
- Booking -> Auth: user existence or account validation only through a defined contract

Important rule:
- no direct sharing of JPA entities across services
- no duplicate domain model reuse as a form of internal coupling

## 5. Data ownership policy

Each service must keep its own authoritative model:

- user data stays in auth-service
- booking data stays in booking-service
- payment data stays in payment-service

Any cross-service read must use DTOs or event payloads.

## 6. Port-driven service design

Each service exposes a business contract interface to preserve abstraction.

Examples:
- AuthServicePort
- UserProfileServicePort
- BookingServicePort
- PaymentServicePort

This matches the Dependency Inversion Principle and keeps controllers decoupled from service implementations.

## 7. Security model

The authentication and authorization layer is centralized in the common module:
- CommonSecurityConfig
- JwtAuthenticationFilter
- JwtTokenProvider

This ensures all services use the same JWT validation mechanics and removes duplicated security configuration.

## 8. Recommended service interaction flow

### Create booking flow
1. User authenticates via Auth Service.
2. Booking Service validates show and seat availability.
3. Booking Service creates a reservation and returns a booking reference.
4. Payment Service creates a Razorpay order for that booking reference.
5. Payment Service verifies payment and notifies Booking Service of completion.

### Identity flow
1. Auth Service issues JWT with user identity.
2. Booking and Payment services verify the token through common security infrastructure.
3. Service-level authorization uses the user identity from the token without sharing local user entities.

## 9. Why this design is stronger

This structure is better than the previous duplicated-entity approach because it provides:

- clear business ownership
- cleaner persistence boundaries
- reduced accidental coupling
- better horizontal scaling opportunities
- independent deployments in the future
- easier fault isolation

## 10. Remaining migration work

The remaining work is not about syntax or compilation. It is about moving from a duplicated-domain module setup to a true owned-domain service setup.

Planned migration steps:
1. Remove duplicate business entity definitions from service modules.
2. Keep only service-local domain objects in the owning service.
3. Replace cross-service entity sharing with DTOs or event payloads.
4. Introduce an API contract layer or synchronous client abstraction between services.
5. Add contract tests for cross-service interaction.
6. Move to independent deployment pipelines per service after the domain model is cleanly split.

## 11. Conclusion

The correct long-term architecture for this backend is a microservice-oriented design with explicit ownership boundaries:
- Auth owns identity
- Booking owns reservation state
- Payment owns transactions
- Common owns infrastructure

This is the proper target design, and the repository is now aligned toward that direction.
