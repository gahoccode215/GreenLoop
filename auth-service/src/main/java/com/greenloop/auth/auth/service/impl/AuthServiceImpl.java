package com.greenloop.auth.auth.service.impl;

import com.greenloop.auth.auth.dto.request.LoginRequest;
import com.greenloop.auth.auth.dto.response.LoginResponse;
import com.greenloop.auth.auth.dto.request.RegisterRequest;
import com.greenloop.auth.auth.dto.response.RegisterResponse;
import com.greenloop.auth.auth.service.AuthService;
import com.greenloop.auth.role.entity.Role;
import com.greenloop.auth.role.repository.RoleRepository;
import com.greenloop.auth.security.service.JwtService;
import com.greenloop.auth.user.entity.User;
import com.greenloop.auth.user.enums.UserStatus;
import com.greenloop.auth.user.enums.UserType;
import com.greenloop.auth.user.repository.UserRepository;
import com.greenloop.common.constant.RoleConstants;
import com.greenloop.common.exception.RegistrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getEmail());

        User user = (User) loadUserByUsername(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
//        if (!user.isEnabled()) {
//            log.warn("Account disabled for: {}", loginRequest.getEmail());
//            throw new RuntimeException("Account is disabled");
//        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Long expiresIn = jwtService.getAccessTokenExpirationSeconds();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(user.getId())
                .build();
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.info("Register attempt for: {}", registerRequest.getEmail());

        // Check if user exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("User already exists: {}", registerRequest.getEmail());
            throw new RegistrationException("User already exists with email: " + registerRequest.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .userType(UserType.CUSTOMER)
                .status(UserStatus.INACTIVE) // Email verification required
                .isEnabled(true)
                .emailVerified(false)
                .build();
        Role customerRole = roleRepository.findByName(RoleConstants.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Customer role not found"));
        user.getRoles().add(customerRole);
        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        // TODO: Send verification email

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }
}
