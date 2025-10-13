package com.sms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatusRequest {
    
    @NotNull(message = "Trạng thái đăng ký không được để trống")
    private Boolean isOpen;
}
