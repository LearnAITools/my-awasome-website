package org.website.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.dto.AuthResponse;
import org.website.dto.EmailChangeRequest;
import org.website.dto.EmailChangeVerifyRequest;
import org.website.dto.UpdateProfileRequest;
import org.website.dto.UserProfileResponse;
import org.website.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserProfileController {
    private final UserProfileService userProfileService;

    UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(userProfileService.getCurrentUser(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestAttribute("userId") Long userId,
            @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @PostMapping("/me/email/request-otp")
    public ResponseEntity<String> requestEmailChangeOtp(
            @RequestAttribute("userId") Long userId,
            @RequestBody EmailChangeRequest request) {
        String message = userProfileService.requestEmailChangeOtp(userId, request);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/me/email/verify-otp")
    public ResponseEntity<AuthResponse> verifyEmailChangeOtp(
            @RequestAttribute("userId") Long userId,
            @RequestBody EmailChangeVerifyRequest request) {
        return ResponseEntity.ok(userProfileService.verifyEmailChangeOtp(userId, request));
    }
}
