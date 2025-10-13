package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearResponse {
    
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<SemesterInfo> semesters;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterInfo {
        private Long id;
        private String name;
        private Boolean isOpen;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
