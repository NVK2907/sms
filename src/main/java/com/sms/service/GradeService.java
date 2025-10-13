package com.sms.service;

import com.sms.dto.request.StudentGradeRequest;
import com.sms.dto.response.StudentGradeResponse;

import java.util.List;

public interface GradeService {
    StudentGradeResponse createOrUpdateGrade(StudentGradeRequest request);
    List<StudentGradeResponse> getGradesByClass(Long classId);
    List<StudentGradeResponse> getGradesByStudent(Long studentId);
    StudentGradeResponse getGradeByClassAndStudent(Long classId, Long studentId);
    void deleteGrade(Long gradeId);
    List<StudentGradeResponse> exportGradesByClass(Long classId);
}
