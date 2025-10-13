package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "role_id", nullable = false)
    private Long roleId;
    
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;
    
    @Column(name = "assigned_by")
    private Long assignedBy;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
