package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;
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
