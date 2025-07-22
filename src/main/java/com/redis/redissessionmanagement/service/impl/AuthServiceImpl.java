package com.redis.redissessionmanagement.service.impl;

import com.redis.redissessionmanagement.dto.request.AuthRequest;
import com.redis.redissessionmanagement.dto.response.AuthResponse;
import com.redis.redissessionmanagement.dto.request.RegisterRequest;
import com.redis.redissessionmanagement.entity.User;
import com.redis.redissessionmanagement.enumarate.Role;
import com.redis.redissessionmanagement.exception.EmailAlreadyExistsException;
import com.redis.redissessionmanagement.exception.InvalidAuthorizationHeaderException;
import com.redis.redissessionmanagement.exception.UserNotFoundException;
import com.redis.redissessionmanagement.repository.UserRepository;
import com.redis.redissessionmanagement.security.JwtService;
import com.redis.redissessionmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.email());
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(new AuthResponse(token));
    }


    @Override
    public ResponseEntity<String> logout(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidAuthorizationHeaderException("Token boş olamaz");
        }
        jwtService.validateToken(token);
        jwtService.invalidateToken(token);
        return ResponseEntity.ok("Successfully logged out");
    }

}

