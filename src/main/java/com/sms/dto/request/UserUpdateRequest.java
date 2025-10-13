package com.sms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    private String email;
    
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String fullName;
    
    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Số điện thoại không hợp lệ")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    private String phone;
    
    private Boolean isActive;
    
    private List<Long> roleIds;
}
