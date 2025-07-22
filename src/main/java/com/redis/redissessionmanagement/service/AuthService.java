package com.redis.redissessionmanagement.service;

import com.redis.redissessionmanagement.dto.request.AuthRequest;
import com.redis.redissessionmanagement.dto.response.AuthResponse;
import com.redis.redissessionmanagement.dto.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> register(RegisterRequest request);

    ResponseEntity<AuthResponse> login(AuthRequest request);

    ResponseEntity<String> logout(String token);
}
