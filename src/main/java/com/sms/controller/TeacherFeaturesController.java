package com.sms.controller;

import com.sms.dto.request.*;
import com.sms.dto.response.*;
import com.sms.service.*;
import com.sms.service.TeacherScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherFeaturesController {
    
    @Autowired
    private TeacherClassService teacherClassService;
    
    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private SubmissionService submissionService;
    
    @Autowired
    private TeacherScheduleService teacherScheduleService;
    
    // ========== QUẢN LÝ LỚP VÀ SINH VIÊN ==========
    
    @GetMapping("/classes/{teacherId}")
    public ResponseEntity<List<TeacherClassResponse>> getClassesByTeacher(@PathVariable Long teacherId) {
        try {
            List<TeacherClassResponse> classes = teacherClassService.getClassesByTeacher(teacherId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{teacherId}/semester/{semesterId}")
    public ResponseEntity<List<TeacherClassResponse>> getClassesByTeacherAndSemester(
            @PathVariable Long teacherId, @PathVariable Long semesterId) {
        try {
            List<TeacherClassResponse> classes = teacherClassService.getClassesByTeacherAndSemester(teacherId, semesterId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/students")
    public ResponseEntity<TeacherClassResponse> getClassWithStudents(@PathVariable Long classId) {
        try {
            TeacherClassResponse classWithStudents = teacherClassService.getClassWithStudents(classId);
            return ResponseEntity.ok(classWithStudents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ ĐIỂM ==========
    
    @PostMapping("/grades")
    public ResponseEntity<StudentGradeResponse> createOrUpdateGrade(@RequestBody StudentGradeRequest request) {
        try {
            StudentGradeResponse grade = gradeService.createOrUpdateGrade(request);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/grades")
    public ResponseEntity<List<StudentGradeResponse>> getGradesByClass(@PathVariable Long classId) {
        try {
            List<StudentGradeResponse> grades = gradeService.getGradesByClass(classId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/students/{studentId}/grades")
    public ResponseEntity<List<StudentGradeResponse>> getGradesByStudent(@PathVariable Long studentId) {
        try {
            List<StudentGradeResponse> grades = gradeService.getGradesByStudent(studentId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/grades/export")
    public ResponseEntity<List<StudentGradeResponse>> exportGradesByClass(@PathVariable Long classId) {
        try {
            List<StudentGradeResponse> grades = gradeService.exportGradesByClass(classId);
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/grades/{gradeId}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long gradeId) {
        try {
            gradeService.deleteGrade(gradeId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ BÀI TẬP ==========
    
    @PostMapping("/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(@RequestBody AssignmentRequest request) {
        try {
            AssignmentResponse assignment = assignmentService.createAssignment(request);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long assignmentId, @RequestBody AssignmentRequest request) {
        try {
            AssignmentResponse assignment = assignmentService.updateAssignment(assignmentId, request);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/assignments/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            assignmentService.deleteAssignment(assignmentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByClass(@PathVariable Long classId) {
        try {
            List<AssignmentResponse> assignments = assignmentService.getAssignmentsByClass(classId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/teachers/{teacherId}/assignments")
    public ResponseEntity<List<AssignmentResponse>> getAssignmentsByTeacher(@PathVariable Long teacherId) {
        try {
            List<AssignmentResponse> assignments = assignmentService.getAssignmentsByTeacher(teacherId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignmentById(@PathVariable Long assignmentId) {
        try {
            AssignmentResponse assignment = assignmentService.getAssignmentById(assignmentId);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ TÀI LIỆU ==========
    
    @PostMapping("/materials")
    public ResponseEntity<MaterialResponse> uploadMaterial(@RequestBody MaterialRequest request) {
        try {
            MaterialResponse material = materialService.uploadMaterial(request);
            return ResponseEntity.ok(material);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/materials/{materialId}")
    public ResponseEntity<MaterialResponse> updateMaterial(
            @PathVariable Long materialId, @RequestBody MaterialRequest request) {
        try {
            MaterialResponse material = materialService.updateMaterial(materialId, request);
            return ResponseEntity.ok(material);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/materials/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long materialId) {
        try {
            materialService.deleteMaterial(materialId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/materials")
    public ResponseEntity<List<MaterialResponse>> getMaterialsByClass(@PathVariable Long classId) {
        try {
            List<MaterialResponse> materials = materialService.getMaterialsByClass(classId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/teachers/{teacherId}/materials")
    public ResponseEntity<List<MaterialResponse>> getMaterialsByTeacher(@PathVariable Long teacherId) {
        try {
            List<MaterialResponse> materials = materialService.getMaterialsByTeacher(teacherId);
            return ResponseEntity.ok(materials);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ ĐIỂM DANH ==========
    
    @PostMapping("/attendance")
    public ResponseEntity<AttendanceResponse> recordAttendance(@RequestBody AttendanceRequest request) {
        try {
            AttendanceResponse attendance = attendanceService.recordAttendance(request);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/attendance/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long attendanceId, @RequestBody AttendanceRequest request) {
        try {
            AttendanceResponse attendance = attendanceService.updateAttendance(attendanceId, request);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/attendance/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        try {
            attendanceService.deleteAttendance(attendanceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/attendance")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByClass(@PathVariable Long classId) {
        try {
            List<AttendanceResponse> attendances = attendanceService.getAttendanceByClass(classId);
            return ResponseEntity.ok(attendances);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/attendance/{date}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByClassAndDate(
            @PathVariable Long classId, @PathVariable String date) {
        try {
            LocalDate attendanceDate = LocalDate.parse(date);
            List<AttendanceResponse> attendances = attendanceService.getAttendanceByClassAndDate(classId, attendanceDate);
            return ResponseEntity.ok(attendances);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/classes/{classId}/attendance/report")
    public ResponseEntity<AttendanceReportResponse> generateAttendanceReport(@PathVariable Long classId) {
        try {
            AttendanceReportResponse report = attendanceService.generateAttendanceReport(classId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ BÀI NỘP ==========
    
    @GetMapping("/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        try {
            List<SubmissionResponse> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/assignments/{assignmentId}/submissions/ungraded")
    public ResponseEntity<List<SubmissionResponse>> getUngradedSubmissions(@PathVariable Long assignmentId) {
        try {
            List<SubmissionResponse> submissions = submissionService.getUngradedSubmissions(assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/submissions/grade")
    public ResponseEntity<SubmissionResponse> gradeSubmission(@RequestBody SubmissionGradeRequest request) {
        try {
            SubmissionResponse submission = submissionService.gradeSubmission(request);
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(@PathVariable Long submissionId) {
        try {
            SubmissionResponse submission = submissionService.getSubmissionById(submissionId);
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== QUẢN LÝ LỊCH HỌC ==========
    
    @GetMapping("/schedule/{teacherId}/weekly")
    public ResponseEntity<List<TeacherScheduleResponse>> getWeeklySchedule(
            @PathVariable Long teacherId, @RequestParam String startDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            List<TeacherScheduleResponse> schedule = teacherScheduleService.getWeeklySchedule(teacherId, start);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/schedule/{teacherId}/daily")
    public ResponseEntity<List<TeacherScheduleResponse>> getDailySchedule(
            @PathVariable Long teacherId, @RequestParam String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date);
            List<TeacherScheduleResponse> schedule = teacherScheduleService.getDailySchedule(teacherId, scheduleDate);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/schedule/{teacherId}/exams/semester/{semesterId}")
    public ResponseEntity<List<TeacherScheduleResponse>> getExamSchedule(
            @PathVariable Long teacherId, @PathVariable Long semesterId) {
        try {
            List<TeacherScheduleResponse> examSchedule = teacherScheduleService.getExamSchedule(teacherId, semesterId);
            return ResponseEntity.ok(examSchedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
