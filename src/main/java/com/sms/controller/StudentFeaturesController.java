package com.sms.controller;

import com.sms.dto.request.*;
import com.sms.dto.response.*;
import com.sms.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentFeaturesController {
    
    @Autowired
    private StudentProfileService studentProfileService;
    
    @Autowired
    private StudentAcademicService studentAcademicService;
    
    @Autowired
    private StudentScheduleService studentScheduleService;
    
    @Autowired
    private StudentGradeService studentGradeService;
    
    @Autowired
    private StudentMaterialService studentMaterialService;
    
    @Autowired
    private StudentAssignmentService studentAssignmentService;
    
    // ========== THÔNG TIN CÁ NHÂN ==========
    
    @GetMapping("/profile/{studentId}")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long studentId) {
        try {
            StudentProfileResponse profile = studentProfileService.getStudentProfile(studentId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/profile/{studentId}")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @PathVariable Long studentId, @RequestBody StudentUpdateRequest request) {
        try {
            StudentProfileResponse profile = studentProfileService.updateStudentProfile(studentId, request);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/change-password/{studentId}")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long studentId, @RequestBody ChangePasswordRequest request) {
        try {
            studentProfileService.changePassword(studentId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== HỌC TẬP ==========
    
    @GetMapping("/{studentId}/classes/available")
    public ResponseEntity<List<StudentClassResponse>> getAvailableClasses(@PathVariable Long studentId) {
        try {
            List<StudentClassResponse> classes = studentAcademicService.getAvailableClasses(studentId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/classes/registered")
    public ResponseEntity<List<StudentClassResponse>> getRegisteredClasses(@PathVariable Long studentId) {
        try {
            List<StudentClassResponse> classes = studentAcademicService.getRegisteredClasses(studentId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/classes/registered/semester/{semesterId}")
    public ResponseEntity<List<StudentClassResponse>> getRegisteredClassesBySemester(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        try {
            List<StudentClassResponse> classes = studentAcademicService.getRegisteredClassesBySemester(studentId, semesterId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/register-class")
    public ResponseEntity<Void> registerForClass(@RequestBody ClassRegistrationRequest request) {
        try {
            studentAcademicService.registerForClass(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{studentId}/unregister-class/{classId}")
    public ResponseEntity<Void> unregisterFromClass(@PathVariable Long studentId, @PathVariable Long classId) {
        try {
            studentAcademicService.unregisterFromClass(studentId, classId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== LỊCH HỌC & LỊCH THI ==========
    
    @GetMapping("/{studentId}/schedule/weekly")
    public ResponseEntity<List<StudentScheduleResponse>> getWeeklySchedule(
            @PathVariable Long studentId, 
            @RequestParam(required = false) String startDate) {
        try {
            LocalDate start;
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate);
            } else {
                // Sử dụng ngày hiện tại làm mặc định
                start = LocalDate.now();
            }
            List<StudentScheduleResponse> schedule = studentScheduleService.getWeeklySchedule(studentId, start);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/schedule/daily")
    public ResponseEntity<List<StudentScheduleResponse>> getDailySchedule(
            @PathVariable Long studentId, @RequestParam String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date);
            List<StudentScheduleResponse> schedule = studentScheduleService.getDailySchedule(studentId, scheduleDate);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/schedule/exams/semester/{semesterId}")
    public ResponseEntity<List<StudentScheduleResponse>> getExamSchedule(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        try {
            List<StudentScheduleResponse> examSchedule = studentScheduleService.getExamSchedule(studentId, semesterId);
            return ResponseEntity.ok(examSchedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== ĐIỂM SỐ ==========
    
    @GetMapping("/{studentId}/grades")
    public ResponseEntity<List<StudentGradeResponse>> getGradesByStudent(@PathVariable Long studentId) {
        try {
            List<StudentGradeResponse> grades = studentGradeService.getGradesByStudent(studentId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/grades/semester/{semesterId}")
    public ResponseEntity<List<StudentGradeResponse>> getGradesByStudentAndSemester(
            @PathVariable Long studentId, @PathVariable Long semesterId) {
        try {
            List<StudentGradeResponse> grades = studentGradeService.getGradesByStudentAndSemester(studentId, semesterId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/grades/class/{classId}")
    public ResponseEntity<StudentGradeResponse> getGradeByClassAndStudent(
            @PathVariable Long studentId, @PathVariable Long classId) {
        try {
            StudentGradeResponse grade = studentGradeService.getGradeByClassAndStudent(studentId, classId);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/gpa")
    public ResponseEntity<StudentGradeResponse.GPASummary> getGPASummary(@PathVariable Long studentId) {
        try {
            StudentGradeResponse.GPASummary gpaSummary = studentGradeService.getGPASummary(studentId);
            return ResponseEntity.ok(gpaSummary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== TÀI LIỆU ==========
    
    @GetMapping("/{studentId}/materials")
    public ResponseEntity<List<StudentMaterialResponse>> getMaterialsByStudent(@PathVariable Long studentId) {
        try {
            List<StudentMaterialResponse> materials = studentMaterialService.getMaterialsByStudent(studentId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/materials/class/{classId}")
    public ResponseEntity<List<StudentMaterialResponse>> getMaterialsByClass(
            @PathVariable Long studentId, @PathVariable Long classId) {
        try {
            List<StudentMaterialResponse> materials = studentMaterialService.getMaterialsByClass(studentId, classId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== BÀI TẬP ==========
    
    @GetMapping("/{studentId}/assignments")
    public ResponseEntity<List<StudentAssignmentResponse>> getAssignmentsByStudent(@PathVariable Long studentId) {
        try {
            List<StudentAssignmentResponse> assignments = studentAssignmentService.getAssignmentsByStudent(studentId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/assignments/class/{classId}")
    public ResponseEntity<List<StudentAssignmentResponse>> getAssignmentsByClass(
            @PathVariable Long studentId, @PathVariable Long classId) {
        try {
            List<StudentAssignmentResponse> assignments = studentAssignmentService.getAssignmentsByClass(studentId, classId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{studentId}/assignments/{assignmentId}")
    public ResponseEntity<StudentAssignmentResponse> getAssignmentById(
            @PathVariable Long studentId, @PathVariable Long assignmentId) {
        try {
            StudentAssignmentResponse assignment = studentAssignmentService.getAssignmentById(studentId, assignmentId);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/submit-assignment")
    public ResponseEntity<Void> submitAssignment(@RequestBody SubmissionRequest request) {
        try {
            studentAssignmentService.submitAssignment(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{studentId}/update-submission/{submissionId}")
    public ResponseEntity<Void> updateSubmission(
            @PathVariable Long studentId, @PathVariable Long submissionId, @RequestBody SubmissionRequest request) {
        try {
            studentAssignmentService.updateSubmission(studentId, submissionId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
