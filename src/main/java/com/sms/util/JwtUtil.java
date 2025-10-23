package com.sms.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateAccessToken(String username, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("username", username);
        claims.put("tokenType", "access");
        return createToken(claims, username, expiration);
    }
    
    public String generateAccessTokenWithUserInfo(String username, Long userId, String role, String email, String fullName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("username", username);
        claims.put("email", email);
        claims.put("fullName", fullName);
        claims.put("tokenType", "access");
        return createToken(claims, username, expiration);
    }
    
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userId", Long.class));
    }
    
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("email", String.class));
    }
    
    public String getFullNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("fullName", String.class));
    }
    
    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("tokenType", String.class));
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token đã hết hạn: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token không được hỗ trợ: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("JWT token không hợp lệ: {}", e.getMessage());
            throw e;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature không hợp lệ: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string trống: {}", e.getMessage());
            throw e;
        }
    }
    
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
            return false;
        }
    }
    
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
            return false;
        }
    }
    
    public Boolean isRefreshToken(String token) {
        try {
            final String type = getClaimFromToken(token, claims -> claims.get("type", String.class));
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
    
    public Long getExpirationTime() {
        return expiration;
    }
}
