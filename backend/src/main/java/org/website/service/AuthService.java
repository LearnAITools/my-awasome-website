package org.website.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dto.AuthResponse;
import org.website.dto.LoginRequest;
import org.website.dto.SignupRequest;
import org.website.exception.UnauthorizedException;
import org.website.exception.ResourceNotFoundException;
import org.website.model.User;
import org.website.model.UserRole;
import org.website.repository.UserRepository;
import org.website.security.JwtTokenProvider;

@Service
@Slf4j
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(UserRole.ROLE_USER);

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());

        log.info("New user registered: {}", savedUser.getEmail());

        return new AuthResponse(
            token,
            "User registered successfully",
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getFullName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for user: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getId());

        log.info("User logged in: {}", user.getEmail());

        return new AuthResponse(
            token,
            "Login successful",
            user.getId(),
            user.getEmail(),
            user.getFullName()
        );
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}
