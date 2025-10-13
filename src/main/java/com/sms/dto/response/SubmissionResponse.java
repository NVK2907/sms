package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private String assignmentTitle;
    private String studentCode;
    private String studentName;
    private String filePath;
    private String fileName;
    private LocalDateTime submittedAt;
    private Float score;
    private Boolean isGraded;
}
