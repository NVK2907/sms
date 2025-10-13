package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private Integer submissionCount;
    private Integer gradedCount;
}
