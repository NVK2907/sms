package com.sms.service.impl;

import com.sms.dto.request.*;
import com.sms.dto.response.UserListResponse;
import com.sms.dto.response.UserResponse;
import com.sms.entity.Role;
import com.sms.entity.User;
import com.sms.entity.UserRole;
import com.sms.exception.*;
import com.sms.repository.RoleRepository;
import com.sms.repository.UserRepository;
import com.sms.repository.UserRoleRepository;
import com.sms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UserAlreadyExistsException("Tên đăng nhập", userRequest.getUsername());
        }
        
        // Kiểm tra email đã tồn tại (nếu có)
        if (userRequest.getEmail() != null && !userRequest.getEmail().isEmpty() 
            && userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email", userRequest.getEmail());
        }
        
        // Tạo user mới
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        user.setPhone(userRequest.getPhone());
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        
        User savedUser = userRepository.save(user);
        
        // Gán roles nếu có
        if (userRequest.getRoleIds() != null && !userRequest.getRoleIds().isEmpty()) {
            assignRolesToUser(savedUser.getId(), userRequest.getRoleIds());
        }
        
        return convertToUserResponse(savedUser);
    }
    
    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Kiểm tra email đã tồn tại (nếu có thay đổi)
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() 
            && !userUpdateRequest.getEmail().equals(user.getEmail())
            && userRepository.existsByEmail(userUpdateRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email", userUpdateRequest.getEmail());
        }
        
        // Cập nhật thông tin
        if (userUpdateRequest.getEmail() != null) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (userUpdateRequest.getFullName() != null) {
            user.setFullName(userUpdateRequest.getFullName());
        }
        if (userUpdateRequest.getPhone() != null) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        if (userUpdateRequest.getIsActive() != null) {
            user.setIsActive(userUpdateRequest.getIsActive());
        }
        
        User updatedUser = userRepository.save(user);
        
        // Cập nhật roles nếu có
        if (userUpdateRequest.getRoleIds() != null) {
            // Xóa tất cả roles cũ
            userRoleRepository.deleteByUserId(userId);
            // Gán roles mới
            if (!userUpdateRequest.getRoleIds().isEmpty()) {
                assignRolesToUser(userId, userUpdateRequest.getRoleIds());
            }
        }
        
        return convertToUserResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        // Xóa tất cả user roles trước
        userRoleRepository.deleteByUserId(userId);
        
        // Xóa user
        userRepository.delete(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return convertToUserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserListResponse getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        
        List<UserResponse> userResponses = userPage.getContent().stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
        
        return new UserListResponse(
            userResponses,
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.getNumber(),
            userPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserListResponse searchUsers(String keyword, Pageable pageable) {
        Page<User> userPage = userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            keyword, keyword, pageable);
        
        List<UserResponse> userResponses = userPage.getContent().stream()
            .map(this::convertToUserResponse)
            .collect(Collectors.toList());
        
        return new UserListResponse(
            userResponses,
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.getNumber(),
            userPage.getSize()
        );
    }
    
    @Override
    public void resetPassword(Long userId, PasswordResetRequest passwordResetRequest) {
        // Kiểm tra mật khẩu xác nhận
        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);
    }
    
    @Override
    public void changeUserStatus(Long userId, UserStatusRequest userStatusRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        user.setIsActive(userStatusRequest.getIsActive());
        userRepository.save(user);
    }
    
    @Override
    public void assignRoles(RoleAssignmentRequest roleAssignmentRequest) {
        userRepository.findById(roleAssignmentRequest.getUserId())
            .orElseThrow(() -> new UserNotFoundException(roleAssignmentRequest.getUserId()));
        
        assignRolesToUser(roleAssignmentRequest.getUserId(), roleAssignmentRequest.getRoleIds());
    }
    
    @Override
    public void revokeRoles(Long userId, List<Long> roleIds) {
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        for (Long roleId : roleIds) {
            userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse.RoleInfo> getUserRoles(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        List<UserRole> userRoles = userRoleRepository.findByUserIdWithRole(userId);
        
        return userRoles.stream()
            .map(userRole -> {
                Role role = roleRepository.findById(userRole.getRoleId())
                    .orElseThrow(() -> new RoleNotFoundException(userRole.getRoleId()));
                
                return new UserResponse.RoleInfo(
                    role.getId(),
                    role.getRoleName(),
                    role.getDescription(),
                    userRole.getAssignedAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    private void assignRolesToUser(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            // Kiểm tra role tồn tại
            roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
            
            // Kiểm tra user đã có role này chưa
            if (!userRoleRepository.findByUserIdAndRoleId(userId, roleId).isPresent()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleRepository.save(userRole);
            }
        }
    }
    
    private UserResponse convertToUserResponse(User user) {
        List<UserResponse.RoleInfo> roles = getUserRoles(user.getId());
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getIsActive(),
            user.getIsEmailVerified(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            roles
        );
    }
}
