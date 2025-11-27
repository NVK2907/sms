package com.sms.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateRequest {
    
    @Size(max = 100, message = "Khoa/Bộ môn không được vượt quá 100 ký tự")
    private String department;
    
    @Size(max = 50, message = "Chức danh không được vượt quá 50 ký tự")
    private String title;
    
    @Size(max = 100, message = "Trình độ không được vượt quá 100 ký tự")
    private String educationLevel;
    
    private Integer experienceYears;
    
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;
    
    private LocalDate hireDate;
    
    private List<Long> subjectIds;
}
