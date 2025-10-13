package com.sms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequest {
    
    @NotBlank(message = "Mã môn học không được để trống")
    @Size(max = 20, message = "Mã môn học không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Mã môn học chỉ được chứa chữ hoa và số")
    private String subjectCode;
    
    @NotBlank(message = "Tên môn học không được để trống")
    @Size(max = 100, message = "Tên môn học không được vượt quá 100 ký tự")
    private String subjectName;
    
    @NotNull(message = "Số tín chỉ không được để trống")
    private Integer credit;
    
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;
}
