# SMS Management System

Hệ thống quản lý sinh viên (Student Management System) được xây dựng bằng Java Spring Boot 3 với Java 21.

## Công nghệ sử dụng

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **Thymeleaf**
- **Lombok**
- **MapStruct**
- **H2 Database** (development)
- **MySQL** (production)

## Cấu trúc project

```
src/main/java/com/sms/
├── entity/          # Các entity class
├── dto/             # Data Transfer Objects
├── repository/      # Repository interfaces
├── service/         # Service interfaces
│   └── impl/        # Service implementations
├── controller/      # REST Controllers
├── config/          # Configuration classes
└── exception/       # Custom exceptions
```

## Cách chạy project

1. Đảm bảo đã cài đặt Java 21 và Maven
2. Clone project và di chuyển vào thư mục sms
3. Chạy lệnh: `mvn spring-boot:run`
4. Truy cập ứng dụng tại: http://localhost:8080/sms
5. Truy cập H2 Console tại: http://localhost:8080/sms/h2-console

## Quy tắc phát triển

- Service classes phải là interface
- Tất cả implementation của service phải nằm trong ServiceImpl classes với annotation @Service
- Tất cả dependencies trong ServiceImpl phải được @Autowired
- ServiceImpl methods phải trả về DTOs, không phải entity classes
- Sử dụng repository methods với .orElseThrow() để kiểm tra sự tồn tại của record
- Sử dụng @Transactional cho các thao tác database tuần tự
- Tuân thủ SOLID, DRY, KISS, YAGNI principles
- Tuân thủ OWASP best practices
