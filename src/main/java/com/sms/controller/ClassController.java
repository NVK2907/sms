package com.sms.controller;

import com.sms.dto.request.ClassRequest;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.ClassListResponse;
import com.sms.dto.response.ClassResponse;
import com.sms.service.ClassService;
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
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    /**
     * Tạo lớp học mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody ClassRequest classRequest) {
        try {
            ClassResponse classResponse = classService.createClass(classRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo lớp học thành công", classResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin lớp học
     */
    @PutMapping("/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody ClassRequest classRequest) {
        try {
            ClassResponse classResponse = classService.updateClass(classId, classRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật lớp học thành công", classResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa lớp học
     */
    @DeleteMapping("/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long classId) {
        try {
            classService.deleteClass(classId);
            return ResponseEntity.ok(ApiResponse.success("Xóa lớp học thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin lớp học theo ID
     */
    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassById(@PathVariable Long classId) {
        try {
            ClassResponse classResponse = classService.getClassById(classId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lớp học thành công", classResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin lớp học theo mã lớp
     */
    @GetMapping("/code/{classCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ClassResponse>> getClassByCode(@PathVariable String classCode) {
        try {
            ClassResponse classResponse = classService.getClassByCode(classCode);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lớp học thành công", classResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học với phân trang và tìm kiếm
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ClassListResponse>> searchClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            ClassListResponse classListResponse = classService.searchClasses(pageable, keyword);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học thành công", classListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học theo môn học
     */
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassesBySubject(@PathVariable Long subjectId) {
        try {
            List<ClassResponse> classes = classService.getClassesBySubject(subjectId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học theo môn học thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học theo học kỳ
     */
    @GetMapping("/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassesBySemester(@PathVariable Long semesterId) {
        try {
            List<ClassResponse> classes = classService.getClassesBySemester(semesterId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học theo học kỳ thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học theo giáo viên
     */
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassesByTeacher(@PathVariable Long teacherId) {
        try {
            List<ClassResponse> classes = classService.getClassesByTeacher(teacherId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học theo giáo viên thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học theo môn học và học kỳ
     */
    @GetMapping("/subject/{subjectId}/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassesBySubjectAndSemester(
            @PathVariable Long subjectId,
            @PathVariable Long semesterId) {
        try {
            List<ClassResponse> classes = classService.getClassesBySubjectAndSemester(subjectId, semesterId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học theo môn học và học kỳ thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học theo giáo viên và học kỳ
     */
    @GetMapping("/teacher/{teacherId}/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse>>> getClassesByTeacherAndSemester(
            @PathVariable Long teacherId,
            @PathVariable Long semesterId) {
        try {
            List<ClassResponse> classes = classService.getClassesByTeacherAndSemester(teacherId, semesterId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học theo giáo viên và học kỳ thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Phân công giáo viên giảng dạy
     */
    @PutMapping("/{classId}/assign-teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignTeacherToClass(
            @PathVariable Long classId,
            @PathVariable Long teacherId) {
        try {
            classService.assignTeacherToClass(classId, teacherId);
            return ResponseEntity.ok(ApiResponse.success("Phân công giáo viên giảng dạy thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Hủy phân công giáo viên
     */
    @PutMapping("/{classId}/remove-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeTeacherFromClass(@PathVariable Long classId) {
        try {
            classService.removeTeacherFromClass(classId);
            return ResponseEntity.ok(ApiResponse.success("Hủy phân công giáo viên thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách sinh viên trong lớp
     */
    @GetMapping("/{classId}/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassResponse.StudentInfo>>> getClassStudents(@PathVariable Long classId) {
        try {
            List<ClassResponse.StudentInfo> students = classService.getClassStudents(classId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sinh viên trong lớp thành công", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
