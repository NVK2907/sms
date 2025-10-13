package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassListResponse {
    
    private List<ClassResponse> classes;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
