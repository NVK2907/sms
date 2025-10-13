package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.request.*;
import com.sms.dto.response.*;
import com.sms.service.*;
import com.sms.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherFeaturesController.class)
@AutoConfigureMockMvc(addFilters = false)
class TeacherFeaturesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherClassService teacherClassService;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private MaterialService materialService;

    @MockBean
    private AttendanceService attendanceService;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private TeacherClassResponse teacherClassResponse;
    private StudentGradeResponse studentGradeResponse;
    private AssignmentResponse assignmentResponse;
    private MaterialResponse materialResponse;
    private AttendanceResponse attendanceResponse;
    private SubmissionResponse submissionResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        teacherClassResponse = new TeacherClassResponse(
                1L, "CS101", "Lập trình Java", "HK1-2024", 50, 30, 
                LocalDateTime.now(), Arrays.asList()
        );

        studentGradeResponse = new StudentGradeResponse(
                1L, "CS101", "Lập trình Java", "CS101", 3,
                8.5f, 9.0f, 8.0f, 8.5f, "B+", LocalDateTime.now()
        );

        assignmentResponse = new AssignmentResponse(
                1L, "CS101", "Lập trình Java", "Bài tập 1", "Mô tả bài tập",
                LocalDateTime.now().plusDays(7), LocalDateTime.now(), 25, 20
        );

        materialResponse = new MaterialResponse(
                1L, "CS101", "Lập trình Java", "Tài liệu chương 1", 
                "/uploads/chapter1.pdf", "chapter1.pdf", LocalDateTime.now()
        );

        attendanceResponse = new AttendanceResponse(
                1L, "CS101", "Lập trình Java", LocalDate.now(), 
                LocalDateTime.now(), Arrays.asList()
        );

        submissionResponse = new SubmissionResponse(
                1L, "Bài tập 1", "SV001", "Nguyễn Văn A", 
                "/uploads/submission.pdf", "submission.pdf", 
                LocalDateTime.now(), 8.5f, true
        );
    }

    // ========== TEST QUẢN LÝ LỚP VÀ SINH VIÊN ==========

    @Test
    void getClassesByTeacher_ShouldReturnClasses() throws Exception {
        // Given
        Long teacherId = 1L;
        List<TeacherClassResponse> expectedClasses = Arrays.asList(teacherClassResponse);
        when(teacherClassService.getClassesByTeacher(teacherId)).thenReturn(expectedClasses);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{teacherId}", teacherId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"))
                .andExpect(jsonPath("$[0].subjectName").value("Lập trình Java"));
    }

    @Test
    void getClassesByTeacherAndSemester_ShouldReturnClasses() throws Exception {
        // Given
        Long teacherId = 1L;
        Long semesterId = 1L;
        List<TeacherClassResponse> expectedClasses = Arrays.asList(teacherClassResponse);
        when(teacherClassService.getClassesByTeacherAndSemester(teacherId, semesterId))
                .thenReturn(expectedClasses);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{teacherId}/semester/{semesterId}", teacherId, semesterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"));
    }

    @Test
    void getClassWithStudents_ShouldReturnClassWithStudents() throws Exception {
        // Given
        Long classId = 1L;
        when(teacherClassService.getClassWithStudents(classId)).thenReturn(teacherClassResponse);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/students", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.classCode").value("CS101"));
    }

    // ========== TEST QUẢN LÝ ĐIỂM ==========

    @Test
    void createOrUpdateGrade_ShouldReturnGrade() throws Exception {
        // Given
        StudentGradeRequest request = new StudentGradeRequest(1L, 1L, 8.5f, 9.0f, 8.0f);
        when(gradeService.createOrUpdateGrade(any(StudentGradeRequest.class)))
                .thenReturn(studentGradeResponse);

        // When & Then
        mockMvc.perform(post("/api/teacher/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.classCode").value("CS101"));
    }

    @Test
    void getGradesByClass_ShouldReturnGrades() throws Exception {
        // Given
        Long classId = 1L;
        List<StudentGradeResponse> expectedGrades = Arrays.asList(studentGradeResponse);
        when(gradeService.getGradesByClass(classId)).thenReturn(expectedGrades);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/grades", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"));
    }

    @Test
    void exportGradesByClass_ShouldReturnGrades() throws Exception {
        // Given
        Long classId = 1L;
        List<StudentGradeResponse> expectedGrades = Arrays.asList(studentGradeResponse);
        when(gradeService.exportGradesByClass(classId)).thenReturn(expectedGrades);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/grades/export", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void deleteGrade_ShouldReturnOk() throws Exception {
        // Given
        Long gradeId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/teacher/grades/{gradeId}", gradeId))
                .andExpect(status().isOk());
    }

    // ========== TEST QUẢN LÝ BÀI TẬP ==========

    @Test
    void createAssignment_ShouldReturnAssignment() throws Exception {
        // Given
        AssignmentRequest request = new AssignmentRequest(1L, 1L, "Bài tập 1", "Mô tả", LocalDateTime.now().plusDays(7));
        when(assignmentService.createAssignment(any(AssignmentRequest.class)))
                .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(post("/api/teacher/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Bài tập 1"));
    }

    @Test
    void updateAssignment_ShouldReturnAssignment() throws Exception {
        // Given
        Long assignmentId = 1L;
        AssignmentRequest request = new AssignmentRequest(1L, 1L, "Bài tập 1 Updated", "Mô tả", LocalDateTime.now().plusDays(7));
        when(assignmentService.updateAssignment(eq(assignmentId), any(AssignmentRequest.class)))
                .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(put("/api/teacher/assignments/{assignmentId}", assignmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteAssignment_ShouldReturnOk() throws Exception {
        // Given
        Long assignmentId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/teacher/assignments/{assignmentId}", assignmentId))
                .andExpect(status().isOk());
    }

    @Test
    void getAssignmentsByClass_ShouldReturnAssignments() throws Exception {
        // Given
        Long classId = 1L;
        List<AssignmentResponse> expectedAssignments = Arrays.asList(assignmentResponse);
        when(assignmentService.getAssignmentsByClass(classId)).thenReturn(expectedAssignments);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/assignments", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Bài tập 1"));
    }

    // ========== TEST QUẢN LÝ TÀI LIỆU ==========

    @Test
    void uploadMaterial_ShouldReturnMaterial() throws Exception {
        // Given
        MaterialRequest request = new MaterialRequest(1L, 1L, "Tài liệu chương 1", "/uploads/chapter1.pdf");
        when(materialService.uploadMaterial(any(MaterialRequest.class)))
                .thenReturn(materialResponse);

        // When & Then
        mockMvc.perform(post("/api/teacher/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tài liệu chương 1"));
    }

    @Test
    void getMaterialsByClass_ShouldReturnMaterials() throws Exception {
        // Given
        Long classId = 1L;
        List<MaterialResponse> expectedMaterials = Arrays.asList(materialResponse);
        when(materialService.getMaterialsByClass(classId)).thenReturn(expectedMaterials);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/materials", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Tài liệu chương 1"));
    }

    // ========== TEST QUẢN LÝ ĐIỂM DANH ==========

    @Test
    void recordAttendance_ShouldReturnAttendance() throws Exception {
        // Given
        AttendanceRequest request = new AttendanceRequest(1L, LocalDate.now(), Arrays.asList());
        when(attendanceService.recordAttendance(any(AttendanceRequest.class)))
                .thenReturn(attendanceResponse);

        // When & Then
        mockMvc.perform(post("/api/teacher/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.classCode").value("CS101"));
    }

    @Test
    void getAttendanceByClass_ShouldReturnAttendance() throws Exception {
        // Given
        Long classId = 1L;
        List<AttendanceResponse> expectedAttendance = Arrays.asList(attendanceResponse);
        when(attendanceService.getAttendanceByClass(classId)).thenReturn(expectedAttendance);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/attendance", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"));
    }

    @Test
    void generateAttendanceReport_ShouldReturnReport() throws Exception {
        // Given
        Long classId = 1L;
        AttendanceReportResponse report = new AttendanceReportResponse(
                "CS101", "Lập trình Java", "HK1-2024", 30, Arrays.asList()
        );
        when(attendanceService.generateAttendanceReport(classId)).thenReturn(report);

        // When & Then
        mockMvc.perform(get("/api/teacher/classes/{classId}/attendance/report", classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.classCode").value("CS101"))
                .andExpect(jsonPath("$.totalSessions").value(30));
    }

    // ========== TEST QUẢN LÝ BÀI NỘP ==========

    @Test
    void getSubmissionsByAssignment_ShouldReturnSubmissions() throws Exception {
        // Given
        Long assignmentId = 1L;
        List<SubmissionResponse> expectedSubmissions = Arrays.asList(submissionResponse);
        when(submissionService.getSubmissionsByAssignment(assignmentId)).thenReturn(expectedSubmissions);

        // When & Then
        mockMvc.perform(get("/api/teacher/assignments/{assignmentId}/submissions", assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].assignmentTitle").value("Bài tập 1"));
    }

    @Test
    void gradeSubmission_ShouldReturnSubmission() throws Exception {
        // Given
        SubmissionGradeRequest request = new SubmissionGradeRequest(1L, 8.5f);
        when(submissionService.gradeSubmission(any(SubmissionGradeRequest.class)))
                .thenReturn(submissionResponse);

        // When & Then
        mockMvc.perform(post("/api/teacher/submissions/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.score").value(8.5));
    }
}
