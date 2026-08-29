# Phased Migration Cleanup Plan

## Phase 1: Freeze the service contract boundary

Objectives:
- define explicit ownership by service
- keep public APIs stable
- add internal DTO contract layer
- keep adapters behind interfaces

Outcomes:
- Auth, Booking, and Payment are clearly separated by business responsibility.
- cross-service communication uses DTOs only
- no service directly depends on another service's JPA entities

## Phase 2: Introduce HTTP adapters and client interfaces

Objectives:
- replace ad hoc logic with explicit service adapters
- create REST client boundaries for inter-service integration

Examples:
- UserIdentityAdapter
- BookingStatusAdapter
- auth-service internal user summary endpoint
- booking-service payment status endpoint

Outcomes:
- real service-to-service calls are isolated behind adapters
- failure handling becomes explicit and centralized
- external dependencies are easier to mock and test

## Phase 3: Remove duplicate model ownership

Objectives:
- eliminate duplicate business entities across modules
- keep one authoritative model per service

Actions:
- move authoritative booking entities to booking-service only
- move authoritative payment entities to payment-service only
- move authoritative user identity entities to auth-service only
- keep shared DTOs in common for cross-service contracts only

Outcomes:
- no cross-service domain duplication
- each service owns its persistence boundary

## Phase 4: Create service-specific API ownership policy

Rules:
- public endpoints remain user-facing and authenticated
- internal endpoints are reserved for service-to-service coordination
- DTOs become the only allowed contract payload type
- JPA entities are never exposed across module boundaries

## Phase 5: Add resilience and fault tolerance

Add:
- retry policies for transient request failures
- timeout configuration on HTTP adapters
- circuit breakers for unhealthy downstream services
- structured logging for inter-service failures

## Phase 6: Independent deployment readiness

Before moving to independent deployment:
- separate databases or at least separate schema ownership
- dedicated build pipelines per service
- independent application config
- service discovery or gateway routing strategy

## Phase 7: Full microservice cutover

When the above phases are complete, the system can be decomposed into independent runtime deployments without breaking internal ownership rules.

This should be done only after the domain ownership cleanup is complete, because the main risk in this project is not HTTP wiring—it is duplicate business domain ownership.
