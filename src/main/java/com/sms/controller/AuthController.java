package com.sms.controller;

import com.sms.dto.request.LoginRequest;
import com.sms.dto.request.RefreshTokenRequest;
import com.sms.dto.response.ApiResponse;
import com.sms.dto.response.LoginResponse;
import com.sms.dto.response.TokenResponse;
import com.sms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Nhận request đăng nhập từ user: {}", loginRequest.getUsername());
            
            LoginResponse response = authService.login(loginRequest);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Làm mới token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            log.info("Nhận request làm mới token");
            
            TokenResponse response = authService.refreshToken(refreshTokenRequest);
            
            return ResponseEntity.ok(ApiResponse.success("Làm mới token thành công", response));
        } catch (Exception e) {
            log.error("Lỗi khi làm mới token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("Nhận request đăng xuất");
            
            String token = authorization.replace("Bearer ", "");
            authService.logout(token);
            
            return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đăng xuất"));
        }
    }
    
    /**
     * Kiểm tra token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("Nhận request kiểm tra token");
            
            String token = authorization.replace("Bearer ", "");
            boolean isValid = authService.validateToken(token);
            
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra token thành công", isValid));
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token không hợp lệ"));
        }
    }
    
    /**
     * Lấy thông tin user hiện tại
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            log.info("Nhận request lấy thông tin user hiện tại");
            
            String token = authorization.replace("Bearer ", "");
            
            if (!authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token không hợp lệ"));
            }
            
            // TODO: Implement get current user info
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin user thành công", null));
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin user"));
        }
    }
}
