package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {
    
    private Long id;
    private String subjectCode;
    private String subjectName;
    private Integer credit;
    private String description;
}
