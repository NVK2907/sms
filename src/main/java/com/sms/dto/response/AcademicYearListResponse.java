package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYearListResponse {
    
    private List<AcademicYearResponse> academicYears;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
