package com.greenloop.auth.auth.controller;

import com.greenloop.auth.auth.dto.request.LoginRequest;
import com.greenloop.auth.auth.dto.request.RegisterRequest;
import com.greenloop.auth.auth.dto.response.LoginResponse;
import com.greenloop.auth.auth.dto.response.RegisterResponse;
import com.greenloop.auth.auth.service.AuthService;
import com.greenloop.common.dto.ApiResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("Login request received for email: {}", request.getEmail());

            LoginResponse loginResponse = authService.login(request);
            ApiResponseDTO<LoginResponse> response = ApiResponseDTO.success(loginResponse, "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            ApiResponseDTO<LoginResponse> errorResponse = ApiResponseDTO.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Register request received for email: {}", request.getEmail());

            RegisterResponse registerResponse = authService.register(request);
            ApiResponseDTO<RegisterResponse> response = ApiResponseDTO.success(registerResponse, "Registration successful");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            ApiResponseDTO<RegisterResponse> errorResponse = ApiResponseDTO.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
