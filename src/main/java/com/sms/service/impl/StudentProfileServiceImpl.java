package com.sms.service.impl;

import com.sms.dto.request.ChangePasswordRequest;
import com.sms.dto.request.StudentUpdateRequest;
import com.sms.dto.response.StudentProfileResponse;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.StudentRepository;
import com.sms.repository.UserRepository;
import com.sms.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentProfileServiceImpl implements StudentProfileService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + studentId));
        
        User user = userRepository.findById(student.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        return new StudentProfileResponse(
                student.getId(),
                student.getStudentCode(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                student.getGender(),
                student.getDob(),
                student.getAddress(),
                student.getClassName(),
                student.getMajor(),
                student.getCourseYear(),
                user.getIsActive(),
                user.getIsEmailVerified(),
                student.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    
    @Override
    public StudentProfileResponse updateStudentProfile(Long studentId, StudentUpdateRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + studentId));
        
        User user = userRepository.findById(student.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        // Cập nhật thông tin User
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        // Cập nhật thông tin Student
        if (request.getGender() != null) {
            student.setGender(request.getGender());
        }
        if (request.getDob() != null) {
            student.setDob(request.getDob());
        }
        if (request.getAddress() != null) {
            student.setAddress(request.getAddress());
        }
        if (request.getClassName() != null) {
            student.setClassName(request.getClassName());
        }
        if (request.getMajor() != null) {
            student.setMajor(request.getMajor());
        }
        if (request.getCourseYear() != null) {
            student.setCourseYear(request.getCourseYear());
        }
        
        User savedUser = userRepository.save(user);
        Student savedStudent = studentRepository.save(student);
        
        return new StudentProfileResponse(
                savedStudent.getId(),
                savedStudent.getStudentCode(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedStudent.getGender(),
                savedStudent.getDob(),
                savedStudent.getAddress(),
                savedStudent.getClassName(),
                savedStudent.getMajor(),
                savedStudent.getCourseYear(),
                savedUser.getIsActive(),
                savedUser.getIsEmailVerified(),
                savedStudent.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }
    
    @Override
    public void changePassword(Long studentId, ChangePasswordRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + studentId));
        
        User user = userRepository.findById(student.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
        
        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }
        
        // Kiểm tra mật khẩu mới và xác nhận
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }
        
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
