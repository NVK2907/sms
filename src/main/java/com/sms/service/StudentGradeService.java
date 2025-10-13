package com.sms.service;

import com.sms.dto.response.StudentGradeResponse;

import java.util.List;

public interface StudentGradeService {
    List<StudentGradeResponse> getGradesByStudent(Long studentId);
    List<StudentGradeResponse> getGradesByStudentAndSemester(Long studentId, Long semesterId);
    StudentGradeResponse getGradeByClassAndStudent(Long studentId, Long classId);
    StudentGradeResponse.GPASummary getGPASummary(Long studentId);
}
