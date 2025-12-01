package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    
    private Long id;
    private String classCode;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long semesterId;
    private String semesterName;
    private Long teacherId;
    private String teacherName;
    private Integer maxStudent;
    private Integer currentStudentCount;
    private LocalDateTime createdAt;
    private String status;
    private List<StudentInfo> students;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfo {
        private Long id;
        private String studentCode;
        private String fullName;
        private LocalDateTime registeredAt;
    }
}
