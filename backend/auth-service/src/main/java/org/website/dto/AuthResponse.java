package org.website.dto;

public record AuthResponse(
    String token,
    String message,
    Long userId,
    String email,
    String fullName,
    String role
) {}
