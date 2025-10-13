package com.sms.controller;

import com.sms.dto.request.*;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.StudentListResponse;
import com.sms.dto.response.StudentResponse;
import com.sms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Tạo sinh viên mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        try {
            StudentResponse studentResponse = studentService.createStudent(studentRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo sinh viên thành công", studentResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin sinh viên
     */
    @PutMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody StudentUpdateRequest studentUpdateRequest) {
        try {
            StudentResponse studentResponse = studentService.updateStudent(studentId, studentUpdateRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật sinh viên thành công", studentResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa sinh viên
     */
    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success("Xóa sinh viên thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin sinh viên theo ID
     */
    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long studentId) {
        try {
            StudentResponse studentResponse = studentService.getStudentById(studentId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin sinh viên thành công", studentResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin sinh viên theo mã sinh viên
     */
    @GetMapping("/code/{studentCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentByCode(@PathVariable String studentCode) {
        try {
            StudentResponse studentResponse = studentService.getStudentByCode(studentCode);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin sinh viên thành công", studentResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách sinh viên với phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<StudentListResponse>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            StudentListResponse studentListResponse = studentService.getAllStudents(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sinh viên thành công", studentListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Tìm kiếm và lọc sinh viên
     */
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<StudentListResponse>> searchStudents(
            @RequestBody StudentSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            StudentListResponse studentListResponse = studentService.searchStudents(searchRequest, pageable);
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm sinh viên thành công", studentListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách sinh viên theo lớp
     */
    @GetMapping("/class/{className}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByClass(@PathVariable String className) {
        try {
            List<StudentResponse> students = studentService.getStudentsByClass(className);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sinh viên theo lớp thành công", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách sinh viên theo chuyên ngành
     */
    @GetMapping("/major/{major}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByMajor(@PathVariable String major) {
        try {
            List<StudentResponse> students = studentService.getStudentsByMajor(major);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sinh viên theo chuyên ngành thành công", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách sinh viên theo khóa học
     */
    @GetMapping("/course/{courseYear}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByCourseYear(@PathVariable Integer courseYear) {
        try {
            List<StudentResponse> students = studentService.getStudentsByCourseYear(courseYear);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách sinh viên theo khóa học thành công", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Gán sinh viên vào lớp học
     */
    @PostMapping("/assign-classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignStudentToClasses(@Valid @RequestBody ClassAssignmentRequest classAssignmentRequest) {
        try {
            studentService.assignStudentToClasses(classAssignmentRequest);
            return ResponseEntity.ok(ApiResponse.success("Gán sinh viên vào lớp học thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Hủy gán sinh viên khỏi lớp học
     */
    @DeleteMapping("/{studentId}/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeStudentFromClasses(
            @PathVariable Long studentId,
            @RequestBody List<Long> classIds) {
        try {
            studentService.removeStudentFromClasses(studentId, classIds);
            return ResponseEntity.ok(ApiResponse.success("Hủy gán sinh viên khỏi lớp học thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách lớp học của sinh viên
     */
    @GetMapping("/{studentId}/classes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponse.ClassInfo>>> getStudentClasses(@PathVariable Long studentId) {
        try {
            List<StudentResponse.ClassInfo> classes = studentService.getStudentClasses(studentId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lớp học của sinh viên thành công", classes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xuất danh sách sinh viên ra Excel
     */
    @PostMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportStudentsToExcel(@RequestBody StudentSearchRequest searchRequest) {
        try {
            byte[] excelData = studentService.exportStudentsToExcel(searchRequest);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "danh_sach_sinh_vien.xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Xuất danh sách sinh viên ra PDF
     */
    @PostMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<byte[]> exportStudentsToPDF(@RequestBody StudentSearchRequest searchRequest) {
        try {
            byte[] pdfData = studentService.exportStudentsToPDF(searchRequest);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "danh_sach_sinh_vien.pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
