package org.website.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.website.dto.AuthResponse;
import org.website.dto.EmailChangeRequest;
import org.website.dto.EmailChangeVerifyRequest;
import org.website.dto.UpdateProfileRequest;
import org.website.dto.UserProfileResponse;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.UserRepository;
import org.website.security.JwtTokenProvider;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(
        "a-secure-test-secret-that-is-at-least-32-bytes", 60_000
    );
    private final UserProfileService service = new UserProfileService(userRepository, tokenProvider);

    @Test
    void updateProfileTrimsNameAndClearsBlankPictureUrl() {
        User user = user();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserProfileResponse response = service.updateProfile(7L, new UpdateProfileRequest("  New Name  ", "  "));

        assertEquals("New Name", response.getFullName());
        assertNull(response.getProfilePictureUrl());
        verify(userRepository).save(user);
    }

    @Test
    void requestEmailChangeNormalizesEmailAndVerifyUpdatesUser() {
        User user = user();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        assertEquals("OTP sent to new@example.com for verification",
            service.requestEmailChangeOtp(7L, new EmailChangeRequest(" New@Example.com ")));

        Object pending = ((Map<?, ?>) ReflectionTestUtils.getField(service, "pendingEmailChanges")).get(7L);
        String otp = ReflectionTestUtils.invokeMethod(pending, "otp");
        AuthResponse response = service.verifyEmailChangeOtp(
            7L, new EmailChangeVerifyRequest("NEW@example.com", otp)
        );

        assertEquals("new@example.com", response.email());
        assertEquals("new@example.com", tokenProvider.getEmailFromToken(response.token()));
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void rejectsCurrentEmailAndAlreadyRegisteredEmail() {
        User user = user();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));
        EmailChangeRequest currentEmailRequest = new EmailChangeRequest(" USER@example.com ");
        EmailChangeRequest duplicateEmailRequest = new EmailChangeRequest("taken@example.com");

        assertThrows(IllegalArgumentException.class,
            () -> service.requestEmailChangeOtp(7L, currentEmailRequest));
        assertThrows(IllegalArgumentException.class,
            () -> service.requestEmailChangeOtp(7L, duplicateEmailRequest));
    }

    private User user() {
        User user = new User("user@example.com", "encoded", "Test User", UserRole.ROLE_USER);
        user.setId(7L);
        return user;
    }
}