package com.sms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TeacherResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String teacherCode;
    private String department;
    private String title;
    private String educationLevel;
    private Integer experienceYears;
    private String address;
    private LocalDate hireDate;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private List<SubjectInfo> subjects;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectInfo {
        private Long id;
        private String subjectCode;
        private String subjectName;
        private Integer credit;
    }
}
