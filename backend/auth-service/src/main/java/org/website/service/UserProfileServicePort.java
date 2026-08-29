package org.website.service;

import org.website.dto.AuthResponse;
import org.website.dto.EmailChangeRequest;
import org.website.dto.EmailChangeVerifyRequest;
import org.website.dto.UpdateProfileRequest;
import org.website.dto.UserProfileResponse;

public interface UserProfileServicePort {
    UserProfileResponse getCurrentUser(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    String requestEmailChangeOtp(Long userId, EmailChangeRequest request);

    AuthResponse verifyEmailChangeOtp(Long userId, EmailChangeVerifyRequest request);
}
