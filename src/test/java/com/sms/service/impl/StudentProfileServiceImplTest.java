package com.sms.service.impl;

import com.sms.dto.request.ChangePasswordRequest;
import com.sms.dto.request.StudentUpdateRequest;
import com.sms.dto.response.StudentProfileResponse;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.repository.StudentRepository;
import com.sms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentProfileServiceImpl studentProfileService;

    private Student testStudent;
    private User testUser;
    private StudentUpdateRequest testUpdateRequest;
    private ChangePasswordRequest testChangePasswordRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setUserId(1L);
        testStudent.setStudentCode("SV001");
        testStudent.setGender("Nam");
        testStudent.setDob(LocalDate.of(2000, 1, 1));
        testStudent.setAddress("Hà Nội");
        testStudent.setClassName("CNTT");
        testStudent.setMajor("Công nghệ thông tin");
        testStudent.setCourseYear(2020);
        testStudent.setCreatedAt(LocalDateTime.now());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("student001");
        testUser.setPassword("encodedPassword");
        testUser.setEmail("student@example.com");
        testUser.setFullName("Nguyễn Văn A");
        testUser.setPhone("0123456789");
        testUser.setIsActive(true);
        testUser.setIsEmailVerified(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUpdateRequest = new StudentUpdateRequest(
                "Nguyễn Văn A Updated", "student.updated@example.com", "0987654321",
                "Nam", LocalDate.of(2000, 1, 1), "TP.HCM", "CNTT", "Công nghệ thông tin", 2020
        );

        testChangePasswordRequest = new ChangePasswordRequest(
                "oldPassword", "newPassword", "newPassword"
        );
    }

    @Test
    void getStudentProfile_ShouldReturnProfile() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        StudentProfileResponse result = studentProfileService.getStudentProfile(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SV001", result.getStudentCode());
        assertEquals("student001", result.getUsername());
        assertEquals("Nguyễn Văn A", result.getFullName());
        assertEquals("student@example.com", result.getEmail());
        assertEquals("0123456789", result.getPhone());
        assertEquals("Nam", result.getGender());
        assertEquals(LocalDate.of(2000, 1, 1), result.getDob());
        assertEquals("Hà Nội", result.getAddress());
        assertEquals("CNTT", result.getClassName());
        assertEquals("Công nghệ thông tin", result.getMajor());
        assertEquals(2020, result.getCourseYear());
        assertTrue(result.getIsActive());
        assertTrue(result.getIsEmailVerified());
    }

    @Test
    void getStudentProfile_WhenStudentNotFound_ShouldThrowException() {
        // Given
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.getStudentProfile(studentId);
        });

        assertEquals("Không tìm thấy sinh viên với ID: 999", exception.getMessage());
    }

    @Test
    void getStudentProfile_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.getStudentProfile(studentId);
        });

        assertEquals("Không tìm thấy thông tin người dùng", exception.getMessage());
    }

    @Test
    void updateStudentProfile_ShouldUpdateAndReturnProfile() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        StudentProfileResponse result = studentProfileService.updateStudentProfile(studentId, testUpdateRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SV001", result.getStudentCode());
        assertEquals("Nguyễn Văn A", result.getFullName());

        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void updateStudentProfile_WhenStudentNotFound_ShouldThrowException() {
        // Given
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.updateStudentProfile(studentId, testUpdateRequest);
        });

        assertEquals("Không tìm thấy sinh viên với ID: 999", exception.getMessage());
    }

    @Test
    void updateStudentProfile_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.updateStudentProfile(studentId, testUpdateRequest);
        });

        assertEquals("Không tìm thấy thông tin người dùng", exception.getMessage());
    }

    @Test
    void updateStudentProfile_WithPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        // Given
        Long studentId = 1L;
        StudentUpdateRequest partialRequest = new StudentUpdateRequest();
        partialRequest.setFullName("Nguyễn Văn B");
        partialRequest.setEmail("newemail@example.com");
        // Other fields are null

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // When
        StudentProfileResponse result = studentProfileService.updateStudentProfile(studentId, partialRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void changePassword_WithValidCurrentPassword_ShouldChangePassword() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        studentProfileService.changePassword(studentId, testChangePasswordRequest);

        // Then
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_WithInvalidCurrentPassword_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.changePassword(studentId, testChangePasswordRequest);
        });

        assertEquals("Mật khẩu hiện tại không đúng", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_WithMismatchedNewPasswords_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword", "differentPassword"
        );
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.changePassword(studentId, request);
        });

        assertEquals("Mật khẩu mới và xác nhận mật khẩu không khớp", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_WhenStudentNotFound_ShouldThrowException() {
        // Given
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.changePassword(studentId, testChangePasswordRequest);
        });

        assertEquals("Không tìm thấy sinh viên với ID: 999", exception.getMessage());
    }

    @Test
    void changePassword_WhenUserNotFound_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentProfileService.changePassword(studentId, testChangePasswordRequest);
        });

        assertEquals("Không tìm thấy thông tin người dùng", exception.getMessage());
    }
}
