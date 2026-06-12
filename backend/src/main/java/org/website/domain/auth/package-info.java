/**
 * Authentication & Authorization Domain Package.
 * 
 * This domain encapsulates user authentication, registration, and access control.
 * Implements JWT-based authentication with Spring Security.
 * 
 * Layers:
 * - dto: Data Transfer Objects for auth operations
 *   * LoginRequest: Email and password for login
 *   * SignupRequest: User details for registration
 *   * AuthResponse: JWT token and user info on success
 * 
 * - service: Authentication business logic
 *   * AuthDomainService: User registration and authentication
 *   * signup() - Create new user account
 *   * login() - Authenticate user and return JWT token
 *   * validateToken() - Verify JWT token validity
 * 
 * - controller: HTTP endpoints
 *   * POST /api/auth/signup - Register new user
 *   * POST /api/auth/login - Login and get JWT token
 *   * POST /api/auth/logout - Invalidate token (optional)
 * 
 * - repository: Database access layer
 *   * Interface for User entity CRUD operations
 *   * Custom queries: findByEmail, existsByEmail, etc.
 * 
 * - model: Domain entity
 *   * User entity with JPA annotations
 *   * UserRole enum: ADMIN, USER
 * 
 * JWT Token:
 * - Issued on successful login
 * - Contains userId, email, roles
 * - Expires after configured duration (e.g., 24 hours)
 * - Used for all subsequent requests in Authorization header
 * - Format: "Authorization: Bearer {token}"
 * 
 * Security:
 * - Passwords stored as bcrypt hashes (never plain text)
 * - Email uniqueness enforced
 * - Token-based stateless authentication
 * - Role-based access control (RBAC) for admin endpoints
 * 
 * Error Codes:
 * - ERR001: Invalid credentials
 * - ERR006: User not found
 * - ERR007: Email already registered
 * - ERR002: Unauthorized access
 * - ERR403: Insufficient permissions
 * 
 * @since 2026-05-30
 */
package org.website.domain.auth;
