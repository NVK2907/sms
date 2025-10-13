package com.sms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentRequest {
    
    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;
    
    @NotEmpty(message = "Danh sách vai trò không được để trống")
    private List<Long> roleIds;
}
