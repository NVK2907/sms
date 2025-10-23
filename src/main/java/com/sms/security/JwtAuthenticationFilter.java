package com.sms.security;

import com.sms.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.getUsernameFromToken(jwt);
                String role = jwtUtil.getRoleFromToken(jwt);
                String email = jwtUtil.getEmailFromToken(jwt);
                String fullName = jwtUtil.getFullNameFromToken(jwt);
                
                log.debug("JWT token valid for user: {} with role: {}, email: {}, fullName: {}", 
                         username, role, email, fullName);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (userDetails != null) {
                    // Chuyển đổi role thành chữ hoa để phù hợp với Spring Security
                    String normalizedRole = role.toUpperCase();
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication set for user: {} with role: {} (normalized: {})", username, role, normalizedRole);
                } else {
                    log.warn("UserDetails not found for username: {}", username);
                }
            } else {
                log.warn("JWT token validation failed or no token found");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
