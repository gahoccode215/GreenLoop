package com.greenloop.auth.auth.service;

import com.greenloop.auth.auth.dto.LoginRequest;
import com.greenloop.auth.auth.dto.LoginResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService  extends UserDetailsService {

    LoginResponse login(LoginRequest loginRequest);
}
