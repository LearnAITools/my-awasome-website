package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.AuthResponse;
import org.website.dto.LoginRequest;
import org.website.dto.SignupRequest;
import org.website.service.AuthService;

/**
 * Authentication Controller - Handles user authentication operations.
 * 
 * This controller manages user registration (signup) and login operations.
 * It is publicly accessible without authentication and provides JWT tokens
 * for authenticated users.
 * 
 * Endpoints:
 * - POST /api/auth/signup - Register a new user
 * - POST /api/auth/login - Authenticate existing user
 * 
 * Security: These endpoints are PUBLIC and do NOT require authentication.
 * All other endpoints require a valid JWT token in the Authorization header.
 * 
 * @author BookMyShow Dev Team
 * @version 1.0
 * @since 2026-05-30
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;

    }

    /**
     * User Registration/Signup Endpoint
     * 
     * Registers a new user in the system with email and password.
     * Creates a new user account if the email is not already registered.
     * 
     * @param request SignupRequest containing email, password, and fullName
     * @return AuthResponse containing JWT token, userId, and user details
     * @throws IllegalArgumentException if email already exists
     * @throws IllegalArgumentException if validation fails
     * 
     * Example Request:
     * {
     *   "email": "user@example.com",
     *   "password": "SecurePassword123",
     *   "fullName": "John Doe"
     * }
     * 
     * Example Response (201):
     * {
     *   "token": "eyJhbGc...",
     *   "message": "User registered successfully",
     *   "userId": 1,
     *   "email": "user@example.com",
     *   "fullName": "John Doe",
     *   "role": "ROLE_USER"
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        log.info("Signup attempt for email: {}", request.email());
        AuthResponse response = authService.signup(request);
        log.info("User registered successfully: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User Login Endpoint
     * 
     * Authenticates a user with email and password credentials.
     * Returns a JWT token that must be included in Authorization header for subsequent requests.
     * 
     * @param request LoginRequest containing email and password
     * @return AuthResponse containing JWT token and user details
     * @throws IllegalArgumentException if credentials are invalid
     * @throws IllegalArgumentException if user not found
     * 
     * Example Request:
     * {
     *   "email": "user@example.com",
     *   "password": "SecurePassword123"
     * }
     * 
     * Example Response (200):
     * {
     *   "token": "eyJhbGc...",
     *   "message": "Login successful",
     *   "userId": 1,
     *   "email": "user@example.com",
     *   "fullName": "John Doe",
     *   "role": "ROLE_USER"
     * }
     * 
     * Error Response (401):
     * {
     *   "error_code": "ERR001",
     *   "message": "Invalid email or password",
     *   "status": 401,
     *   "error_type": "AUTH_INVALID_CREDENTIALS",
     *   "timestamp": "2026-05-30T10:30:00"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully: {}", request.email());
        return ResponseEntity.ok(response);
    }
}

