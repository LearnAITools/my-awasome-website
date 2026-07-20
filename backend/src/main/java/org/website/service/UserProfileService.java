package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.AuthResponse;
import org.website.dto.EmailChangeRequest;
import org.website.dto.EmailChangeVerifyRequest;
import org.website.dto.UpdateProfileRequest;
import org.website.dto.UserProfileResponse;
import org.website.exception.ResourceNotFoundException;
import org.website.model.User;
import org.website.repository.UserRepository;
import org.website.security.JwtTokenProvider;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional
public class UserProfileService {
    private static final int OTP_TTL_MINUTES = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final Map<Long, PendingEmailChange> pendingEmailChanges = new ConcurrentHashMap<>();

    UserProfileService(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    public UserProfileResponse getCurrentUser(Long userId) {
        return toProfileResponse(findUser(userId));
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl().trim().isEmpty() ? null : request.getProfilePictureUrl().trim());
        }

        return toProfileResponse(userRepository.save(user));
    }

    public String requestEmailChangeOtp(Long userId, EmailChangeRequest request) {
        User user = findUser(userId);
        String newEmail = normalizeEmail(request.getNewEmail());

        if (newEmail.equalsIgnoreCase(user.getEmail())) {
            throw new IllegalArgumentException("New email must be different from current email");
        }
        userRepository.findByEmail(newEmail).ifPresent(existing -> {
            throw new IllegalArgumentException("Email already exists");
        });

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        pendingEmailChanges.put(userId, new PendingEmailChange(newEmail, otp, LocalDateTime.now().plusMinutes(OTP_TTL_MINUTES)));
        log.info("[PROFILE] Email change OTP for user {} to {} is {}. It expires in {} minutes.", userId, newEmail, otp, OTP_TTL_MINUTES);
        return "OTP sent to " + newEmail + " for verification";
    }

    public AuthResponse verifyEmailChangeOtp(Long userId, EmailChangeVerifyRequest request) {
        User user = findUser(userId);
        String newEmail = normalizeEmail(request.getNewEmail());
        PendingEmailChange pending = pendingEmailChanges.get(userId);

        if (pending == null || !pending.newEmail().equalsIgnoreCase(newEmail)) {
            throw new IllegalArgumentException("No pending OTP request found for this email");
        }
        if (pending.expiresAt().isBefore(LocalDateTime.now())) {
            pendingEmailChanges.remove(userId);
            throw new IllegalArgumentException("OTP has expired");
        }
        if (!pending.otp().equals(request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        userRepository.findByEmail(newEmail).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new IllegalArgumentException("Email already exists");
            }
        });

        user.setEmail(newEmail);
        User savedUser = userRepository.save(user);
        pendingEmailChanges.remove(userId);
        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        return new AuthResponse(
            token,
            "Email updated successfully",
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getFullName(),
            savedUser.getRole().name()
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getProfilePictureUrl()
        );
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        return email.trim().toLowerCase();
    }

    private record PendingEmailChange(String newEmail, String otp, LocalDateTime expiresAt) {
    }
}
