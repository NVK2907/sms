package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String subjectCode;
    private Integer credits;
    private Float midterm;
    private Float finalGrade;
    private Float other;
    private Float total;
    private String letterGrade;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GPASummary {
        private Double gpa;
        private Integer totalCredits;
        private Integer completedCredits;
        private List<StudentGradeResponse> grades;
    }
}