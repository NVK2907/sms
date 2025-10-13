package com.sms.service.impl;

import com.sms.dto.request.ClassRegistrationRequest;
import com.sms.dto.response.StudentClassResponse;
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
class StudentAcademicServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ClassStudentRepository classStudentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentAcademicServiceImpl studentAcademicService;

    private Course testClass;
    private ClassStudent testClassStudent;
    private Subject testSubject;
    private Semester testSemester;
    private Teacher testTeacher;
    private User testUser;
    private Student testStudent;
    private Schedule testSchedule;
    private ClassRegistrationRequest testRegistrationRequest;

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

        testClassStudent = new ClassStudent();
        testClassStudent.setId(1L);
        testClassStudent.setClassId(1L);
        testClassStudent.setStudentId(1L);

        testSubject = new Subject();
        testSubject.setId(1L);
        testSubject.setSubjectName("Lập trình Java");
        testSubject.setSubjectCode("CS101");

        testSemester = new Semester();
        testSemester.setId(1L);
        testSemester.setName("HK1-2024");

        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setUserId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setFullName("Thầy B");

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setUserId(1L);
        testStudent.setStudentCode("SV001");

        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setClassId(1L);
        testSchedule.setDayOfWeek("MONDAY");
        testSchedule.setStartTime(java.time.LocalTime.of(8, 0));
        testSchedule.setEndTime(java.time.LocalTime.of(10, 0));
        testSchedule.setRoom("A101");

        testRegistrationRequest = new ClassRegistrationRequest(1L, 1L);
    }

    @Test
    void getAvailableClasses_ShouldReturnAvailableClasses() {
        // Given
        Long studentId = 1L;
        List<Course> allClasses = Arrays.asList(testClass);
        List<ClassStudent> registeredClasses = Arrays.asList(); // No registered classes
        when(courseRepository.findAll()).thenReturn(allClasses);
        when(classStudentRepository.findByStudentId(studentId)).thenReturn(registeredClasses);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(scheduleRepository.findByClassId(1L)).thenReturn(Arrays.asList(testSchedule));

        // When
        List<StudentClassResponse> result = studentAcademicService.getAvailableClasses(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getClassCode());
        assertEquals("Lập trình Java", result.get(0).getSubjectName());
        assertEquals("HK1-2024", result.get(0).getSemesterName());
        assertEquals("Thầy B", result.get(0).getTeacherName());
        assertFalse(result.get(0).getIsRegistered());
    }

    @Test
    void getAvailableClasses_ShouldExcludeRegisteredClasses() {
        // Given
        Long studentId = 1L;
        List<Course> allClasses = Arrays.asList(testClass);
        List<ClassStudent> registeredClasses = Arrays.asList(testClassStudent); // Student is registered
        when(courseRepository.findAll()).thenReturn(allClasses);
        when(classStudentRepository.findByStudentId(studentId)).thenReturn(registeredClasses);

        // When
        List<StudentClassResponse> result = studentAcademicService.getAvailableClasses(studentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Should be empty because student is already registered
    }

    @Test
    void getRegisteredClasses_ShouldReturnRegisteredClasses() {
        // Given
        Long studentId = 1L;
        List<ClassStudent> classStudents = Arrays.asList(testClassStudent);
        when(classStudentRepository.findByStudentId(studentId)).thenReturn(classStudents);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(scheduleRepository.findByClassId(1L)).thenReturn(Arrays.asList(testSchedule));

        // When
        List<StudentClassResponse> result = studentAcademicService.getRegisteredClasses(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getClassCode());
        assertTrue(result.get(0).getIsRegistered());
    }

    @Test
    void getRegisteredClassesBySemester_ShouldReturnClassesForSpecificSemester() {
        // Given
        Long studentId = 1L;
        Long semesterId = 1L;
        List<ClassStudent> classStudents = Arrays.asList(testClassStudent);
        when(classStudentRepository.findByStudentId(studentId)).thenReturn(classStudents);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L);
        when(scheduleRepository.findByClassId(1L)).thenReturn(Arrays.asList(testSchedule));

        // When
        List<StudentClassResponse> result = studentAcademicService.getRegisteredClassesBySemester(studentId, semesterId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getClassCode());
        assertTrue(result.get(0).getIsRegistered());
    }

    @Test
    void registerForClass_ShouldRegisterSuccessfully() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(classStudentRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(classStudentRepository.countByClassId(1L)).thenReturn(5L); // Less than maxStudent (50)
        when(classStudentRepository.save(any(ClassStudent.class))).thenReturn(testClassStudent);

        // When
        studentAcademicService.registerForClass(testRegistrationRequest);

        // Then
        verify(classStudentRepository).save(any(ClassStudent.class));
    }

    @Test
    void registerForClass_WhenStudentNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentAcademicService.registerForClass(testRegistrationRequest);
        });

        assertEquals("Không tìm thấy sinh viên với ID: 1", exception.getMessage());
        verify(classStudentRepository, never()).save(any(ClassStudent.class));
    }

    @Test
    void registerForClass_WhenClassNotFound_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentAcademicService.registerForClass(testRegistrationRequest);
        });

        assertEquals("Không tìm thấy lớp với ID: 1", exception.getMessage());
        verify(classStudentRepository, never()).save(any(ClassStudent.class));
    }

    @Test
    void registerForClass_WhenAlreadyRegistered_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(classStudentRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.of(testClassStudent));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentAcademicService.registerForClass(testRegistrationRequest);
        });

        assertEquals("Sinh viên đã đăng ký lớp này", exception.getMessage());
        verify(classStudentRepository, never()).save(any(ClassStudent.class));
    }

    @Test
    void registerForClass_WhenClassIsFull_ShouldThrowException() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(classStudentRepository.findByClassIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(classStudentRepository.countByClassId(1L)).thenReturn(50L); // Equal to maxStudent (50)

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentAcademicService.registerForClass(testRegistrationRequest);
        });

        assertEquals("Lớp đã đầy, không thể đăng ký", exception.getMessage());
        verify(classStudentRepository, never()).save(any(ClassStudent.class));
    }

    @Test
    void unregisterFromClass_ShouldUnregisterSuccessfully() {
        // Given
        Long studentId = 1L;
        Long classId = 1L;
        when(classStudentRepository.findByClassIdAndStudentId(classId, studentId))
                .thenReturn(Optional.of(testClassStudent));

        // When
        studentAcademicService.unregisterFromClass(studentId, classId);

        // Then
        verify(classStudentRepository).delete(testClassStudent);
    }

    @Test
    void unregisterFromClass_WhenNotRegistered_ShouldThrowException() {
        // Given
        Long studentId = 1L;
        Long classId = 1L;
        when(classStudentRepository.findByClassIdAndStudentId(classId, studentId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentAcademicService.unregisterFromClass(studentId, classId);
        });

        assertEquals("Sinh viên chưa đăng ký lớp này", exception.getMessage());
        verify(classStudentRepository, never()).delete(any(ClassStudent.class));
    }

    @Test
    void getRegisteredClasses_WhenNoRegisteredClasses_ShouldReturnEmptyList() {
        // Given
        Long studentId = 1L;
        when(classStudentRepository.findByStudentId(studentId)).thenReturn(Arrays.asList());

        // When
        List<StudentClassResponse> result = studentAcademicService.getRegisteredClasses(studentId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
