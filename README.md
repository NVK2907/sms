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

## Cấu trúc Database

Hệ thống sử dụng 20 bảng chính:

### 1. Bảng Quản lý Người dùng và Phân quyền
- **users**: Thông tin người dùng (username, password, email, fullName, phone, isActive, isEmailVerified)
- **roles**: Vai trò trong hệ thống (roleName, description)
- **permissions**: Quyền hạn (permissionCode, permissionName, description)
- **user_roles**: Gán vai trò cho người dùng (userId, roleId, assignedAt)
- **role_permissions**: Gán quyền cho vai trò (roleId, permissionId, assignedBy, isActive, assignedAt)

### 2. Bảng Quản lý Sinh viên và Giáo viên
- **students**: Thông tin sinh viên (userId, studentCode, gender, dob, address, className, major, courseYear)
- **teachers**: Thông tin giáo viên (userId, teacherCode, department, title, educationLevel, experienceYears, address, hireDate)

### 3. Bảng Quản lý Học tập
- **subjects**: Môn học (subjectCode, subjectName, credit, description, status)
- **academic_years**: Năm học (name, startDate, endDate)
- **semesters**: Học kỳ (academicYearId, name, isOpen, startDate, endDate)
- **classes**: Lớp học (classCode, subjectId, semesterId, teacherId, maxStudent, status)
- **class_students**: Sinh viên đăng ký lớp (classId, studentId, registeredAt)
- **teacher_subjects**: Giáo viên dạy môn học (teacherId, subjectId, assignedAt)

### 4. Bảng Quản lý Lịch học và Thi cử
- **schedules**: Lịch học (classId, dayOfWeek, startTime, endTime, room)
- **exams**: Lịch thi (classId, examDate, examTime, room)

### 5. Bảng Quản lý Điểm số và Điểm danh
- **grades**: Điểm số (classId, studentId, midterm, final, other, total)
- **attendance**: Điểm danh (classId, studentId, attendanceDate, status, recordedAt)

### 6. Bảng Quản lý Bài tập và Tài liệu
- **assignments**: Bài tập (classId, teacherId, title, description, deadline)
- **submissions**: Bài nộp của sinh viên (assignmentId, studentId, filePath, submittedAt, score)
- **materials**: Tài liệu học tập (classId, teacherId, title, filePath, uploadedAt)

## Entity Mapping

Danh sách mapping giữa **bảng database** và **entity class** trong BE:

- **users** → `User`
- **roles** → `Role`
- **permissions** → `Permission`
- **user_roles** → `UserRole`
- **role_permissions** → `RolePermission`
- **students** → `Student`
- **teachers** → `Teacher`
- **subjects** → `Subject`
- **academic_years** → `AcademicYear`
- **semesters** → `Semester`
- **classes** → `Course`
- **class_students** → `ClassStudent`
- **teacher_subjects** → `TeacherSubject`
- **schedules** → `Schedule`
- **exams** → `Exam`
- **grades** → `Grade`
- **attendance** → `Attendance`
- **assignments** → `Assignment`
- **submissions** → `Submission`
- **materials** → `Material`

## API Endpoints

### 1. Authentication API (`/api/auth`)
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/refresh` - Làm mới token
- `POST /api/auth/logout` - Đăng xuất
- `GET /api/auth/validate` - Kiểm tra token
- `GET /api/auth/me` - Lấy thông tin user hiện tại

### 2. User Management API (`/api/users`)
- `POST /api/users` - Tạo người dùng mới (ADMIN)
- `PUT /api/users/{userId}` - Cập nhật người dùng (ADMIN)
- `DELETE /api/users/{userId}` - Xóa người dùng (ADMIN)
- `GET /api/users/{userId}` - Lấy thông tin người dùng (ADMIN, TEACHER)
- `GET /api/users` - Lấy danh sách người dùng với phân trang (ADMIN)
- `GET /api/users/search` - Tìm kiếm người dùng (ADMIN)
- `PUT /api/users/{userId}/reset-password` - Đặt lại mật khẩu (ADMIN)
- `PUT /api/users/{userId}/status` - Thay đổi trạng thái người dùng (ADMIN)
- `POST /api/users/assign-roles` - Gán vai trò cho người dùng (ADMIN)
- `DELETE /api/users/{userId}/roles` - Thu hồi vai trò (ADMIN)
- `GET /api/users/{userId}/roles` - Lấy danh sách vai trò của người dùng (ADMIN, TEACHER)

### 3. Student Management API (`/api/students`)
- `POST /api/students` - Tạo sinh viên mới (ADMIN)
- `PUT /api/students/{studentId}` - Cập nhật sinh viên (ADMIN)
- `DELETE /api/students/{studentId}` - Xóa sinh viên (ADMIN)
- `GET /api/students/{studentId}` - Lấy thông tin sinh viên theo ID (ADMIN, TEACHER)
- `GET /api/students/code/{studentCode}` - Lấy thông tin sinh viên theo mã (ADMIN, TEACHER)
- `GET /api/students` - Lấy danh sách sinh viên với phân trang (ADMIN, TEACHER)
- `POST /api/students/search` - Tìm kiếm và lọc sinh viên (ADMIN, TEACHER)
- `GET /api/students/class/{className}` - Lấy sinh viên theo lớp (ADMIN, TEACHER)
- `GET /api/students/major/{major}` - Lấy sinh viên theo chuyên ngành (ADMIN, TEACHER)
- `GET /api/students/course/{courseYear}` - Lấy sinh viên theo khóa học (ADMIN, TEACHER)
- `POST /api/students/assign-classes` - Gán sinh viên vào lớp học (ADMIN)
- `DELETE /api/students/{studentId}/classes` - Hủy gán sinh viên khỏi lớp (ADMIN)
- `GET /api/students/{studentId}/classes` - Lấy danh sách lớp học của sinh viên (ADMIN, TEACHER)
- `POST /api/students/export/excel` - Xuất danh sách sinh viên ra Excel (ADMIN, TEACHER)
- `POST /api/students/export/pdf` - Xuất danh sách sinh viên ra PDF (ADMIN, TEACHER)

### 4. Teacher Management API (`/api/teachers`)
- `POST /api/teachers` - Tạo giáo viên mới (ADMIN)
- `PUT /api/teachers/{teacherId}` - Cập nhật giáo viên (ADMIN)
- `DELETE /api/teachers/{teacherId}` - Xóa giáo viên (ADMIN)
- `GET /api/teachers/{teacherId}` - Lấy thông tin giáo viên theo ID (ADMIN, TEACHER)
- `GET /api/teachers/code/{teacherCode}` - Lấy thông tin giáo viên theo mã (ADMIN, TEACHER)
- `GET /api/teachers` - Lấy danh sách giáo viên với phân trang (ADMIN, TEACHER)
- `GET /api/teachers/search` - Tìm kiếm giáo viên (ADMIN)
- `GET /api/teachers/department/{department}` - Lấy giáo viên theo khoa/bộ môn (ADMIN, TEACHER)
- `GET /api/teachers/title/{title}` - Lấy giáo viên theo chức danh (ADMIN, TEACHER)
- `POST /api/teachers/{teacherId}/subjects` - Gán môn học cho giáo viên (ADMIN)
- `DELETE /api/teachers/{teacherId}/subjects` - Hủy gán môn học (ADMIN)
- `GET /api/teachers/{teacherId}/subjects` - Lấy danh sách môn học của giáo viên (ADMIN, TEACHER)

### 5. Subject Management API (`/api/subjects`)
- `POST /api/subjects` - Tạo môn học mới (ADMIN)
- `PUT /api/subjects/{subjectId}` - Cập nhật môn học (ADMIN)
- `DELETE /api/subjects/{subjectId}` - Xóa môn học (ADMIN)
- `GET /api/subjects/{subjectId}` - Lấy thông tin môn học theo ID (ADMIN, TEACHER)
- `GET /api/subjects/code/{subjectCode}` - Lấy thông tin môn học theo mã (ADMIN, TEACHER)
- `GET /api/subjects/search` - Lấy danh sách môn học với tìm kiếm (ADMIN, TEACHER)
- `GET /api/subjects/credit/{credit}` - Lấy môn học theo số tín chỉ (ADMIN, TEACHER)
- `GET /api/subjects/credit-range` - Lấy môn học theo khoảng tín chỉ (ADMIN, TEACHER)

### 6. Class Management API (`/api/classes`)
- `POST /api/classes` - Tạo lớp học mới (ADMIN)
- `PUT /api/classes/{classId}` - Cập nhật lớp học (ADMIN)
- `DELETE /api/classes/{classId}` - Xóa lớp học (ADMIN)
- `GET /api/classes/{classId}` - Lấy thông tin lớp học theo ID (ADMIN, TEACHER)
- `GET /api/classes/code/{classCode}` - Lấy thông tin lớp học theo mã (ADMIN, TEACHER)
- `GET /api/classes/search` - Tìm kiếm lớp học (ADMIN, TEACHER)
- `GET /api/classes/subject/{subjectId}` - Lấy lớp học theo môn học (ADMIN, TEACHER)
- `GET /api/classes/semester/{semesterId}` - Lấy lớp học theo học kỳ (ADMIN, TEACHER)
- `GET /api/classes/teacher/{teacherId}` - Lấy lớp học theo giáo viên (ADMIN, TEACHER)
- `GET /api/classes/subject/{subjectId}/semester/{semesterId}` - Lấy lớp học theo môn và học kỳ (ADMIN, TEACHER)
- `GET /api/classes/teacher/{teacherId}/semester/{semesterId}` - Lấy lớp học theo giáo viên và học kỳ (ADMIN, TEACHER)
- `PUT /api/classes/{classId}/assign-teacher/{teacherId}` - Phân công giáo viên (ADMIN)
- `PUT /api/classes/{classId}/remove-teacher` - Hủy phân công giáo viên (ADMIN)
- `GET /api/classes/{classId}/students` - Lấy danh sách sinh viên trong lớp (ADMIN, TEACHER)
- `POST /api/classes/{classId}/import-students` - Import sinh viên từ Excel (ADMIN)

### 7. Semester & Academic Year API (`/api/semesters`)
- **Academic Year:**
  - `POST /api/semesters/academic-years` - Tạo năm học mới (ADMIN)
  - `PUT /api/semesters/academic-years/{academicYearId}` - Cập nhật năm học (ADMIN)
  - `DELETE /api/semesters/academic-years/{academicYearId}` - Xóa năm học (ADMIN)
  - `GET /api/semesters/academic-years/{academicYearId}` - Lấy thông tin năm học (ADMIN, TEACHER)
  - `GET /api/semesters/academic-years` - Lấy danh sách năm học (ADMIN, TEACHER)
  - `GET /api/semesters/academic-years/current` - Lấy năm học hiện tại (ADMIN, TEACHER, STUDENT)
- **Semester:**
  - `POST /api/semesters` - Tạo học kỳ mới (ADMIN)
  - `PUT /api/semesters/{semesterId}` - Cập nhật học kỳ (ADMIN)
  - `DELETE /api/semesters/{semesterId}` - Xóa học kỳ (ADMIN)
  - `GET /api/semesters/{semesterId}` - Lấy thông tin học kỳ (ADMIN, TEACHER)
  - `GET /api/semesters` - Lấy danh sách học kỳ (ADMIN, TEACHER)
  - `GET /api/semesters/academic-years/{academicYearId}/semesters` - Lấy học kỳ theo năm học (ADMIN, TEACHER)
  - `GET /api/semesters/current` - Lấy học kỳ hiện tại (ADMIN, TEACHER, STUDENT)
  - `GET /api/semesters/open` - Lấy học kỳ đang mở đăng ký (ADMIN, TEACHER, STUDENT)
  - `PUT /api/semesters/{semesterId}/registration-status` - Đóng/mở đăng ký học phần (ADMIN)
  - `PUT /api/semesters/close-all-registrations` - Đóng tất cả đăng ký (ADMIN)
  - `PUT /api/semesters/open-all-registrations` - Mở tất cả đăng ký (ADMIN)

### 8. Student Features API (`/api/student`)
- **Thông tin cá nhân:**
  - `GET /api/student/profile/{studentId}` - Lấy thông tin cá nhân
  - `PUT /api/student/profile/{studentId}` - Cập nhật thông tin cá nhân
  - `POST /api/student/change-password/{studentId}` - Đổi mật khẩu
- **Học tập:**
  - `GET /api/student/{studentId}/classes/available` - Lấy lớp học có thể đăng ký
  - `GET /api/student/{studentId}/classes/registered` - Lấy lớp đã đăng ký
  - `GET /api/student/{studentId}/classes/registered/semester/{semesterId}` - Lấy lớp đã đăng ký theo học kỳ
  - `GET /api/student/{studentId}/classes/{classId}` - Lấy chi tiết lớp học
  - `POST /api/student/register-class` - Đăng ký lớp học
  - `DELETE /api/student/{studentId}/unregister-class/{classId}` - Hủy đăng ký lớp
- **Lịch học & Lịch thi:**
  - `GET /api/student/{studentId}/schedule/weekly` - Lấy lịch học theo tuần
  - `GET /api/student/{studentId}/schedule/daily` - Lấy lịch học theo ngày
  - `GET /api/student/{studentId}/schedule/exams/semester/{semesterId}` - Lấy lịch thi theo học kỳ
- **Điểm số:**
  - `GET /api/student/{studentId}/grades` - Lấy tất cả điểm
  - `GET /api/student/{studentId}/grades/semester/{semesterId}` - Lấy điểm theo học kỳ
  - `GET /api/student/{studentId}/grades/class/{classId}` - Lấy điểm theo lớp
  - `GET /api/student/{studentId}/gpa` - Lấy tổng kết GPA
- **Tài liệu:**
  - `GET /api/student/{studentId}/materials` - Lấy tất cả tài liệu
  - `GET /api/student/{studentId}/materials/class/{classId}` - Lấy tài liệu theo lớp
- **Bài tập:**
  - `GET /api/student/{studentId}/assignments` - Lấy tất cả bài tập
  - `GET /api/student/{studentId}/assignments/class/{classId}` - Lấy bài tập theo lớp
  - `GET /api/student/{studentId}/assignments/{assignmentId}` - Lấy chi tiết bài tập
  - `POST /api/student/submit-assignment` - Nộp bài tập
  - `PUT /api/student/{studentId}/update-submission/{submissionId}` - Cập nhật bài nộp
- **Điểm danh:**
  - `GET /api/student/{studentId}/attendance` - Lấy lịch sử điểm danh

### 9. Teacher Features API (`/api/teacher`)
- **Quản lý lớp và sinh viên:**
  - `GET /api/teacher/classes/{teacherId}` - Lấy lớp học của giáo viên
  - `GET /api/teacher/classes/{teacherId}/semester/{semesterId}` - Lấy lớp học theo học kỳ
  - `GET /api/teacher/classes/{classId}/students` - Lấy sinh viên trong lớp
- **Quản lý điểm:**
  - `POST /api/teacher/grades` - Tạo/cập nhật điểm
  - `GET /api/teacher/classes/{classId}/grades` - Lấy điểm theo lớp
  - `GET /api/teacher/students/{studentId}/grades` - Lấy điểm theo sinh viên
  - `GET /api/teacher/classes/{classId}/grades/export` - Xuất điểm ra file
  - `DELETE /api/teacher/grades/{gradeId}` - Xóa điểm
- **Quản lý bài tập:**
  - `POST /api/teacher/assignments` - Tạo bài tập
  - `PUT /api/teacher/assignments/{assignmentId}` - Cập nhật bài tập
  - `DELETE /api/teacher/assignments/{assignmentId}` - Xóa bài tập
  - `GET /api/teacher/classes/{classId}/assignments` - Lấy bài tập theo lớp
  - `GET /api/teacher/teachers/{teacherId}/assignments` - Lấy bài tập theo giáo viên
  - `GET /api/teacher/assignments/{assignmentId}` - Lấy chi tiết bài tập
- **Quản lý tài liệu:**
  - `POST /api/teacher/materials` - Upload tài liệu
  - `PUT /api/teacher/materials/{materialId}` - Cập nhật tài liệu
  - `DELETE /api/teacher/materials/{materialId}` - Xóa tài liệu
  - `GET /api/teacher/classes/{classId}/materials` - Lấy tài liệu theo lớp
  - `GET /api/teacher/teachers/{teacherId}/materials` - Lấy tài liệu theo giáo viên
- **Quản lý điểm danh:**
  - `POST /api/teacher/attendance` - Ghi điểm danh
  - `PUT /api/teacher/attendance/{attendanceId}` - Cập nhật điểm danh
  - `DELETE /api/teacher/attendance/{attendanceId}` - Xóa điểm danh
  - `GET /api/teacher/classes/{classId}/attendance` - Lấy điểm danh theo lớp
  - `GET /api/teacher/classes/{classId}/attendance/{date}` - Lấy điểm danh theo ngày
  - `GET /api/teacher/classes/{classId}/attendance/report` - Tạo báo cáo điểm danh
- **Quản lý bài nộp:**
  - `GET /api/teacher/assignments/{assignmentId}/submissions` - Lấy bài nộp theo bài tập
  - `GET /api/teacher/assignments/{assignmentId}/submissions/ungraded` - Lấy bài nộp chưa chấm
  - `POST /api/teacher/submissions/grade` - Chấm điểm bài nộp
  - `GET /api/teacher/submissions/{submissionId}` - Lấy chi tiết bài nộp
- **Quản lý lịch học:**
  - `GET /api/teacher/schedule/{teacherId}/weekly` - Lấy lịch học theo tuần
  - `GET /api/teacher/schedule/{teacherId}/daily` - Lấy lịch học theo ngày
  - `GET /api/teacher/schedule/{teacherId}/exams/semester/{semesterId}` - Lấy lịch thi theo học kỳ

### 10. Token Info API (`/api/token`)
- `GET /api/token/info` - Lấy thông tin từ JWT token
- `GET /api/token/auth-info` - Lấy thông tin authentication hiện tại
- `GET /api/token/test-access` - Test endpoint kiểm tra quyền truy cập

## Quy tắc phát triển

- Service classes phải là interface
- Tất cả implementation của service phải nằm trong ServiceImpl classes với annotation @Service
- Tất cả dependencies trong ServiceImpl phải được @Autowired
- ServiceImpl methods phải trả về DTOs, không phải entity classes
- Sử dụng repository methods với .orElseThrow() để kiểm tra sự tồn tại của record
- Sử dụng @Transactional cho các thao tác database tuần tự
- Tuân thủ SOLID, DRY, KISS, YAGNI principles
- Tuân thủ OWASP best practices
