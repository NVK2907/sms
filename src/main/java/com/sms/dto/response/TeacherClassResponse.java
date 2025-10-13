package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String semesterName;
    private Integer maxStudent;
    private Integer currentStudentCount;
    private LocalDateTime createdAt;
    private List<StudentInClassResponse> students;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInClassResponse {
        private Long studentId;
        private String studentCode;
        private String studentName;
        private String email;
        private String className;
        private String major;
        private Integer courseYear;
    }
}
