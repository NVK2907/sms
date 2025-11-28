package com.sms.service.impl;

import com.sms.dto.request.LoginRequest;
import com.sms.dto.request.RefreshTokenRequest;
import com.sms.dto.response.LoginResponse;
import com.sms.dto.response.TokenResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.AuthService;
import com.sms.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Đang xử lý đăng nhập cho user: {}", loginRequest.getUsername());
        
        // Tìm user theo username
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng"));
        
        // Kiểm tra trạng thái tài khoản
        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }
        
        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
        
        // Lấy thông tin roles và permissions
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roleNames = userRoles.stream()
                .map(userRole -> roleRepository.findById(userRole.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role không tồn tại"))
                        .getRoleName())
                .collect(Collectors.toList());
        
        // Lấy permissions từ roles
        List<String> permissions = userRoles.stream()
                .flatMap(userRole -> rolePermissionRepository.findByRoleIdAndIsActiveTrue(userRole.getRoleId()).stream())
                .map(rolePermission -> permissionRepository.findById(rolePermission.getPermissionId())
                        .orElseThrow(() -> new RuntimeException("Permission không tồn tại"))
                        .getPermissionCode())
                .distinct()
                .collect(Collectors.toList());
        
        // Tạo tokens
        String primaryRole = roleNames.isEmpty() ? "USER" : roleNames.get(0);
        String accessToken = jwtUtil.generateAccessTokenWithUserInfo(
            user.getUsername(), 
            user.getId(), 
            primaryRole, 
            user.getEmail(), 
            user.getFullName()
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId());
        
        // Tạo user info
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setFullName(user.getFullName());
        userInfo.setPhone(user.getPhone());
        studentRepository.findByUserId(user.getId())
                .ifPresent(student -> userInfo.setStudentId(student.getId()));
        teacherRepository.findByUserId(user.getId())
                .ifPresent(teacher -> userInfo.setTeacherId(teacher.getId()));
        userInfo.setRoles(roleNames);
        userInfo.setPermissions(permissions);
        
        // Tạo response
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getExpirationTime());
        response.setUserInfo(userInfo);
        
        log.info("Đăng nhập thành công cho user: {}", user.getUsername());
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Đang làm mới token");
        
        String refreshToken = refreshTokenRequest.getRefreshToken();
        
        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
        
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Token không phải là refresh token");
        }
        
        // Lấy thông tin từ token
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        
        // Kiểm tra user còn tồn tại và active
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        
        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }
        
        // Lấy role chính
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        String primaryRole = "USER";
        if (!userRoles.isEmpty()) {
            Role role = roleRepository.findById(userRoles.get(0).getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
            primaryRole = role.getRoleName();
        }
        
        // Tạo access token mới
        String newAccessToken = jwtUtil.generateAccessTokenWithUserInfo(
            username, 
            userId, 
            primaryRole, 
            user.getEmail(), 
            user.getFullName()
        );
        
        TokenResponse response = new TokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken); // Giữ nguyên refresh token
        response.setExpiresIn(jwtUtil.getExpirationTime());
        
        log.info("Làm mới token thành công cho user: {}", username);
        return response;
    }
    
    @Override
    public void logout(String token) {
        log.info("Đăng xuất user");
        // Trong implementation đơn giản này, chúng ta chỉ log
        // Trong thực tế, có thể lưu token vào blacklist
        log.info("User đã đăng xuất thành công");
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("Lỗi khi validate token: {}", e.getMessage());
            return false;
        }
    }
}
