package com.sms.exception;

import com.sms.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation error: {}", errors);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Dữ liệu không hợp lệ"));
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không đúng"));
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Xác thực thất bại"));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Không có quyền truy cập"));
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleRoleNotFoundException(RoleNotFoundException ex) {
        log.error("Role not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handlePasswordMismatchException(PasswordMismatchException ex) {
        log.error("Password mismatch: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(AcademicYearNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAcademicYearNotFoundException(AcademicYearNotFoundException ex) {
        log.error("Academic year not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(SemesterNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleSemesterNotFoundException(SemesterNotFoundException ex) {
        log.error("Semester not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(AcademicYearAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleAcademicYearAlreadyExistsException(AcademicYearAlreadyExistsException ex) {
        log.error("Academic year already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(SemesterAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleSemesterAlreadyExistsException(SemesterAlreadyExistsException ex) {
        log.error("Semester already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidDateRangeException(InvalidDateRangeException ex) {
        log.error("Invalid date range: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(AcademicYearHasSemestersException.class)
    public ResponseEntity<ApiResponse<String>> handleAcademicYearHasSemestersException(AcademicYearHasSemestersException ex) {
        log.error("Academic year has semesters: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleStudentNotFoundException(StudentNotFoundException ex) {
        log.error("Student not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleStudentAlreadyExistsException(StudentAlreadyExistsException ex) {
        log.error("Student already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleTeacherNotFoundException(TeacherNotFoundException ex) {
        log.error("Teacher not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(TeacherAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleTeacherAlreadyExistsException(TeacherAlreadyExistsException ex) {
        log.error("Teacher already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleSubjectNotFoundException(SubjectNotFoundException ex) {
        log.error("Subject not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(SubjectAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleSubjectAlreadyExistsException(SubjectAlreadyExistsException ex) {
        log.error("Subject already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleClassNotFoundException(ClassNotFoundException ex) {
        log.error("Class not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(ClassAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleClassAlreadyExistsException(ClassAlreadyExistsException ex) {
        log.error("Class already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(ClassFullException.class)
    public ResponseEntity<ApiResponse<String>> handleClassFullException(ClassFullException ex) {
        log.error("Class is full: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime error: {}", ex.getMessage());
        
        // Xử lý các lỗi phổ biến từ User Management
        if (ex.getMessage().contains("Không tìm thấy người dùng")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage()));
        }
        
        if (ex.getMessage().contains("đã tồn tại") || 
            ex.getMessage().contains("không khớp") ||
            ex.getMessage().contains("Không tìm thấy vai trò")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Đã xảy ra lỗi không mong muốn"));
    }
}
