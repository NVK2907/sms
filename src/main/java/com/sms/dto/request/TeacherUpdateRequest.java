package com.sms.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateRequest {
    
    @Size(max = 100, message = "Khoa/Bộ môn không được vượt quá 100 ký tự")
    private String department;
    
    @Size(max = 50, message = "Chức danh không được vượt quá 50 ký tự")
    private String title;
    
    private List<Long> subjectIds;
}
