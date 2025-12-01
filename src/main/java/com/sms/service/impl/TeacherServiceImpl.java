package com.sms.service.impl;

import com.sms.dto.request.TeacherRequest;
import com.sms.dto.request.TeacherUpdateRequest;
import com.sms.dto.response.TeacherListResponse;
import com.sms.dto.response.TeacherResponse;
import com.sms.entity.*;
import com.sms.exception.UserNotFoundException;
import com.sms.repository.*;
import com.sms.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherSubjectRepository teacherSubjectRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    public TeacherResponse createTeacher(TeacherRequest teacherRequest) {
        // Kiểm tra user tồn tại
        userRepository.findById(teacherRequest.getUserId())
            .orElseThrow(() -> new UserNotFoundException(teacherRequest.getUserId()));
        
        // Kiểm tra mã giáo viên đã tồn tại
        if (teacherRepository.existsByTeacherCode(teacherRequest.getTeacherCode())) {
            throw new RuntimeException("Mã giáo viên đã tồn tại: " + teacherRequest.getTeacherCode());
        }
        
        // Kiểm tra user đã có thông tin giáo viên chưa
        if (teacherRepository.existsByUserId(teacherRequest.getUserId())) {
            throw new RuntimeException("Người dùng đã có thông tin giáo viên");
        }
        
        Teacher teacher = new Teacher();
        teacher.setUserId(teacherRequest.getUserId());
        teacher.setTeacherCode(teacherRequest.getTeacherCode());
        teacher.setDepartment(teacherRequest.getDepartment());
        teacher.setTitle(teacherRequest.getTitle());
        teacher.setEducationLevel(teacherRequest.getEducationLevel());
        teacher.setExperienceYears(teacherRequest.getExperienceYears());
        teacher.setAddress(teacherRequest.getAddress());
        teacher.setHireDate(teacherRequest.getHireDate());
        
        Teacher savedTeacher = teacherRepository.save(teacher);
        
        // Gán môn học nếu có
        if (teacherRequest.getSubjectIds() != null && !teacherRequest.getSubjectIds().isEmpty()) {
            assignSubjectsToTeacher(savedTeacher.getId(), teacherRequest.getSubjectIds());
        }
        
        return convertToTeacherResponse(savedTeacher);
    }
    
    @Override
    public TeacherResponse updateTeacher(Long teacherId, TeacherUpdateRequest teacherUpdateRequest) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        if (teacherUpdateRequest.getDepartment() != null) {
            teacher.setDepartment(teacherUpdateRequest.getDepartment());
        }
        if (teacherUpdateRequest.getTitle() != null) {
            teacher.setTitle(teacherUpdateRequest.getTitle());
        }
        if (teacherUpdateRequest.getEducationLevel() != null) {
            teacher.setEducationLevel(teacherUpdateRequest.getEducationLevel());
        }
        if (teacherUpdateRequest.getExperienceYears() != null) {
            teacher.setExperienceYears(teacherUpdateRequest.getExperienceYears());
        }
        if (teacherUpdateRequest.getAddress() != null) {
            teacher.setAddress(teacherUpdateRequest.getAddress());
        }
        if (teacherUpdateRequest.getHireDate() != null) {
            teacher.setHireDate(teacherUpdateRequest.getHireDate());
        }
        
        Teacher updatedTeacher = teacherRepository.save(teacher);
        
        // Cập nhật môn học nếu có
        if (teacherUpdateRequest.getSubjectIds() != null) {
            // Xóa tất cả môn học cũ
            teacherSubjectRepository.deleteByTeacherId(teacherId);
            // Gán môn học mới
            if (!teacherUpdateRequest.getSubjectIds().isEmpty()) {
                assignSubjectsToTeacher(teacherId, teacherUpdateRequest.getSubjectIds());
            }
        }
        
        return convertToTeacherResponse(updatedTeacher);
    }
    
    @Override
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        // Xóa tất cả teacher_subject records
        teacherSubjectRepository.deleteByTeacherId(teacherId);
        
        // Xóa teacher
        teacherRepository.delete(teacher);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        return convertToTeacherResponse(teacher);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByCode(String teacherCode) {
        Teacher teacher = teacherRepository.findByTeacherCode(teacherCode)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với mã: " + teacherCode));
        
        return convertToTeacherResponse(teacher);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherListResponse getAllTeachers(Pageable pageable, Boolean isActive) {
        List<Teacher> teachers;
        if (isActive != null) {
            teachers = teacherRepository.findAllByIsActive(isActive);
        } else {
            teachers = teacherRepository.findAll();
        }
        
        // Convert to Page manually for pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), teachers.size());
        List<Teacher> pageContent = start < teachers.size() ? 
            teachers.subList(start, end) : 
            List.of();
        
        List<TeacherResponse> teacherResponses = pageContent.stream()
            .map(this::convertToTeacherResponse)
            .collect(Collectors.toList());
        
        return new TeacherListResponse(
            teacherResponses,
            teachers.size(),
            (int) Math.ceil((double) teachers.size() / pageable.getPageSize()),
            pageable.getPageNumber(),
            pageable.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherListResponse searchTeachers(String keyword, Pageable pageable, Boolean isActive) {
        List<Teacher> teachers = teacherRepository.searchTeachers(keyword, isActive);
        
        // Convert to Page manually for pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), teachers.size());
        List<Teacher> pageContent = start < teachers.size() ? 
            teachers.subList(start, end) : 
            List.of();
        
        List<TeacherResponse> teacherResponses = pageContent.stream()
            .map(this::convertToTeacherResponse)
            .collect(Collectors.toList());
        
        return new TeacherListResponse(
            teacherResponses,
            teachers.size(),
            (int) Math.ceil((double) teachers.size() / pageable.getPageSize()),
            pageable.getPageNumber(),
            pageable.getPageSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getTeachersByDepartment(String department) {
        List<Teacher> teachers = teacherRepository.findByDepartment(department);
        
        return teachers.stream()
            .map(this::convertToTeacherResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getTeachersByTitle(String title) {
        List<Teacher> teachers = teacherRepository.findByTitle(title);
        
        return teachers.stream()
            .map(this::convertToTeacherResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignSubjectsToTeacher(Long teacherId, List<Long> subjectIds) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        for (Long subjectId : subjectIds) {
            // Kiểm tra môn học tồn tại
            subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
            
            // Kiểm tra giáo viên đã được gán môn học này chưa
            if (teacherSubjectRepository.findByTeacherIdAndSubjectId(teacherId, subjectId).isPresent()) {
                continue; // Bỏ qua nếu đã được gán
            }
            
            TeacherSubject teacherSubject = new TeacherSubject();
            teacherSubject.setTeacherId(teacherId);
            teacherSubject.setSubjectId(subjectId);
            teacherSubjectRepository.save(teacherSubject);
        }
    }
    
    @Override
    public void removeSubjectsFromTeacher(Long teacherId, List<Long> subjectIds) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        for (Long subjectId : subjectIds) {
            teacherSubjectRepository.deleteByTeacherIdAndSubjectId(teacherId, subjectId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse.SubjectInfo> getTeacherSubjects(Long teacherId) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findByTeacherId(teacherId);
        
        return teacherSubjects.stream()
            .map(teacherSubject -> {
                Subject subject = subjectRepository.findById(teacherSubject.getSubjectId())
                    .orElse(new Subject());
                
                return new TeacherResponse.SubjectInfo(
                    subject.getId(),
                    subject.getSubjectCode(),
                    subject.getSubjectName(),
                    subject.getCredit()
                );
            })
            .collect(Collectors.toList());
    }
    
    private TeacherResponse convertToTeacherResponse(Teacher teacher) {
        User user = userRepository.findById(teacher.getUserId()).orElse(new User());
        List<TeacherResponse.SubjectInfo> subjects = getTeacherSubjects(teacher.getId());
        
        return new TeacherResponse(
            teacher.getId(),
            teacher.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            teacher.getTeacherCode(),
            teacher.getDepartment(),
            teacher.getTitle(),
            teacher.getEducationLevel(),
            teacher.getExperienceYears(),
            teacher.getAddress(),
            teacher.getHireDate(),
            teacher.getCreatedAt(),
            user.getIsActive(),
            subjects
        );
    }
}
