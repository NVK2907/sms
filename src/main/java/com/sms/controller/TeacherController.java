package com.sms.controller;

import com.sms.dto.request.TeacherRequest;
import com.sms.dto.request.TeacherUpdateRequest;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.TeacherListResponse;
import com.sms.dto.response.TeacherResponse;
import com.sms.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;
    
    /**
     * Tạo giáo viên mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponse>> createTeacher(@Valid @RequestBody TeacherRequest teacherRequest) {
        try {
            TeacherResponse teacherResponse = teacherService.createTeacher(teacherRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo giáo viên thành công", teacherResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin giáo viên
     */
    @PutMapping("/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherResponse>> updateTeacher(
            @PathVariable Long teacherId,
            @Valid @RequestBody TeacherUpdateRequest teacherUpdateRequest) {
        try {
            TeacherResponse teacherResponse = teacherService.updateTeacher(teacherId, teacherUpdateRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật giáo viên thành công", teacherResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa giáo viên
     */
    @DeleteMapping("/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(@PathVariable Long teacherId) {
        try {
            teacherService.deleteTeacher(teacherId);
            return ResponseEntity.ok(ApiResponse.success("Xóa giáo viên thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin giáo viên theo ID
     */
    @GetMapping("/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherById(@PathVariable Long teacherId) {
        try {
            TeacherResponse teacherResponse = teacherService.getTeacherById(teacherId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin giáo viên thành công", teacherResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin giáo viên theo mã giáo viên
     */
    @GetMapping("/code/{teacherCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponse>> getTeacherByCode(@PathVariable String teacherCode) {
        try {
            TeacherResponse teacherResponse = teacherService.getTeacherByCode(teacherCode);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin giáo viên thành công", teacherResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách giáo viên với phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherListResponse>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Boolean isActive) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            TeacherListResponse teacherListResponse = teacherService.getAllTeachers(pageable, isActive);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách giáo viên thành công", teacherListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Tìm kiếm giáo viên
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherListResponse>> searchTeachers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Boolean isActive) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            TeacherListResponse teacherListResponse = teacherService.searchTeachers(keyword, pageable, isActive);
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm giáo viên thành công", teacherListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách giáo viên theo khoa/bộ môn
     */
    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getTeachersByDepartment(@PathVariable String department) {
        try {
            List<TeacherResponse> teachers = teacherService.getTeachersByDepartment(department);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách giáo viên theo khoa/bộ môn thành công", teachers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách giáo viên theo chức danh
     */
    @GetMapping("/title/{title}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getTeachersByTitle(@PathVariable String title) {
        try {
            List<TeacherResponse> teachers = teacherService.getTeachersByTitle(title);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách giáo viên theo chức danh thành công", teachers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Gán môn học cho giáo viên
     */
    @PostMapping("/{teacherId}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignSubjectsToTeacher(
            @PathVariable Long teacherId,
            @RequestBody List<Long> subjectIds) {
        try {
            teacherService.assignSubjectsToTeacher(teacherId, subjectIds);
            return ResponseEntity.ok(ApiResponse.success("Gán môn học cho giáo viên thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Hủy gán môn học của giáo viên
     */
    @DeleteMapping("/{teacherId}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeSubjectsFromTeacher(
            @PathVariable Long teacherId,
            @RequestBody List<Long> subjectIds) {
        try {
            teacherService.removeSubjectsFromTeacher(teacherId, subjectIds);
            return ResponseEntity.ok(ApiResponse.success("Hủy gán môn học của giáo viên thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách môn học của giáo viên
     */
    @GetMapping("/{teacherId}/subjects")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherResponse.SubjectInfo>>> getTeacherSubjects(@PathVariable Long teacherId) {
        try {
            List<TeacherResponse.SubjectInfo> subjects = teacherService.getTeacherSubjects(teacherId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách môn học của giáo viên thành công", subjects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
