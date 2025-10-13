package com.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRequest {
    
    @NotNull(message = "ID năm học không được để trống")
    private Long academicYearId;
    
    @NotBlank(message = "Tên học kỳ không được để trống")
    @Size(max = 20, message = "Tên học kỳ không được vượt quá 20 ký tự")
    private String name;
    
    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;
    
    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;
    
    private Boolean isOpen = true;
}
