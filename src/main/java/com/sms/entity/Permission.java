package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "permission_code", unique = true, nullable = false, length = 100)
    private String permissionCode;
    
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
