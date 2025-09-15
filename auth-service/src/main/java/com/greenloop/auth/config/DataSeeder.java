package com.greenloop.auth.config;

import com.greenloop.auth.permission.entity.Permission;
import com.greenloop.auth.permission.repository.PermissionRepository;
import com.greenloop.auth.role.entity.Role;
import com.greenloop.auth.role.repository.RoleRepository;
import com.greenloop.auth.user.entity.User;
import com.greenloop.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedBasicData();
    }

    private void seedBasicData() {
        // Tạo permission cơ bản
        Permission readPerm = Permission.builder()
                .name("app:read")
                .displayName("Read Access")
                .resource("app")
                .action("read")
                .build();
        permissionRepository.save(readPerm);

        // Tạo role USER
        Role userRole = Role.builder()
                .name("USER")
                .displayName("Regular User")
                .isActive(true)
                .permissions(Set.of(readPerm))
                .build();
        roleRepository.save(userRole);

        // Tạo user test
        if (!userRepository.existsByUsername("testuser")) {
            User testUser = User.builder()
                    .username("testuser")
                    .email("test@test.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Test User")
                    .isEnabled(true)
                    .emailVerified(true)
                    .roles(Set.of(userRole))
                    .build();
            userRepository.save(testUser);
            log.info("Created test user: testuser/123456");
        }
    }
}
