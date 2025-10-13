package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    private Long classId;
    private Long teacherId;
    private String title;
    private String description;
    private LocalDateTime deadline;
}
