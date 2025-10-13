package com.sms.service;

import com.sms.dto.request.LoginRequest;
import com.sms.dto.request.RefreshTokenRequest;
import com.sms.dto.response.LoginResponse;
import com.sms.dto.response.TokenResponse;

public interface AuthService {
    
    /**
     * Đăng nhập người dùng
     * @param loginRequest thông tin đăng nhập
     * @return thông tin đăng nhập thành công
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Làm mới access token
     * @param refreshTokenRequest refresh token
     * @return token mới
     */
    TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    
    /**
     * Đăng xuất người dùng
     * @param token access token
     */
    void logout(String token);
    
    /**
     * Xác thực token
     * @param token JWT token
     * @return true nếu token hợp lệ
     */
    boolean validateToken(String token);
}
