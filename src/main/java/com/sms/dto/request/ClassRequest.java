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
public class ClassRequest {
    
    @NotBlank(message = "Mã lớp không được để trống")
    @Size(max = 20, message = "Mã lớp không được vượt quá 20 ký tự")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Mã lớp chỉ được chứa chữ hoa và số")
    private String classCode;
    
    @NotNull(message = "ID môn học không được để trống")
    private Long subjectId;
    
    @NotNull(message = "ID học kỳ không được để trống")
    private Long semesterId;
    
    private Long teacherId;
    
    private Integer maxStudent = 50;
}
