package org.website.service;

import org.website.dto.AuthResponse;
import org.website.dto.LoginRequest;
import org.website.dto.SignupRequest;
import org.website.model.User;

public interface AuthServicePort {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);

    User getUserById(Long userId);
}
