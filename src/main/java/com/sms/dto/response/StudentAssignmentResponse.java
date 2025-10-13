package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAssignmentResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private Boolean isSubmitted;
    private LocalDateTime submittedAt;
    private Float score;
    private Boolean isGraded;
}
