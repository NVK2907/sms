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
public class ClassAssignmentRequest {
    
    @NotNull(message = "ID sinh viên không được để trống")
    private Long studentId;
    
    @NotEmpty(message = "Danh sách lớp không được để trống")
    private List<Long> classIds;
}
