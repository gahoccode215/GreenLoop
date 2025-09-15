package com.greenloop.auth.auth.controller;

import com.greenloop.auth.auth.dto.LoginRequest;
import com.greenloop.auth.auth.dto.LoginResponse;
import com.greenloop.auth.auth.dto.RegisterRequest;
import com.greenloop.auth.auth.service.AuthService;
import com.greenloop.auth.auth.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
