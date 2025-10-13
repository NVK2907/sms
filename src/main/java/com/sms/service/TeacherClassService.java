package com.sms.service;

import com.sms.dto.response.TeacherClassResponse;

import java.util.List;

public interface TeacherClassService {
    List<TeacherClassResponse> getClassesByTeacher(Long teacherId);
    List<TeacherClassResponse> getClassesByTeacherAndSemester(Long teacherId, Long semesterId);
    TeacherClassResponse getClassWithStudents(Long classId);
}
