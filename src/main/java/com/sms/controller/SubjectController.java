package com.sms.controller;

import com.sms.dto.request.SubjectRequest;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.SubjectListResponse;
import com.sms.dto.response.SubjectResponse;
import com.sms.service.SubjectService;
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
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {
    
    @Autowired
    private SubjectService subjectService;
    
    /**
     * Tạo môn học mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest subjectRequest) {
        try {
            SubjectResponse subjectResponse = subjectService.createSubject(subjectRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo môn học thành công", subjectResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin môn học
     */
    @PutMapping("/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable Long subjectId,
            @Valid @RequestBody SubjectRequest subjectRequest) {
        try {
            SubjectResponse subjectResponse = subjectService.updateSubject(subjectId, subjectRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật môn học thành công", subjectResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa môn học
     */
    @DeleteMapping("/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable Long subjectId) {
        try {
            subjectService.deleteSubject(subjectId);
            return ResponseEntity.ok(ApiResponse.success("Xóa môn học thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin môn học theo ID
     */
    @GetMapping("/{subjectId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectById(@PathVariable Long subjectId) {
        try {
            SubjectResponse subjectResponse = subjectService.getSubjectById(subjectId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin môn học thành công", subjectResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin môn học theo mã môn học
     */
    @GetMapping("/code/{subjectCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubjectByCode(@PathVariable String subjectCode) {
        try {
            SubjectResponse subjectResponse = subjectService.getSubjectByCode(subjectCode);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin môn học thành công", subjectResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách môn học với phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SubjectListResponse>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            SubjectListResponse subjectListResponse = subjectService.getAllSubjects(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách môn học thành công", subjectListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Tìm kiếm môn học theo tên
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> searchSubjectsByName(@RequestParam String subjectName) {
        try {
            List<SubjectResponse> subjects = subjectService.searchSubjectsByName(subjectName);
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm môn học thành công", subjects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách môn học theo số tín chỉ
     */
    @GetMapping("/credit/{credit}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getSubjectsByCredit(@PathVariable Integer credit) {
        try {
            List<SubjectResponse> subjects = subjectService.getSubjectsByCredit(credit);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách môn học theo tín chỉ thành công", subjects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách môn học theo khoảng tín chỉ
     */
    @GetMapping("/credit-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getSubjectsByCreditRange(
            @RequestParam Integer minCredit,
            @RequestParam Integer maxCredit) {
        try {
            List<SubjectResponse> subjects = subjectService.getSubjectsByCreditRange(minCredit, maxCredit);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách môn học theo khoảng tín chỉ thành công", subjects));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
