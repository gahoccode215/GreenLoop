package com.greenloop.auth.config;

import com.greenloop.auth.permission.entity.Permission;
import com.greenloop.auth.permission.repository.PermissionRepository;
import com.greenloop.auth.role.entity.Role;
import com.greenloop.auth.role.repository.RoleRepository;
import com.greenloop.common.constant.PermissionConstants;
import com.greenloop.common.constant.RoleConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        try {
            initializeRolesAndPermissions();
            log.info("Application initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during application initialization: {}", e.getMessage());
        }
    }

    private void initializeRolesAndPermissions() {
        // Create basic permissions using constants
        Permission userRead = createPermissionIfNotExists(
                PermissionConstants.USER_READ, PermissionConstants.USER_READ_DISPLAY,
                PermissionConstants.USER_RESOURCE, PermissionConstants.READ_ACTION);

        Permission userWrite = createPermissionIfNotExists(
                PermissionConstants.USER_WRITE, PermissionConstants.USER_WRITE_DISPLAY,
                PermissionConstants.USER_RESOURCE, PermissionConstants.WRITE_ACTION);

        Permission adminRead = createPermissionIfNotExists(
                PermissionConstants.ADMIN_READ, PermissionConstants.ADMIN_READ_DISPLAY,
                PermissionConstants.ADMIN_RESOURCE, PermissionConstants.READ_ACTION);

        Permission adminWrite = createPermissionIfNotExists(
                PermissionConstants.ADMIN_WRITE, PermissionConstants.ADMIN_WRITE_DISPLAY,
                PermissionConstants.ADMIN_RESOURCE, PermissionConstants.WRITE_ACTION);

        // Create roles using constants
        createRoleIfNotExists(
                RoleConstants.CUSTOMER, RoleConstants.CUSTOMER_DISPLAY,
                RoleConstants.CUSTOMER_DESC, Set.of(userRead));

        createRoleIfNotExists(
                RoleConstants.ADMIN, RoleConstants.ADMIN_DISPLAY,
                RoleConstants.ADMIN_DESC, Set.of(userRead, userWrite, adminRead, adminWrite));

        log.info("Roles and permissions initialized");
    }

    private Permission createPermissionIfNotExists(String name, String displayName, String resource, String action) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = Permission.builder()
                    .name(name)
                    .displayName(displayName)
                    .resource(resource)
                    .action(action)
                    .build();
            Permission saved = permissionRepository.save(permission);
            log.info("Created permission: {}", name);
            return saved;
        });
    }

    private Role createRoleIfNotExists(String name, String displayName, String description, Set<Permission> permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .permissions(permissions)
                    .build();
            Role saved = roleRepository.save(role);
            log.info("Created role: {}", name);
            return saved;
        });
    }
}
