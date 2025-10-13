package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String address;
    private String className;
    private String major;
    private Integer courseYear;
}