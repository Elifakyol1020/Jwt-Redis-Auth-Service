package com.redis.redissessionmanagement.controller;

import com.redis.redissessionmanagement.dto.request.AuthRequest;
import com.redis.redissessionmanagement.dto.request.RegisterRequest;
import com.redis.redissessionmanagement.dto.response.AuthResponse;
import com.redis.redissessionmanagement.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Başarıyla çıkış yapıldı");
    }

}
