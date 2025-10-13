package com.sms.repository;

import com.sms.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    boolean existsByPermissionCode(String permissionCode);
}
