package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private String studentCode;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
    private String className;
    private String major;
    private Integer courseYear;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
