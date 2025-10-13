package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoleInfo> roles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Long id;
        private String roleName;
        private String description;
        private LocalDateTime assignedAt;
    }
}
