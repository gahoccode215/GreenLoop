package com.greenloop.auth.permission.repository;

import com.greenloop.auth.permission.entity.Permission;
import com.greenloop.auth.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
