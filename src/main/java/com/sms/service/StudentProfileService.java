package com.sms.service;

import com.sms.dto.request.ChangePasswordRequest;
import com.sms.dto.request.StudentUpdateRequest;
import com.sms.dto.response.StudentProfileResponse;

public interface StudentProfileService {
    StudentProfileResponse getStudentProfile(Long studentId);
    StudentProfileResponse updateStudentProfile(Long studentId, StudentUpdateRequest request);
    void changePassword(Long studentId, ChangePasswordRequest request);
}
