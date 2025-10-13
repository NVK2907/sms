package com.sms.service;

import com.sms.dto.response.StudentMaterialResponse;

import java.util.List;

public interface StudentMaterialService {
    List<StudentMaterialResponse> getMaterialsByStudent(Long studentId);
    List<StudentMaterialResponse> getMaterialsByClass(Long studentId, Long classId);
}
