package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String studentCode;
    private String gender;
    private LocalDate dob;
    private String address;
    private String className;
    private String major;
    private Integer courseYear;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<ClassInfo> classes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassInfo {
        private Long id;
        private String classCode;
        private String subjectName;
        private String teacherName;
        private LocalDateTime registeredAt;
    }
}
