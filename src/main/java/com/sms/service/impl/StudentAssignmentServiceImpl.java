package com.sms.service.impl;

import com.sms.dto.request.SubmissionRequest;
import com.sms.dto.response.StudentAssignmentResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.StudentAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentAssignmentServiceImpl implements StudentAssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentAssignmentResponse> getAssignmentsByStudent(Long studentId) {
        // Lấy danh sách lớp đã đăng ký
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        List<Long> classIds = classStudents.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        // Lấy bài tập từ các lớp đã đăng ký
        return classIds.stream()
                .flatMap(classId -> assignmentRepository.findByClassId(classId).stream())
                .map(assignment -> convertToStudentAssignmentResponse(assignment, studentId))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentAssignmentResponse> getAssignmentsByClass(Long studentId, Long classId) {
        // Kiểm tra sinh viên đã đăng ký lớp này chưa
        classStudentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên chưa đăng ký lớp này"));
        
        List<Assignment> assignments = assignmentRepository.findByClassId(classId);
        return assignments.stream()
                .map(assignment -> convertToStudentAssignmentResponse(assignment, studentId))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentAssignmentResponse getAssignmentById(Long studentId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        // Kiểm tra sinh viên đã đăng ký lớp này chưa
        classStudentRepository.findByClassIdAndStudentId(assignment.getClassId(), studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên chưa đăng ký lớp này"));
        
        return convertToStudentAssignmentResponse(assignment, studentId);
    }
    
    @Override
    public void submitAssignment(SubmissionRequest request) {
        // Kiểm tra sinh viên đã đăng ký lớp này chưa
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + request.getAssignmentId()));
        
        classStudentRepository.findByClassIdAndStudentId(assignment.getClassId(), request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Sinh viên chưa đăng ký lớp này"));
        
        // Kiểm tra đã nộp bài chưa
        if (submissionRepository.findByAssignmentIdAndStudentId(request.getAssignmentId(), request.getStudentId()).isPresent()) {
            throw new RuntimeException("Sinh viên đã nộp bài tập này");
        }
        
        // Tạo bài nộp mới
        Submission submission = new Submission();
        submission.setAssignmentId(request.getAssignmentId());
        submission.setStudentId(request.getStudentId());
        submission.setFilePath(request.getFilePath());
        
        submissionRepository.save(submission);
    }
    
    @Override
    public void updateSubmission(Long studentId, Long submissionId, SubmissionRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp với ID: " + submissionId));
        
        // Kiểm tra quyền sở hữu
        if (!submission.getStudentId().equals(studentId)) {
            throw new RuntimeException("Không có quyền cập nhật bài nộp này");
        }
        
        // Cập nhật bài nộp
        submission.setFilePath(request.getFilePath());
        submissionRepository.save(submission);
    }
    
    private StudentAssignmentResponse convertToStudentAssignmentResponse(Assignment assignment, Long studentId) {
        Course clazz = courseRepository.findById(assignment.getClassId())
                .orElse(new Course());
        Subject subject = subjectRepository.findById(clazz.getSubjectId())
                .orElse(new Subject());
        
        // Kiểm tra đã nộp bài chưa
        boolean isSubmitted = submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), studentId).isPresent();
        
        Submission submission = null;
        if (isSubmitted) {
            submission = submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), studentId).orElse(null);
        }
        
        return new StudentAssignmentResponse(
                assignment.getId(),
                clazz.getClassCode(),
                subject.getSubjectName(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDeadline(),
                assignment.getCreatedAt(),
                isSubmitted,
                submission != null ? submission.getSubmittedAt() : null,
                submission != null ? submission.getScore() : null,
                submission != null && submission.getScore() != null
        );
    }
}
