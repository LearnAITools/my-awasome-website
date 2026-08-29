# Microservice Split Implementation Plan

## 1. Objective

This document converts the current modular backend into a true microservice-oriented design with explicit ownership boundaries and clear validation contracts.

The target state is:
- Auth Service owns identity and account data.
- Booking Service owns reservation and booking state.
- Payment Service owns transaction and refund state.
- Common module owns shared infrastructure only.

---

## 2. Target service layout

### 2.1 Auth Service

Responsibilities:
- signup / login / logout flows
- user profile lifecycle
- JWT issuance and authentication validation
- user lookup APIs

Database ownership:
- users
- roles
- profile metadata
- password hash and identity state

Boundaries:
- does not own bookings
- does not own payment transactions
- does not own seat availability

### 2.2 Booking Service

Responsibilities:
- movie / show / theater catalog for booking
- seat reservation lifecycle
- booking creation, lookup, and cancellation
- expiry logic and admin analytics

Database ownership:
- shows
- movies
- theaters
- seats
- bookings
- booking-seat relations

Boundaries:
- does not own user credentials
- does not own payment settlements
- does not own identity profile data

### 2.3 Payment Service

Responsibilities:
- Razorpay order creation
- payment verification
- refund processing
- webhook handling
- payment status records

Database ownership:
- payments
- payment events
- refund records
- transaction metadata

Boundaries:
- does not own user profiles
- does not own seat reservation state as an authority
- does not own the booking domain as source-of-truth data

### 2.4 Common Module

Responsibilities:
- JWT utilities
- authentication filters
- security configuration
- error payload models
- shared validation helpers
- shared observability helpers

Policy:
- Common is not a business domain owner.
- Common must not contain authoritative business entities.

---

## 3. Ownership rules

The following rules must be enforced during the split:

1. Each service owns exactly one business domain.
2. A service cannot hold a JPA entity that belongs to another service as a source of truth.
3. Data exchange happens through DTOs, not JPA entities.
4. Cross-service calls are explicit and versioned.
5. The service boundary is validated by package ownership and table ownership.

---

## 4. Inter-service API boundaries

## 4.1 Auth Service -> Booking Service

Use case:
- Booking Service needs to validate that the user exists and is active.

Recommended API:
- GET /internal/users/{userId}
- Response: UserIdentitySummary

DTO contract:

```java
public record UserIdentitySummary(
    Long userId,
    String email,
    String fullName,
    String role,
    boolean active
) {}
```

Purpose:
- simple identity validation
- avoids sharing the full user entity
- keeps user domain in Auth Service only

## 4.2 Booking Service -> Payment Service

Use case:
- Payment Service creates an order for a confirmed booking.

Recommended API:
- POST /internal/payments/orders

Request DTO:

```java
public record PaymentOrderRequestDto(
    String bookingReference,
    Long userId,
    Long amountInCents,
    String currency,
    String bookingStatus
) {}
```

Response DTO:

```java
public record PaymentOrderResponseDto(
    String orderId,
    String bookingReference,
    Long amountInCents,
    String currency,
    String status
) {}
```

Purpose:
- payment service receives only the necessary booking summary
- booking service remains the reservation authority

## 4.3 Payment Service -> Booking Service

Use case:
- Payment verification must update booking state.

Recommended API:
- POST /internal/bookings/payment-status

Request DTO:

```java
public record BookingPaymentStatusUpdateDto(
    String bookingReference,
    String paymentId,
    String orderId,
    String paymentStatus,
    Long amountInCents,
    String updatedAt
) {}
```

Response DTO:

```java
public record BookingPaymentStatusAckDto(
    String bookingReference,
    boolean accepted,
    String status
) {}
```

Purpose:
- payment service updates booking lifecycle without exposing its internal entities
- avoids connection to booking database directly from the payment service

## 4.4 Booking Service -> Auth Service

Use case:
- booking requests may require user identity validation.

Recommended API:
- GET /internal/users/{userId}/summary

DTO:

```java
public record UserSummaryDto(
    Long userId,
    String email,
    String fullName,
    String role
) {}
```

Purpose:
- minimal profile contract
- no full account implementation details

---

## 5. DTO contract inventory

## 5.1 Auth Service DTOs

### SignupRequest

```java
public record SignupRequest(
    String email,
    String password,
    String fullName
) {}
```

### AuthResponse

```java
public record AuthResponse(
    String token,
    String message,
    Long userId,
    String email,
    String fullName,
    String role
) {}
```

### UserIdentitySummary

```java
public record UserIdentitySummary(
    Long userId,
    String email,
    String fullName,
    String role,
    boolean active
) {}
```

## 5.2 Booking Service DTOs

### BookingRequest

```java
public record BookingRequest(
    Long showId,
    List<Integer> selectedSeats
) {}
```

### BookingResponse

```java
public record BookingResponse(
    Long bookingId,
    String bookingReference,
    String status,
    Long totalAmountInCents,
    LocalDateTime bookingTime,
    String movieTitle,
    String showTime,
    String theaterName,
    List<Integer> seatNumbers
) {}
```

### BookingPaymentStatusUpdateDto

```java
public record BookingPaymentStatusUpdateDto(
    String bookingReference,
    String paymentId,
    String orderId,
    String paymentStatus,
    Long amountInCents,
    String updatedAt
) {}
```

## 5.3 Payment Service DTOs

### PaymentOrderRequestDto

```java
public record PaymentOrderRequestDto(
    String bookingReference,
    Long userId,
    Long amountInCents,
    String currency,
    String bookingStatus
) {}
```

### PaymentOrderResponseDto

```java
public record PaymentOrderResponseDto(
    String orderId,
    String bookingReference,
    Long amountInCents,
    String currency,
    String status
) {}
```

### PaymentVerifyRequest

```java
public record PaymentVerifyRequest(
    String bookingReference,
    String razorpayOrderId,
    String razorpayPaymentId,
    String razorpaySignature
) {}
```

---

## 6. Explicit API contract design

## 6.1 Auth Service endpoints

### Public
- POST /api/auth/signup
- POST /api/auth/login

### Internal
- GET /internal/users/{userId}
- GET /internal/users/{userId}/summary

## 6.2 Booking Service endpoints

### Protected
- POST /api/bookings
- GET /api/bookings/{id}
- GET /api/bookings/reference/{reference}
- GET /api/bookings/user
- DELETE /api/bookings/{id}

### Internal
- POST /internal/bookings/payment-status
- GET /internal/bookings/{bookingReference}

## 6.3 Payment Service endpoints

### Protected
- POST /api/payments/create-order
- POST /api/payments/verify
- POST /api/payments/refund

### Internal
- POST /internal/payments/orders
- POST /internal/payments/verify-status

### Webhook
- POST /api/payments/webhook

---

## 7. Recommended migration phases

## Phase 1: isolate ownership
- keep existing module layout
- declare service ownership boundaries in documentation
- ensure each module has its own database schema and domain model

## Phase 2: introduce internal contracts
- add internal REST endpoints for cross-service lookup/update tasks
- define DTOs for User, Booking, and Payment state transitions
- ensure no service exposes its JPA entities directly

## Phase 3: remove shared entity coupling
- remove duplicate model reuse across modules
- keep only local entities and DTOs inside each microservice

## Phase 4: add service-to-service client layer
- create Feign or RestClient abstraction per service boundary
- isolate network calls behind interfaces

Example:

```java
public interface UserClient {
    UserIdentitySummary getUserSummary(Long userId);
}
```

## Phase 5: deploy independently
- each service has its own Docker build, deployment pipeline, and database migration scripts
- no shared package-level coupling remains

---

## 8. Validation rules

The split is considered complete only when these conditions are true:

1. No service owns another service's source-of-truth entity.
2. Every cross-service dependency uses DTOs or events.
3. Internal endpoints are versioned and documented.
4. Each module can run independently.
5. Each service has a separate migration and deployment path.
6. Security is enforced using a common JWT contract and service-specific authorization rules.

---

## 9. Recommended engineering standards

- use Java 17 records for DTOs
- use constructor injection everywhere
- use `@Transactional` only at the service layer
- use a common error contract from the common module
- avoid direct repository access from controllers
- use explicit API client interfaces for cross-service communication

---

## 10. Final recommendation

The best next implementation step is to treat this repository as a phased microservice migration, not a full immediate cutover.

Recommended practical path:
1. Freeze the current structure as the baseline.
2. Define the ownership boundaries and DTO contracts above.
3. Introduce internal service clients and DTO-based integration.
4. Remove duplicated Domain data ownership.
5. Then split deployment and database ownership per service.

This gives the team a realistic, low-risk migration plan while moving toward a true microservice-oriented system.
