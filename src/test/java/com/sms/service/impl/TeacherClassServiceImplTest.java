package com.sms.service.impl;

import com.sms.dto.response.TeacherClassResponse;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherClassServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ClassStudentRepository classStudentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeacherClassServiceImpl teacherClassService;

    private Course testClass;
    private Subject testSubject;
    private Semester testSemester;
    private ClassStudent testClassStudent;
    private Student testStudent;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test data
        testClass = new Course();
        testClass.setId(1L);
        testClass.setClassCode("CS101");
        testClass.setSubjectId(1L);
        testClass.setSemesterId(1L);
        testClass.setTeacherId(1L);
        testClass.setMaxStudent(50);
        testClass.setCreatedAt(LocalDateTime.now());

        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setSubjectName("Lập trình Java");
        testSubject.setSubjectCode("CS101");

        testSemester = new Semester();
        testSemester.setId(1L);
        testSemester.setName("HK1-2024");

        testClassStudent = new ClassStudent();
        testClassStudent.setId(1L);
        testClassStudent.setClassId(1L);
        testClassStudent.setStudentId(1L);

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setUserId(1L);
        testStudent.setStudentCode("SV001");
        testStudent.setClassName("CNTT");
        testStudent.setMajor("Công nghệ thông tin");
        testStudent.setCourseYear(2020);

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Nguyễn Văn A");
        testUser.setEmail("student@example.com");
    }

    @Test
    void getClassesByTeacher_ShouldReturnClasses() {
        // Given
        Long teacherId = 1L;
        List<Course> classes = Arrays.asList(testClass);
        when(courseRepository.findByTeacherId(teacherId)).thenReturn(classes);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(classStudentRepository.findByClassId(1L)).thenReturn(Arrays.asList(testClassStudent));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        List<TeacherClassResponse> result = teacherClassService.getClassesByTeacher(teacherId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getClassCode());
        assertEquals("Lập trình Java", result.get(0).getSubjectName());
        assertEquals("HK1-2024", result.get(0).getSemesterName());
        assertEquals(5, result.get(0).getCurrentStudentCount());
    }

    @Test
    void getClassesByTeacherAndSemester_ShouldReturnClasses() {
        // Given
        Long teacherId = 1L;
        Long semesterId = 1L;
        List<Course> classes = Arrays.asList(testClass);
        when(courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId)).thenReturn(classes);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(classStudentRepository.findByClassId(1L)).thenReturn(Arrays.asList(testClassStudent));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        List<TeacherClassResponse> result = teacherClassService.getClassesByTeacherAndSemester(teacherId, semesterId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getClassCode());
    }

    @Test
    void getClassWithStudents_ShouldReturnClassWithStudents() {
        // Given
        Long classId = 1L;
        when(courseRepository.findById(classId)).thenReturn(Optional.of(testClass));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(classStudentRepository.findByClassId(1L)).thenReturn(Arrays.asList(testClassStudent));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        TeacherClassResponse result = teacherClassService.getClassWithStudents(classId);

        // Then
        assertNotNull(result);
        assertEquals("CS101", result.getClassCode());
        assertEquals("Lập trình Java", result.getSubjectName());
        assertEquals(1, result.getStudents().size());
        assertEquals("SV001", result.getStudents().get(0).getStudentCode());
        assertEquals("Nguyễn Văn A", result.getStudents().get(0).getStudentName());
    }

    @Test
    void getClassWithStudents_WhenClassNotFound_ShouldThrowException() {
        // Given
        Long classId = 999L;
        when(courseRepository.findById(classId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            teacherClassService.getClassWithStudents(classId);
        });

        assertEquals("Không tìm thấy lớp với ID: 999", exception.getMessage());
    }

    @Test
    void getClassesByTeacher_WhenNoClasses_ShouldReturnEmptyList() {
        // Given
        Long teacherId = 1L;
        when(courseRepository.findByTeacherId(teacherId)).thenReturn(Arrays.asList());

        // When
        List<TeacherClassResponse> result = teacherClassService.getClassesByTeacher(teacherId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getClassesByTeacherAndSemester_WhenNoClasses_ShouldReturnEmptyList() {
        // Given
        Long teacherId = 1L;
        Long semesterId = 1L;
        when(courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId)).thenReturn(Arrays.asList());

        // When
        List<TeacherClassResponse> result = teacherClassService.getClassesByTeacherAndSemester(teacherId, semesterId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
