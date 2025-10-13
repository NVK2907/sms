package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchRequest {
    
    private String keyword;
    private String className;
    private String major;
    private Integer courseYear;
    private String gender;
}
