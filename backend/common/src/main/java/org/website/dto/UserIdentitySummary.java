package org.website.dto;

public record UserIdentitySummary(
    Long userId,
    String email,
    String fullName,
    String role,
    boolean active
) {}
