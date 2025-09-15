package com.greenloop.auth.auth.service;

import com.greenloop.auth.auth.dto.request.LoginRequest;
import com.greenloop.auth.auth.dto.response.LoginResponse;
import com.greenloop.auth.auth.dto.request.RegisterRequest;
import com.greenloop.auth.auth.dto.response.RegisterResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService  extends UserDetailsService {

    LoginResponse login(LoginRequest loginRequest);
    RegisterResponse register(RegisterRequest registerRequest);
}
