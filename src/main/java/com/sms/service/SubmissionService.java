package com.sms.service;

import com.sms.dto.request.SubmissionGradeRequest;
import com.sms.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId);
    List<SubmissionResponse> getSubmissionsByStudent(Long studentId);
    SubmissionResponse gradeSubmission(SubmissionGradeRequest request);
    SubmissionResponse getSubmissionById(Long submissionId);
    List<SubmissionResponse> getUngradedSubmissions(Long assignmentId);
}
