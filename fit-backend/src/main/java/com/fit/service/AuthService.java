package com.fit.service;

import com.fit.dto.AuthResponse;
import com.fit.dto.LoginRequest;
import com.fit.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(Long userId);
}