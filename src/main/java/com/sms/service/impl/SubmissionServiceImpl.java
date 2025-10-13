package com.sms.service.impl;

import com.sms.dto.request.SubmissionGradeRequest;
import com.sms.dto.response.SubmissionResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .map(this::convertToSubmissionResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + studentId));
        
        List<Submission> submissions = submissionRepository.findByStudentId(studentId);
        return submissions.stream()
                .map(this::convertToSubmissionResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public SubmissionResponse gradeSubmission(SubmissionGradeRequest request) {
        Submission submission = submissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + request.getSubmissionId()));
        
        submission.setScore(request.getScore());
        Submission updatedSubmission = submissionRepository.save(submission);
        
        return convertToSubmissionResponse(updatedSubmission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));
        
        return convertToSubmissionResponse(submission);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getUngradedSubmissions(Long assignmentId) {
        assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        return submissions.stream()
                .filter(submission -> submission.getScore() == null)
                .map(this::convertToSubmissionResponse)
                .collect(Collectors.toList());
    }
    
    private SubmissionResponse convertToSubmissionResponse(Submission submission) {
        Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
                .orElse(new Assignment());
        
        Student student = studentRepository.findById(submission.getStudentId())
                .orElse(new Student());
        User user = userRepository.findById(student.getUserId())
                .orElse(new User());
        
        // Lấy tên file từ đường dẫn
        String fileName = "";
        if (submission.getFilePath() != null && !submission.getFilePath().isEmpty()) {
            String[] pathParts = submission.getFilePath().split("/");
            fileName = pathParts[pathParts.length - 1];
        }
        
        return new SubmissionResponse(
                submission.getId(),
                assignment.getTitle(),
                student.getStudentCode(),
                user.getFullName(),
                submission.getFilePath(),
                fileName,
                submission.getSubmittedAt(),
                submission.getScore(),
                submission.getScore() != null
        );
    }
}
