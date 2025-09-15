package com.greenloop.auth.permission.repository;

import com.greenloop.auth.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Set<Permission> findByNameIn(List<String> names);
    Set<Permission> findByResourceIn(List<String> resources);
    boolean existsByName(String name);
    List<Permission> findByResource(String resource);
}
