package com.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    
    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;
    
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Size(max = 20, message = "Mã sinh viên không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Mã sinh viên chỉ được chứa chữ hoa và số")
    private String studentCode;
    
    @Pattern(regexp = "^(Nam|Nữ|Khác)$", message = "Giới tính phải là Nam, Nữ hoặc Khác")
    private String gender;
    
    private LocalDate dob;
    
    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;
    
    @Size(max = 50, message = "Tên lớp không được vượt quá 50 ký tự")
    private String className;
    
    @Size(max = 100, message = "Chuyên ngành không được vượt quá 100 ký tự")
    private String major;
    
    @NotNull(message = "Khóa học không được để trống")
    private Integer courseYear;
}
