package com.sms.service.impl;

import com.sms.dto.request.StudentGradeRequest;
import com.sms.dto.response.StudentGradeResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Grade testGrade;
    private Course testClass;
    private Student testStudent;
    private Subject testSubject;
    private User testUser;
    private StudentGradeRequest testRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testGrade = new Grade();
        testGrade.setId(1L);
        testGrade.setClassId(1L);
        testGrade.setStudentId(1L);
        testGrade.setMidterm(8.5f);
        testGrade.setFinalGrade(9.0f);
        testGrade.setOther(8.0f);
        testGrade.setTotal(8.5f);
        testGrade.setUpdatedAt(LocalDateTime.now());

        testClass = new Course();
        testClass.setId(1L);
        testClass.setClassCode("CS101");
        testClass.setSubjectId(1L);

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setUserId(1L);
        testStudent.setStudentCode("SV001");

        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setSubjectName("Lập trình Java");
        testSubject.setSubjectCode("CS101");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Nguyễn Văn A");

        testRequest = new StudentGradeRequest(1L, 1L, 8.5f, 9.0f, 8.0f);
    }

    @Test
    void createOrUpdateGrade_WhenGradeExists_ShouldUpdateGrade() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.of(testGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(testGrade);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        StudentGradeResponse result = gradeService.createOrUpdateGrade(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CS101", result.getClassCode());
        assertEquals("Lập trình Java", result.getSubjectName());
        assertEquals(8.5f, result.getMidterm());
        assertEquals(9.0f, result.getFinalGrade());
        assertEquals(8.0f, result.getOther());
        assertEquals(8.5f, result.getTotal());
        assertEquals("B+", result.getLetterGrade());

        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    void createOrUpdateGrade_WhenGradeNotExists_ShouldCreateNewGrade() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenReturn(testGrade);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        StudentGradeResponse result = gradeService.createOrUpdateGrade(testRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CS101", result.getClassCode());
        assertEquals("B+", result.getLetterGrade());

        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    void createOrUpdateGrade_WhenClassNotFound_ShouldThrowException() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gradeService.createOrUpdateGrade(testRequest);
        });

        assertEquals("Không tìm thấy lớp với ID: 1", exception.getMessage());
    }

    @Test
    void createOrUpdateGrade_WhenStudentNotFound_ShouldThrowException() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gradeService.createOrUpdateGrade(testRequest);
        });

        assertEquals("Không tìm thấy sinh viên với ID: 1", exception.getMessage());
    }

    @Test
    void getGradesByClass_ShouldReturnGrades() {
        // Given
        Long classId = 1L;
        List<Grade> grades = Arrays.asList(testGrade);
        when(courseRepository.findById(classId)).thenReturn(Optional.of(testClass));
        when(gradeRepository.findByClassId(classId)).thenReturn(grades);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        List<StudentGradeResponse> result = gradeService.getGradesByClass(classId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("CS101", result.get(0).getClassCode());
    }

    @Test
    void getGradesByStudent_ShouldReturnGrades() {
        // Given
        Long studentId = 1L;
        List<Grade> grades = Arrays.asList(testGrade);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByStudentId(studentId)).thenReturn(grades);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        List<StudentGradeResponse> result = gradeService.getGradesByStudent(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getGradeByClassAndStudent_ShouldReturnGrade() {
        // Given
        Long classId = 1L;
        Long studentId = 1L;
        when(gradeRepository.findByClassIdAndStudentId(classId, studentId)).thenReturn(Optional.of(testGrade));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        StudentGradeResponse result = gradeService.getGradeByClassAndStudent(classId, studentId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CS101", result.getClassCode());
    }

    @Test
    void getGradeByClassAndStudent_WhenGradeNotFound_ShouldThrowException() {
        // Given
        Long classId = 1L;
        Long studentId = 1L;
        when(gradeRepository.findByClassIdAndStudentId(classId, studentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gradeService.getGradeByClassAndStudent(classId, studentId);
        });

        assertEquals("Không tìm thấy điểm của sinh viên trong lớp này", exception.getMessage());
    }

    @Test
    void deleteGrade_ShouldDeleteGrade() {
        // Given
        Long gradeId = 1L;
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(testGrade));

        // When
        gradeService.deleteGrade(gradeId);

        // Then
        verify(gradeRepository).delete(testGrade);
    }

    @Test
    void deleteGrade_WhenGradeNotFound_ShouldThrowException() {
        // Given
        Long gradeId = 999L;
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gradeService.deleteGrade(gradeId);
        });

        assertEquals("Không tìm thấy điểm với ID: 999", exception.getMessage());
    }

    @Test
    void exportGradesByClass_ShouldReturnGrades() {
        // Given
        Long classId = 1L;
        List<Grade> grades = Arrays.asList(testGrade);
        when(courseRepository.findById(classId)).thenReturn(Optional.of(testClass));
        when(gradeRepository.findByClassId(classId)).thenReturn(grades);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        List<StudentGradeResponse> result = gradeService.exportGradesByClass(classId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void calculateTotal_WithAllGrades_ShouldCalculateCorrectly() {
        // Given
        StudentGradeRequest request = new StudentGradeRequest(1L, 1L, 8.0f, 9.0f, 7.0f);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> {
            Grade grade = invocation.getArgument(0);
            grade.setId(1L); // Set ID for the saved grade
            return grade; // Return the same grade with calculated values
        });
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        StudentGradeResponse result = gradeService.createOrUpdateGrade(request);

        // Then
        assertNotNull(result);
        // Total should be (8.0 + 9.0 + 7.0) / 3 = 8.0
        assertEquals(8.0f, result.getTotal(), 0.01f); // Use delta for float comparison
    }

    @Test
    void calculateTotal_WithNullGrades_ShouldReturnNull() {
        // Given
        StudentGradeRequest request = new StudentGradeRequest(1L, 1L, null, null, null);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> {
            Grade grade = invocation.getArgument(0);
            grade.setId(1L); // Set ID for the saved grade
            return grade; // Return the same grade with calculated values
        });
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));

        // When
        StudentGradeResponse result = gradeService.createOrUpdateGrade(request);

        // Then
        assertNotNull(result);
        assertNull(result.getTotal());
    }
}
