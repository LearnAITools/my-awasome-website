/**
 * Booking Domain Package.
 * 
 * This domain encapsulates all booking-related business logic and operations.
 * 
 * Layers:
 * - dto: Data Transfer Objects for API requests/responses
 *   * BookingRequestDto: Input for creating bookings
 *   * BookingResponseDto: Output with booking details
 * 
 * - service: Business logic and core operations
 *   * BookingDomainService: Core booking logic, seat reservations, cancellations
 *   * Handles concurrency control via optimistic locking
 *   * Enforces user isolation (users only access own bookings)
 * 
 * - controller: HTTP endpoints
 *   * Maps HTTP requests to BookingDomainService methods
 *   * Handles authentication and authorization
 * 
 * - repository: Database access layer
 *   * Interface for Booking entity CRUD operations
 *   * Custom queries: findByBookingReference, findByUserId, etc.
 * 
 * - model: Domain entity
 *   * Booking entity with JPA annotations
 *   * BookingStatus enum: PENDING, CONFIRMED, CANCELLED
 * 
 * Error Codes:
 * - ERR100: Booking not found
 * - ERR200: Seat already booked
 * - ERR203: Booking in invalid state
 * - ERR204: Concurrency conflict
 * 
 * @since 2026-05-30
 */
package org.website.domain.booking;
