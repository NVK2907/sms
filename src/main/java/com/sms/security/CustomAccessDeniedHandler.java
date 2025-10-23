package com.sms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        log.error("Access denied error: {}", accessDeniedException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        ApiResponse<Object> apiResponse = ApiResponse.error("Không có quyền truy cập: " + accessDeniedException.getMessage());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
