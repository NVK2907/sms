package com.sms.repository;

import com.sms.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermissionId(Long permissionId);
    
    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
    
    List<RolePermission> findByRoleIdAndIsActiveTrue(Long roleId);
    
    void deleteByRoleId(Long roleId);
    
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
