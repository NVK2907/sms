package com.sms.service;

import com.sms.dto.request.AssignmentRequest;
import com.sms.dto.response.AssignmentResponse;

import java.util.List;

public interface AssignmentService {
    AssignmentResponse createAssignment(AssignmentRequest request);
    AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request);
    void deleteAssignment(Long assignmentId);
    List<AssignmentResponse> getAssignmentsByClass(Long classId);
    List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId);
    AssignmentResponse getAssignmentById(Long assignmentId);
}
