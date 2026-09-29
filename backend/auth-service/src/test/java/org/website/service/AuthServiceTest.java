package org.website.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.website.dto.AuthResponse;
import org.website.dto.LoginRequest;
import org.website.dto.SignupRequest;
import org.website.exception.ResourceNotFoundException;
import org.website.exception.UnauthorizedException;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.UserRepository;
import org.website.security.JwtTokenProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(
        "a-secure-test-secret-that-is-at-least-32-bytes", 60_000
    );
    private final AuthService authService = new AuthService(userRepository, passwordEncoder, tokenProvider);

    @Test
    void signupEncodesPasswordAndReturnsAuthResponse() {
        SignupRequest request = new SignupRequest("user@example.com", "plain-password", "Test User");
        User savedUser = new User("user@example.com", "encoded-password", "Test User", UserRole.ROLE_USER);
        savedUser.setId(7L);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.signup(request);

        assertEquals("user@example.com", tokenProvider.getEmailFromToken(response.token()));
        assertEquals(7L, response.userId());
        assertEquals("ROLE_USER", response.role());
        verify(passwordEncoder).encode("plain-password");
    }

    @Test
    void rejectsDuplicateSignupEmail() {
        when(userRepository.findByEmail("user@example.com"))
            .thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.signup(new SignupRequest("user@example.com", "password", "Test User"))
        );

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void rejectsInvalidLoginPassword() {
        User user = new User("user@example.com", "encoded-password", "Test User", UserRole.ROLE_USER);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThrows(
            UnauthorizedException.class,
            () -> authService.login(new LoginRequest("user@example.com", "wrong"))
        );
    }

    @Test
    void reportsMissingUserById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> authService.getUserById(99L)
        );

        assertEquals("User not found with ID: 99", exception.getMessage());
    }
}
