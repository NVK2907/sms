package com.sms.service;

import com.sms.dto.request.*;
import com.sms.dto.response.UserListResponse;
import com.sms.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    
    /**
     * Tạo người dùng mới
     */
    UserResponse createUser(UserRequest userRequest);
    
    /**
     * Cập nhật thông tin người dùng
     */
    UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest);
    
    /**
     * Xóa người dùng
     */
    void deleteUser(Long userId);
    
    /**
     * Lấy thông tin người dùng theo ID
     */
    UserResponse getUserById(Long userId);
    
    /**
     * Lấy danh sách người dùng với phân trang
     */
    UserListResponse getAllUsers(Pageable pageable);
    
    /**
     * Tìm kiếm người dùng theo tên đăng nhập hoặc họ tên
     */
    UserListResponse searchUsers(String keyword, Pageable pageable);
    
    /**
     * Đặt lại mật khẩu cho người dùng
     */
    void resetPassword(Long userId, PasswordResetRequest passwordResetRequest);
    
    /**
     * Thay đổi trạng thái hoạt động của người dùng (khóa/mở khóa)
     */
    void changeUserStatus(Long userId, UserStatusRequest userStatusRequest);
    
    /**
     * Gán vai trò cho người dùng
     */
    void assignRoles(RoleAssignmentRequest roleAssignmentRequest);
    
    /**
     * Thu hồi vai trò của người dùng
     */
    void revokeRoles(Long userId, List<Long> roleIds);
    
    /**
     * Lấy danh sách vai trò của người dùng
     */
    List<UserResponse.RoleInfo> getUserRoles(Long userId);
}
