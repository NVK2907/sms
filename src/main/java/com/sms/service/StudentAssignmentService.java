package com.sms.service;

import com.sms.dto.request.SubmissionRequest;
import com.sms.dto.response.StudentAssignmentResponse;

import java.util.List;

public interface StudentAssignmentService {
    List<StudentAssignmentResponse> getAssignmentsByStudent(Long studentId);
    List<StudentAssignmentResponse> getAssignmentsByClass(Long studentId, Long classId);
    StudentAssignmentResponse getAssignmentById(Long studentId, Long assignmentId);
    void submitAssignment(SubmissionRequest request);
    void updateSubmission(Long studentId, Long submissionId, SubmissionRequest request);
}
