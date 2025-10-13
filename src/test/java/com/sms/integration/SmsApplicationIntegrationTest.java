package com.sms.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.request.StudentGradeRequest;
import com.sms.dto.request.AssignmentRequest;
import com.sms.dto.request.MaterialRequest;
import com.sms.dto.request.AttendanceRequest;
import com.sms.entity.*;
import com.sms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SmsApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassStudentRepository classStudentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private User testUser;
    private Student testStudent;
    private Teacher testTeacher;
    private Subject testSubject;
    private Semester testSemester;
    private Course testClass;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        attendanceRepository.deleteAll();
        materialRepository.deleteAll();
        assignmentRepository.deleteAll();
        gradeRepository.deleteAll();
        classStudentRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();
        subjectRepository.deleteAll();
        teacherRepository.deleteAll();
        studentRepository.deleteAll();
        userRepository.deleteAll();

        // Create test data
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);

        testStudent = new Student();
        testStudent.setUserId(testUser.getId());
        testStudent.setStudentCode("SV001");
        testStudent.setGender("Nam");
        testStudent.setDob(LocalDate.of(2000, 1, 1));
        testStudent.setAddress("Hà Nội");
        testStudent.setClassName("CNTT");
        testStudent.setMajor("Công nghệ thông tin");
        testStudent.setCourseYear(2020);
        testStudent = studentRepository.save(testStudent);

        User teacherUser = new User();
        teacherUser.setUsername("teacher");
        teacherUser.setPassword("password");
        teacherUser.setEmail("teacher@example.com");
        teacherUser.setFullName("Teacher Name");
        teacherUser.setIsActive(true);
        teacherUser = userRepository.save(teacherUser);

        testTeacher = new Teacher();
        testTeacher.setUserId(teacherUser.getId());
        testTeacher.setTeacherCode("GV001");
        testTeacher.setDepartment("CNTT");
        testTeacher.setTitle("Thạc sĩ");
        testTeacher = teacherRepository.save(testTeacher);

        testSubject = new Subject();
        testSubject.setSubjectName("Lập trình Java");
        testSubject.setSubjectCode("CS101");
        testSubject = subjectRepository.save(testSubject);

        testSemester = new Semester();
        testSemester.setName("HK1-2024");
        testSemester.setStartDate(LocalDate.of(2024, 1, 1));
        testSemester.setEndDate(LocalDate.of(2024, 6, 30));
        testSemester = semesterRepository.save(testSemester);

        testClass = new Course();
        testClass.setClassCode("CS101-01");
        testClass.setSubjectId(testSubject.getId());
        testClass.setSemesterId(testSemester.getId());
        testClass.setTeacherId(testTeacher.getId());
        testClass.setMaxStudent(50);
        testClass = courseRepository.save(testClass);

        // Register student to class
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(testClass.getId());
        classStudent.setStudentId(testStudent.getId());
        classStudentRepository.save(classStudent);
    }

    @Test
    void testTeacherGetClasses_ShouldReturnClasses() throws Exception {
        mockMvc.perform(get("/api/teacher/classes/{teacherId}", testTeacher.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testClass.getId()))
                .andExpect(jsonPath("$[0].classCode").value("CS101-01"))
                .andExpect(jsonPath("$[0].subjectName").value("Lập trình Java"));
    }

    @Test
    void testTeacherCreateGrade_ShouldCreateGrade() throws Exception {
        StudentGradeRequest request = new StudentGradeRequest(
                testClass.getId(), testStudent.getId(), 8.5f, 9.0f, 8.0f
        );

        mockMvc.perform(post("/api/teacher/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.classCode").value("CS101-01"))
                .andExpect(jsonPath("$.total").value(8.5)); // (8.5 + 9.0 + 8.0) / 3 = 8.5
    }

    @Test
    void testTeacherCreateAssignment_ShouldCreateAssignment() throws Exception {
        AssignmentRequest request = new AssignmentRequest(
                testClass.getId(), testTeacher.getId(), "Bài tập 1", "Mô tả bài tập",
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post("/api/teacher/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Bài tập 1"))
                .andExpect(jsonPath("$.classCode").value("CS101-01"));
    }

    @Test
    void testTeacherUploadMaterial_ShouldUploadMaterial() throws Exception {
        MaterialRequest request = new MaterialRequest(
                testClass.getId(), testTeacher.getId(), "Tài liệu chương 1", "/uploads/chapter1.pdf"
        );

        mockMvc.perform(post("/api/teacher/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Tài liệu chương 1"))
                .andExpect(jsonPath("$.classCode").value("CS101-01"));
    }

    @Test
    void testTeacherRecordAttendance_ShouldRecordAttendance() throws Exception {
        AttendanceRequest request = new AttendanceRequest(
                testClass.getId(), LocalDate.now(),
                Arrays.asList(new AttendanceRequest.StudentAttendanceRequest(testStudent.getId(), "present"))
        );

        mockMvc.perform(post("/api/teacher/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.classCode").value("CS101-01"));
    }

    @Test
    void testStudentGetProfile_ShouldReturnProfile() throws Exception {
        mockMvc.perform(get("/api/student/profile/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testStudent.getId()))
                .andExpect(jsonPath("$.studentCode").value("SV001"))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    void testStudentGetRegisteredClasses_ShouldReturnClasses() throws Exception {
        mockMvc.perform(get("/api/student/{studentId}/classes/registered", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(testClass.getId()))
                .andExpect(jsonPath("$[0].classCode").value("CS101-01"))
                .andExpect(jsonPath("$[0].isRegistered").value(true));
    }

    @Test
    void testStudentGetGrades_ShouldReturnGrades() throws Exception {
        // First create a grade
        Grade grade = new Grade();
        grade.setClassId(testClass.getId());
        grade.setStudentId(testStudent.getId());
        grade.setMidterm(8.5f);
        grade.setFinalGrade(9.0f);
        grade.setOther(8.0f);
        grade.setTotal(8.5f);
        gradeRepository.save(grade);

        mockMvc.perform(get("/api/student/{studentId}/grades", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].classCode").value("CS101-01"))
                .andExpect(jsonPath("$[0].total").value(8.5));
    }

    @Test
    void testStudentGetMaterials_ShouldReturnMaterials() throws Exception {
        // First create a material
        Material material = new Material();
        material.setClassId(testClass.getId());
        material.setTeacherId(testTeacher.getId());
        material.setTitle("Tài liệu chương 1");
        material.setFilePath("/uploads/chapter1.pdf");
        materialRepository.save(material);

        mockMvc.perform(get("/api/student/{studentId}/materials", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Tài liệu chương 1"))
                .andExpect(jsonPath("$[0].classCode").value("CS101-01"));
    }

    @Test
    void testStudentGetAssignments_ShouldReturnAssignments() throws Exception {
        // First create an assignment
        Assignment assignment = new Assignment();
        assignment.setClassId(testClass.getId());
        assignment.setTeacherId(testTeacher.getId());
        assignment.setTitle("Bài tập 1");
        assignment.setDescription("Mô tả bài tập");
        assignment.setDeadline(LocalDateTime.now().plusDays(7));
        assignmentRepository.save(assignment);

        mockMvc.perform(get("/api/student/{studentId}/assignments", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Bài tập 1"))
                .andExpect(jsonPath("$[0].classCode").value("CS101-01"));
    }
}
