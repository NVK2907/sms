package com.sms.controller;

import com.sms.dto.response.ApiResponse;
import com.sms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")
public class TokenInfoController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Lấy thông tin từ JWT token
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTokenInfo(
            @RequestHeader("Authorization") String authHeader) {
        
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("username", jwtUtil.getUsernameFromToken(token));
            tokenInfo.put("userId", jwtUtil.getUserIdFromToken(token));
            tokenInfo.put("role", jwtUtil.getRoleFromToken(token));
            tokenInfo.put("email", jwtUtil.getEmailFromToken(token));
            tokenInfo.put("fullName", jwtUtil.getFullNameFromToken(token));
            tokenInfo.put("tokenType", jwtUtil.getTokenTypeFromToken(token));
            tokenInfo.put("expiration", jwtUtil.getExpirationDateFromToken(token));
            tokenInfo.put("isExpired", jwtUtil.isTokenExpired(token));
            
            return ResponseEntity.ok(ApiResponse.success("Thông tin token", tokenInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Lỗi khi lấy thông tin token: " + e.getMessage()));
        }
    }
    
    /**
     * Lấy thông tin authentication hiện tại
     */
    @GetMapping("/auth-info")
    public ResponseEntity<ApiResponse<Object>> getAuthInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            Map<String, Object> authInfo = new HashMap<>();
            authInfo.put("name", auth.getName());
            authInfo.put("authorities", auth.getAuthorities());
            authInfo.put("principal", auth.getPrincipal());
            authInfo.put("authenticated", auth.isAuthenticated());
            
            return ResponseEntity.ok(ApiResponse.success("Thông tin authentication", authInfo));
        } else {
            return ResponseEntity.ok(ApiResponse.error("Không có authentication"));
        }
    }
    
    /**
     * Test endpoint để kiểm tra quyền truy cập
     */
    @GetMapping("/test-access")
    public ResponseEntity<ApiResponse<String>> testAccess() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String authorities = auth.getAuthorities().toString();
            return ResponseEntity.ok(ApiResponse.success("Bạn có quyền truy cập", authorities));
        } else {
            return ResponseEntity.ok(ApiResponse.error("Không có quyền truy cập"));
        }
    }
}
