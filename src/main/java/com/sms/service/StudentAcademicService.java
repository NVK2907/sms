package com.sms.service;

import com.sms.dto.request.ClassRegistrationRequest;
import com.sms.dto.response.StudentClassResponse;

import java.util.List;

public interface StudentAcademicService {
    List<StudentClassResponse> getAvailableClasses(Long studentId);
    List<StudentClassResponse> getRegisteredClasses(Long studentId);
    List<StudentClassResponse> getRegisteredClassesBySemester(Long studentId, Long semesterId);
    StudentClassResponse getClassDetails(Long studentId, Long classId);
    void registerForClass(ClassRegistrationRequest request);
    void unregisterFromClass(Long studentId, Long classId);
}
