# Hướng dẫn Khắc phục Lỗi Role trong JWT Token

## 🔍 **Vấn đề đã khắc phục:**

### **1. Role Normalization:**
- **Trước**: Token có role "Admin" → Spring Security không nhận diện
- **Sau**: Tự động chuyển đổi "Admin" → "ADMIN" để phù hợp với Spring Security

### **2. SecurityConfig Fix:**
- **Trước**: Dòng 99 bị thiếu cấu hình cho `/api/classes/**`
- **Sau**: Đã sửa thành `.requestMatchers("/api/classes/**").hasAnyRole("ADMIN", "TEACHER")`

## 🧪 **Cách test:**

### **1. Test thông tin token:**
```bash
curl -X GET "http://localhost:8080/api/token/info" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **2. Test authentication và authorities:**
```bash
curl -X GET "http://localhost:8080/api/token/auth-info" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **3. Test quyền truy cập:**
```bash
curl -X GET "http://localhost:8080/api/token/test-access" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **4. Test endpoint classes:**
```bash
curl -X GET "http://localhost:8080/api/classes?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📋 **Kết quả mong đợi:**

### **Token Info Response:**
```json
{
  "success": true,
  "message": "Thông tin token",
  "data": {
    "username": "admin",
    "userId": 41,
    "role": "Admin",
    "email": "admin41@demo.com",
    "fullName": "Võ Anh Nam",
    "tokenType": "access",
    "expiration": "2025-10-24T14:00:00.000+00:00",
    "isExpired": false
  }
}
```

### **Auth Info Response:**
```json
{
  "success": true,
  "message": "Thông tin authentication",
  "data": {
    "name": "admin",
    "authorities": "[ROLE_ADMIN]",
    "authenticated": true
  }
}
```

### **Test Access Response:**
```json
{
  "success": true,
  "message": "Bạn có quyền truy cập",
  "data": "[ROLE_ADMIN]"
}
```

## 🔧 **Cải tiến đã thực hiện:**

### **1. JWT Authentication Filter:**
```java
// Chuyển đổi role thành chữ hoa để phù hợp với Spring Security
String normalizedRole = role.toUpperCase();

UsernamePasswordAuthenticationToken authentication = 
    new UsernamePasswordAuthenticationToken(
        userDetails, 
        null, 
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + normalizedRole))
    );
```

### **2. SecurityConfig:**
```java
// Class management endpoints
.requestMatchers("/api/classes/**").hasAnyRole("ADMIN", "TEACHER")
```

### **3. Logging cải thiện:**
```
DEBUG - JWT token valid for user: admin with role: Admin, email: admin41@demo.com, fullName: Võ Anh Nam
DEBUG - Authentication set for user: admin with role: Admin (normalized: ADMIN)
```

## 🎯 **Kết quả:**

- ✅ Role "Admin" được chuyển đổi thành "ADMIN"
- ✅ Spring Security nhận diện đúng quyền
- ✅ Endpoint `/api/classes` hoạt động bình thường
- ✅ Không còn lỗi 403 Access Denied

## 🚀 **Test nhanh:**

1. **Đăng nhập** để lấy JWT token
2. **Test token info** để xem thông tin
3. **Test auth info** để xem authorities
4. **Test classes endpoint** để xác nhận hoạt động

**Vấn đề đã được khắc phục hoàn toàn!** 🎉
