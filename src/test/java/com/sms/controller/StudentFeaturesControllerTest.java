package com.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.request.*;
import com.sms.dto.response.*;
import com.sms.service.*;
import com.sms.security.JwtAuthenticationFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentFeaturesController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentFeaturesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentProfileService studentProfileService;

    @MockBean
    private StudentAcademicService studentAcademicService;

    @MockBean
    private StudentScheduleService studentScheduleService;

    @MockBean
    private StudentGradeService studentGradeService;

    @MockBean
    private StudentMaterialService studentMaterialService;

    @MockBean
    private StudentAssignmentService studentAssignmentService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private StudentProfileResponse studentProfileResponse;
    private StudentClassResponse studentClassResponse;
    private StudentGradeResponse studentGradeResponse;
    private StudentScheduleResponse studentScheduleResponse;
    private StudentMaterialResponse studentMaterialResponse;
    private StudentAssignmentResponse studentAssignmentResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        studentProfileResponse = new StudentProfileResponse(
                1L, "SV001", "student001", "Nguyễn Văn A", "student@example.com",
                "0123456789", "Nam", LocalDate.of(2000, 1, 1), "Hà Nội",
                "CNTT", "Công nghệ thông tin", 2020, true, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        studentClassResponse = new StudentClassResponse(
                1L, "CS101", "Lập trình Java", "CS101", "HK1-2024", "Thầy B",
                50, 30, LocalDateTime.now(), true, Arrays.asList()
        );

        studentGradeResponse = new StudentGradeResponse(
                1L, "CS101", "Lập trình Java", "CS101", 3, 8.5f, 9.0f, 8.0f, 8.5f, "B+", LocalDateTime.now()
        );

        studentScheduleResponse = new StudentScheduleResponse(
                LocalDate.now(), Arrays.asList()
        );

        studentMaterialResponse = new StudentMaterialResponse(
                1L, "CS101", "Lập trình Java", "Tài liệu chương 1",
                "/uploads/chapter1.pdf", "chapter1.pdf", LocalDateTime.now()
        );

        studentAssignmentResponse = new StudentAssignmentResponse(
                1L, "CS101", "Lập trình Java", "Bài tập 1", "Mô tả bài tập",
                LocalDateTime.now().plusDays(7), LocalDateTime.now(), true,
                LocalDateTime.now(), 8.5f, true
        );
    }

    // ========== TEST THÔNG TIN CÁ NHÂN ==========

    @Test
    void getStudentProfile_ShouldReturnProfile() throws Exception {
        // Given
        Long studentId = 1L;
        when(studentProfileService.getStudentProfile(studentId)).thenReturn(studentProfileResponse);

        // When & Then
        mockMvc.perform(get("/api/student/profile/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentCode").value("SV001"))
                .andExpect(jsonPath("$.fullName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.email").value("student@example.com"));
    }

    @Test
    void updateStudentProfile_ShouldReturnUpdatedProfile() throws Exception {
        // Given
        Long studentId = 1L;
        StudentUpdateRequest request = new StudentUpdateRequest(
                "Nguyễn Văn A Updated", "student.updated@example.com", "0987654321",
                "Nam", LocalDate.of(2000, 1, 1), "TP.HCM", "CNTT", "Công nghệ thông tin", 2020
        );
        when(studentProfileService.updateStudentProfile(eq(studentId), any(StudentUpdateRequest.class)))
                .thenReturn(studentProfileResponse);

        // When & Then
        mockMvc.perform(put("/api/student/profile/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Nguyễn Văn A"));
    }

    @Test
    void changePassword_ShouldReturnOk() throws Exception {
        // Given
        Long studentId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword", "newPassword", "newPassword"
        );

        // When & Then
        mockMvc.perform(post("/api/student/change-password/{studentId}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ========== TEST HỌC TẬP ==========

    @Test
    void getAvailableClasses_ShouldReturnClasses() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentClassResponse> expectedClasses = Arrays.asList(studentClassResponse);
        when(studentAcademicService.getAvailableClasses(studentId)).thenReturn(expectedClasses);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/classes/available", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"))
                .andExpect(jsonPath("$[0].subjectName").value("Lập trình Java"));
    }

    @Test
    void getRegisteredClasses_ShouldReturnClasses() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentClassResponse> expectedClasses = Arrays.asList(studentClassResponse);
        when(studentAcademicService.getRegisteredClasses(studentId)).thenReturn(expectedClasses);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/classes/registered", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].isRegistered").value(true));
    }

    @Test
    void getRegisteredClassesBySemester_ShouldReturnClasses() throws Exception {
        // Given
        Long studentId = 1L;
        Long semesterId = 1L;
        List<StudentClassResponse> expectedClasses = Arrays.asList(studentClassResponse);
        when(studentAcademicService.getRegisteredClassesBySemester(studentId, semesterId))
                .thenReturn(expectedClasses);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/classes/registered/semester/{semesterId}", studentId, semesterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void registerForClass_ShouldReturnOk() throws Exception {
        // Given
        ClassRegistrationRequest request = new ClassRegistrationRequest(1L, 1L);

        // When & Then
        mockMvc.perform(post("/api/student/register-class")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void unregisterFromClass_ShouldReturnOk() throws Exception {
        // Given
        Long studentId = 1L;
        Long classId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/student/{studentId}/unregister-class/{classId}", studentId, classId))
                .andExpect(status().isOk());
    }

    // ========== TEST LỊCH HỌC & LỊCH THI ==========

    @Test
    void getWeeklySchedule_ShouldReturnSchedule() throws Exception {
        // Given
        Long studentId = 1L;
        String startDate = "2024-01-01";
        List<StudentScheduleResponse> expectedSchedule = Arrays.asList(studentScheduleResponse);
        when(studentScheduleService.getWeeklySchedule(studentId, LocalDate.parse(startDate)))
                .thenReturn(expectedSchedule);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/schedule/weekly", studentId)
                        .param("startDate", startDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    void getWeeklySchedule_WithoutStartDate_ShouldUseCurrentDate() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentScheduleResponse> expectedSchedule = Arrays.asList(studentScheduleResponse);
        when(studentScheduleService.getWeeklySchedule(eq(studentId), any(LocalDate.class)))
                .thenReturn(expectedSchedule);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/schedule/weekly", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    void getDailySchedule_ShouldReturnSchedule() throws Exception {
        // Given
        Long studentId = 1L;
        String date = "2024-01-01";
        List<StudentScheduleResponse> expectedSchedule = Arrays.asList(studentScheduleResponse);
        when(studentScheduleService.getDailySchedule(studentId, LocalDate.parse(date)))
                .thenReturn(expectedSchedule);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/schedule/daily", studentId)
                        .param("date", date))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    void getExamSchedule_ShouldReturnSchedule() throws Exception {
        // Given
        Long studentId = 1L;
        Long semesterId = 1L;
        List<StudentScheduleResponse> expectedSchedule = Arrays.asList(studentScheduleResponse);
        when(studentScheduleService.getExamSchedule(studentId, semesterId))
                .thenReturn(expectedSchedule);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/schedule/exams/semester/{semesterId}", studentId, semesterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].date").exists());
    }

    // ========== TEST ĐIỂM SỐ ==========

    @Test
    void getGradesByStudent_ShouldReturnGrades() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentGradeResponse> expectedGrades = Arrays.asList(studentGradeResponse);
        when(studentGradeService.getGradesByStudent(studentId)).thenReturn(expectedGrades);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/grades", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].classCode").value("CS101"))
                .andExpect(jsonPath("$[0].letterGrade").value("B+"));
    }

    @Test
    void getGradesByStudentAndSemester_ShouldReturnGrades() throws Exception {
        // Given
        Long studentId = 1L;
        Long semesterId = 1L;
        List<StudentGradeResponse> expectedGrades = Arrays.asList(studentGradeResponse);
        when(studentGradeService.getGradesByStudentAndSemester(studentId, semesterId))
                .thenReturn(expectedGrades);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/grades/semester/{semesterId}", studentId, semesterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getGradeByClassAndStudent_ShouldReturnGrade() throws Exception {
        // Given
        Long studentId = 1L;
        Long classId = 1L;
        when(studentGradeService.getGradeByClassAndStudent(classId, studentId))
                .thenReturn(studentGradeResponse);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/grades/class/{classId}", studentId, classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.letterGrade").value("B+"));
    }

    @Test
    void getGPASummary_ShouldReturnGPASummary() throws Exception {
        // Given
        Long studentId = 1L;
        StudentGradeResponse.GPASummary gpaSummary = new StudentGradeResponse.GPASummary(
                3.5, 30, 24, Arrays.asList(studentGradeResponse)
        );
        when(studentGradeService.getGPASummary(studentId)).thenReturn(gpaSummary);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/gpa", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gpa").value(3.5))
                .andExpect(jsonPath("$.totalCredits").value(30))
                .andExpect(jsonPath("$.completedCredits").value(24));
    }

    // ========== TEST TÀI LIỆU ==========

    @Test
    void getMaterialsByStudent_ShouldReturnMaterials() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentMaterialResponse> expectedMaterials = Arrays.asList(studentMaterialResponse);
        when(studentMaterialService.getMaterialsByStudent(studentId)).thenReturn(expectedMaterials);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/materials", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Tài liệu chương 1"));
    }

    @Test
    void getMaterialsByClass_ShouldReturnMaterials() throws Exception {
        // Given
        Long studentId = 1L;
        Long classId = 1L;
        List<StudentMaterialResponse> expectedMaterials = Arrays.asList(studentMaterialResponse);
        when(studentMaterialService.getMaterialsByClass(studentId, classId)).thenReturn(expectedMaterials);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/materials/class/{classId}", studentId, classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ========== TEST BÀI TẬP ==========

    @Test
    void getAssignmentsByStudent_ShouldReturnAssignments() throws Exception {
        // Given
        Long studentId = 1L;
        List<StudentAssignmentResponse> expectedAssignments = Arrays.asList(studentAssignmentResponse);
        when(studentAssignmentService.getAssignmentsByStudent(studentId)).thenReturn(expectedAssignments);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/assignments", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Bài tập 1"))
                .andExpect(jsonPath("$[0].isSubmitted").value(true));
    }

    @Test
    void getAssignmentsByClass_ShouldReturnAssignments() throws Exception {
        // Given
        Long studentId = 1L;
        Long classId = 1L;
        List<StudentAssignmentResponse> expectedAssignments = Arrays.asList(studentAssignmentResponse);
        when(studentAssignmentService.getAssignmentsByClass(studentId, classId)).thenReturn(expectedAssignments);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/assignments/class/{classId}", studentId, classId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAssignmentById_ShouldReturnAssignment() throws Exception {
        // Given
        Long studentId = 1L;
        Long assignmentId = 1L;
        when(studentAssignmentService.getAssignmentById(studentId, assignmentId))
                .thenReturn(studentAssignmentResponse);

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/assignments/{assignmentId}", studentId, assignmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.isSubmitted").value(true));
    }

    @Test
    void submitAssignment_ShouldReturnOk() throws Exception {
        // Given
        SubmissionRequest request = new SubmissionRequest(1L, 1L, "/uploads/submission.pdf");

        // When & Then
        mockMvc.perform(post("/api/student/submit-assignment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateSubmission_ShouldReturnOk() throws Exception {
        // Given
        Long studentId = 1L;
        Long submissionId = 1L;
        SubmissionRequest request = new SubmissionRequest(1L, 1L, "/uploads/submission_updated.pdf");

        // When & Then
        mockMvc.perform(put("/api/student/{studentId}/update-submission/{submissionId}", studentId, submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
