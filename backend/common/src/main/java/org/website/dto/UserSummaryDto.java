package org.website.dto;

public record UserSummaryDto(
    Long userId,
    String email,
    String fullName,
    String role
) {}
