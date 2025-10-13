package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterResponse {
    
    private Long id;
    private Long academicYearId;
    private String academicYearName;
    private String name;
    private Boolean isOpen;
    private LocalDate startDate;
    private LocalDate endDate;
}
