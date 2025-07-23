package com.redis.redissessionmanagement.service.impl;

import com.redis.redissessionmanagement.dto.request.AuthRequest;
import com.redis.redissessionmanagement.dto.response.AuthResponse;
import com.redis.redissessionmanagement.dto.request.RegisterRequest;
import com.redis.redissessionmanagement.entity.User;
import com.redis.redissessionmanagement.enumarate.Role;
import com.redis.redissessionmanagement.exception.*;
import com.redis.redissessionmanagement.repository.UserRepository;
import com.redis.redissessionmanagement.security.JwtService;
import com.redis.redissessionmanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        if (!bCryptPasswordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("Oturum bulunamadı");
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedException("Request context bulunamadı");
        }

        String authHeader = attributes.getRequest().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Geçersiz token formatı");
        }

        String token = authHeader.substring(7);
        if (token.isBlank()) {
            throw new InvalidAuthorizationHeaderException("Token boş olamaz");
        }
        jwtService.validateToken(token);
        jwtService.invalidateToken(token);
        SecurityContextHolder.clearContext();
    }

}

