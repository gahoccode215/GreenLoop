package com.greenloop.auth.auth.service.impl;

import com.greenloop.auth.auth.dto.LoginRequest;
import com.greenloop.auth.auth.dto.LoginResponse;
import com.greenloop.auth.auth.service.AuthService;
import com.greenloop.auth.auth.service.JwtService;
import com.greenloop.auth.user.entity.User;
import com.greenloop.auth.user.repository.UserRepository;
import com.greenloop.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", email);
        return userRepository.findByUsername(email)
                .or(() -> userRepository.findByEmail(email))
                .orElseThrow(() -> {
                    log.warn("User not found: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getEmail());

        // 1. Load user (username OR email)
        User user = (User) loadUserByUsername(loginRequest.getEmail());

        // 2. Verify credentials
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid username or password");
        }
        if (!user.isEnabled()) {
            log.warn("Account disabled for: {}", loginRequest.getEmail());
            throw new RuntimeException("Account is disabled");
        }

        // 3. Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Long expiresIn = jwtService.getAccessTokenExpirationSeconds();

        // 4. Build response (extend LoginResponse with builder)
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(user.getId())
                .build();
    }
}
