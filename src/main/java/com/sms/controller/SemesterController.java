package com.sms.controller;

import com.sms.dto.request.*;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.AcademicYearListResponse;
import com.sms.dto.response.AcademicYearResponse;
import com.sms.dto.response.SemesterListResponse;
import com.sms.dto.response.SemesterResponse;
import com.sms.service.SemesterService;
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
@RequestMapping("/api/semesters")
@CrossOrigin(origins = "*")
public class SemesterController {
    
    @Autowired
    private SemesterService semesterService;
    
    // Academic Year Management Endpoints
    
    /**
     * Tạo năm học mới
     */
    @PostMapping("/academic-years")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> createAcademicYear(@Valid @RequestBody AcademicYearRequest academicYearRequest) {
        try {
            AcademicYearResponse academicYearResponse = semesterService.createAcademicYear(academicYearRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo năm học thành công", academicYearResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin năm học
     */
    @PutMapping("/academic-years/{academicYearId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> updateAcademicYear(
            @PathVariable Long academicYearId,
            @Valid @RequestBody AcademicYearRequest academicYearRequest) {
        try {
            AcademicYearResponse academicYearResponse = semesterService.updateAcademicYear(academicYearId, academicYearRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật năm học thành công", academicYearResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa năm học
     */
    @DeleteMapping("/academic-years/{academicYearId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAcademicYear(@PathVariable Long academicYearId) {
        try {
            semesterService.deleteAcademicYear(academicYearId);
            return ResponseEntity.ok(ApiResponse.success("Xóa năm học thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin năm học theo ID
     */
    @GetMapping("/academic-years/{academicYearId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getAcademicYearById(@PathVariable Long academicYearId) {
        try {
            AcademicYearResponse academicYearResponse = semesterService.getAcademicYearById(academicYearId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin năm học thành công", academicYearResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách năm học với phân trang
     */
    @GetMapping("/academic-years")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AcademicYearListResponse>> getAllAcademicYears(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            AcademicYearListResponse academicYearListResponse = semesterService.getAllAcademicYears(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách năm học thành công", academicYearListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy năm học hiện tại
     */
    @GetMapping("/academic-years/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getCurrentAcademicYear() {
        try {
            AcademicYearResponse academicYearResponse = semesterService.getCurrentAcademicYear();
            return ResponseEntity.ok(ApiResponse.success("Lấy năm học hiện tại thành công", academicYearResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Semester Management Endpoints
    
    /**
     * Tạo học kỳ mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SemesterResponse>> createSemester(@Valid @RequestBody SemesterRequest semesterRequest) {
        try {
            SemesterResponse semesterResponse = semesterService.createSemester(semesterRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo học kỳ thành công", semesterResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin học kỳ
     */
    @PutMapping("/{semesterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SemesterResponse>> updateSemester(
            @PathVariable Long semesterId,
            @Valid @RequestBody SemesterRequest semesterRequest) {
        try {
            SemesterResponse semesterResponse = semesterService.updateSemester(semesterId, semesterRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật học kỳ thành công", semesterResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa học kỳ
     */
    @DeleteMapping("/{semesterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSemester(@PathVariable Long semesterId) {
        try {
            semesterService.deleteSemester(semesterId);
            return ResponseEntity.ok(ApiResponse.success("Xóa học kỳ thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin học kỳ theo ID
     */
    @GetMapping("/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SemesterResponse>> getSemesterById(@PathVariable Long semesterId) {
        try {
            SemesterResponse semesterResponse = semesterService.getSemesterById(semesterId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin học kỳ thành công", semesterResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách học kỳ với phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SemesterListResponse>> getAllSemesters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            SemesterListResponse semesterListResponse = semesterService.getAllSemesters(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách học kỳ thành công", semesterListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách học kỳ theo năm học
     */
    @GetMapping("/academic-years/{academicYearId}/semesters")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getSemestersByAcademicYear(@PathVariable Long academicYearId) {
        try {
            List<SemesterResponse> semesterResponses = semesterService.getSemestersByAcademicYear(academicYearId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách học kỳ theo năm học thành công", semesterResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy học kỳ hiện tại
     */
    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SemesterResponse>> getCurrentSemester() {
        try {
            SemesterResponse semesterResponse = semesterService.getCurrentSemester();
            return ResponseEntity.ok(ApiResponse.success("Lấy học kỳ hiện tại thành công", semesterResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách học kỳ đang mở đăng ký
     */
    @GetMapping("/open")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getOpenSemesters() {
        try {
            List<SemesterResponse> semesterResponses = semesterService.getOpenSemesters();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách học kỳ đang mở đăng ký thành công", semesterResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // Registration Management Endpoints
    
    /**
     * Đóng/mở đăng ký học phần cho học kỳ
     */
    @PutMapping("/{semesterId}/registration-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeRegistrationStatus(
            @PathVariable Long semesterId,
            @Valid @RequestBody RegistrationStatusRequest registrationStatusRequest) {
        try {
            semesterService.changeRegistrationStatus(semesterId, registrationStatusRequest);
            String message = registrationStatusRequest.getIsOpen() ? 
                "Mở đăng ký học phần thành công" : "Đóng đăng ký học phần thành công";
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Đóng tất cả đăng ký học phần
     */
    @PutMapping("/close-all-registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> closeAllRegistrations() {
        try {
            semesterService.closeAllRegistrations();
            return ResponseEntity.ok(ApiResponse.success("Đóng tất cả đăng ký học phần thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Mở tất cả đăng ký học phần
     */
    @PutMapping("/open-all-registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> openAllRegistrations() {
        try {
            semesterService.openAllRegistrations();
            return ResponseEntity.ok(ApiResponse.success("Mở tất cả đăng ký học phần thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
