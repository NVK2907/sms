package com.sms.controller;

import com.sms.dto.request.*;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.UserListResponse;
import com.sms.dto.response.UserResponse;
import com.sms.service.UserService;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Tạo người dùng mới
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = userService.createUser(userRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo người dùng thành công", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            UserResponse userResponse = userService.updateUser(userId, userUpdateRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Xóa người dùng
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin người dùng theo ID
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách người dùng với phân trang
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserListResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            UserListResponse userListResponse = userService.getAllUsers(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", userListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Tìm kiếm người dùng
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserListResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            UserListResponse userListResponse = userService.searchUsers(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm người dùng thành công", userListResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Đặt lại mật khẩu
     */
    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long userId,
            @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            userService.resetPassword(userId, passwordResetRequest);
            return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Thay đổi trạng thái người dùng (khóa/mở khóa)
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusRequest userStatusRequest) {
        try {
            userService.changeUserStatus(userId, userStatusRequest);
            String message = userStatusRequest.getIsActive() ? 
                "Mở khóa tài khoản thành công" : "Khóa tài khoản thành công";
            return ResponseEntity.ok(ApiResponse.success(message, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Gán vai trò cho người dùng
     */
    @PostMapping("/assign-roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> assignRoles(@Valid @RequestBody RoleAssignmentRequest roleAssignmentRequest) {
        try {
            userService.assignRoles(roleAssignmentRequest);
            return ResponseEntity.ok(ApiResponse.success("Gán vai trò thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Thu hồi vai trò của người dùng
     */
    @DeleteMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> revokeRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        try {
            userService.revokeRoles(userId, roleIds);
            return ResponseEntity.ok(ApiResponse.success("Thu hồi vai trò thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Lấy danh sách vai trò của người dùng
     */
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<UserResponse.RoleInfo>>> getUserRoles(@PathVariable Long userId) {
        try {
            List<UserResponse.RoleInfo> roles = userService.getUserRoles(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách vai trò thành công", roles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
