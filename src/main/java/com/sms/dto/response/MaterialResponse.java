package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String title;
    private String filePath;
    private String fileName;
    private LocalDateTime uploadedAt;
}
