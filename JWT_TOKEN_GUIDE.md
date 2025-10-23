# Hướng dẫn JWT Token với Thông tin Đầy đủ

## 🎯 **Cải tiến đã thực hiện:**

### **1. JWT Token mới có thông tin đầy đủ:**
```json
{
  "userId": 41,
  "role": "Admin",
  "username": "admin",
  "email": "admin@example.com",
  "fullName": "Administrator",
  "tokenType": "access",
  "sub": "admin",
  "iat": 1761202775,
  "exp": 1761289175
}
```

### **2. Các thông tin được thêm vào token:**
- ✅ `username`: Tên đăng nhập
- ✅ `email`: Email của user
- ✅ `fullName`: Tên đầy đủ
- ✅ `tokenType`: Loại token (access/refresh)

## 🧪 **Cách test JWT token mới:**

### **1. Đăng nhập để lấy token:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### **2. Kiểm tra thông tin token:**
```bash
curl -X GET "http://localhost:8080/api/token/info" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Kết quả mong đợi:**
```json
{
  "success": true,
  "message": "Thông tin token",
  "data": {
    "username": "admin",
    "userId": 41,
    "role": "Admin",
    "email": "admin@example.com",
    "fullName": "Administrator",
    "tokenType": "access",
    "expiration": "2025-10-24T14:00:00.000+00:00",
    "isExpired": false
  }
}
```

### **3. Kiểm tra thông tin authentication:**
```bash
curl -X GET "http://localhost:8080/api/token/auth-info" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### **4. Test endpoint classes với token mới:**
```bash
curl -X GET "http://localhost:8080/api/classes?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔍 **Logging được cải thiện:**

Với logging mới, bạn sẽ thấy:
```
DEBUG - JWT token valid for user: admin with role: Admin, email: admin@example.com, fullName: Administrator
DEBUG - Authentication set for user: admin with role: Admin
```

## 📋 **Các method mới trong JwtUtil:**

- `generateAccessTokenWithUserInfo()`: Tạo token với thông tin đầy đủ
- `getEmailFromToken()`: Lấy email từ token
- `getFullNameFromToken()`: Lấy tên đầy đủ từ token
- `getTokenTypeFromToken()`: Lấy loại token

## 🚀 **Lợi ích:**

1. **Thông tin đầy đủ**: Token chứa tất cả thông tin cần thiết
2. **Giảm database calls**: Không cần query database để lấy thông tin user
3. **Performance tốt hơn**: Xử lý nhanh hơn với thông tin có sẵn
4. **Debug dễ dàng**: Logging chi tiết để debug
5. **Bảo mật tốt hơn**: Token có đầy đủ thông tin để xác thực

## 🎯 **Kết quả:**

Sau khi cập nhật, JWT token sẽ có thông tin đầy đủ và endpoint `/api/classes` sẽ hoạt động bình thường với token hợp lệ!
