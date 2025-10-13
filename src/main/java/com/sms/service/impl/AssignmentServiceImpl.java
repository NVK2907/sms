package com.sms.service.impl;

import com.sms.dto.request.AssignmentRequest;
import com.sms.dto.response.AssignmentResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request) {
        // Kiểm tra lớp tồn tại
        Course classEntity = courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        Assignment assignment = new Assignment();
        assignment.setClassId(request.getClassId());
        assignment.setTeacherId(request.getTeacherId());
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDeadline(request.getDeadline());
        
        Assignment savedAssignment = assignmentRepository.save(assignment);
        return convertToAssignmentResponse(savedAssignment);
    }
    
    @Override
    public AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        // Kiểm tra lớp tồn tại
        courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        assignment.setClassId(request.getClassId());
        assignment.setTeacherId(request.getTeacherId());
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDeadline(request.getDeadline());
        
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return convertToAssignmentResponse(updatedAssignment);
    }
    
    @Override
    public void deleteAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        assignmentRepository.delete(assignment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByClass(Long classId) {
        courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<Assignment> assignments = assignmentRepository.findByClassId(classId);
        return assignments.stream()
                .map(this::convertToAssignmentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId) {
        List<Assignment> assignments = assignmentRepository.findByTeacherId(teacherId);
        return assignments.stream()
                .map(this::convertToAssignmentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập với ID: " + assignmentId));
        
        return convertToAssignmentResponse(assignment);
    }
    
    private AssignmentResponse convertToAssignmentResponse(Assignment assignment) {
        Course classEntity = courseRepository.findById(assignment.getClassId())
                .orElse(new Course());
        Subject subject = subjectRepository.findById(classEntity.getSubjectId())
                .orElse(new Subject());
        
        // Đếm số lượng bài nộp
        List<Submission> submissions = submissionRepository.findByAssignmentId(assignment.getId());
        int submissionCount = submissions.size();
        
        // Đếm số lượng bài đã chấm điểm
        int gradedCount = (int) submissions.stream()
                .filter(submission -> submission.getScore() != null)
                .count();
        
        return new AssignmentResponse(
                assignment.getId(),
                classEntity.getClassCode(),
                subject.getSubjectName(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDeadline(),
                assignment.getCreatedAt(),
                submissionCount,
                gradedCount
        );
    }
}
