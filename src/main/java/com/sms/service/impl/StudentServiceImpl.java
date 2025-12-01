package com.sms.service.impl;

import com.sms.dto.request.*;
import com.sms.dto.response.StudentListResponse;
import com.sms.dto.response.StudentResponse;
import com.sms.entity.*;
import com.sms.exception.*;
import com.sms.repository.*;
import com.sms.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Override
    public StudentResponse createStudent(StudentRequest studentRequest) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(studentRequest.getUserId())
            .orElseThrow(() -> new UserNotFoundException(studentRequest.getUserId()));
        
        // Kiểm tra mã sinh viên đã tồn tại
        if (studentRepository.existsByStudentCode(studentRequest.getStudentCode())) {
            throw new StudentAlreadyExistsException("Mã sinh viên", studentRequest.getStudentCode());
        }
        
        // Kiểm tra user đã có thông tin sinh viên chưa
        if (studentRepository.existsByUserId(studentRequest.getUserId())) {
            throw new StudentAlreadyExistsException("Người dùng đã có thông tin sinh viên");
        }
        
        Student student = new Student();
        student.setUserId(studentRequest.getUserId());
        student.setStudentCode(studentRequest.getStudentCode());
        student.setGender(studentRequest.getGender());
        student.setDob(studentRequest.getDob());
        student.setAddress(studentRequest.getAddress());
        student.setClassName(studentRequest.getClassName());
        student.setMajor(studentRequest.getMajor());
        student.setCourseYear(studentRequest.getCourseYear());
        
        Student savedStudent = studentRepository.save(student);
        return convertToStudentResponse(savedStudent);
    }
    
    @Override
    public StudentResponse updateStudent(Long studentId, StudentUpdateRequest studentUpdateRequest) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        if (studentUpdateRequest.getGender() != null) {
            student.setGender(studentUpdateRequest.getGender());
        }
        if (studentUpdateRequest.getDob() != null) {
            student.setDob(studentUpdateRequest.getDob());
        }
        if (studentUpdateRequest.getAddress() != null) {
            student.setAddress(studentUpdateRequest.getAddress());
        }
        if (studentUpdateRequest.getClassName() != null) {
            student.setClassName(studentUpdateRequest.getClassName());
        }
        if (studentUpdateRequest.getMajor() != null) {
            student.setMajor(studentUpdateRequest.getMajor());
        }
        if (studentUpdateRequest.getCourseYear() != null) {
            student.setCourseYear(studentUpdateRequest.getCourseYear());
        }
        
        Student updatedStudent = studentRepository.save(student);
        return convertToStudentResponse(updatedStudent);
    }
    
    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        // Xóa tất cả class_student records
        classStudentRepository.deleteByStudentId(studentId);
        
        // Xóa student
        studentRepository.delete(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        return convertToStudentResponse(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
            .orElseThrow(() -> new StudentNotFoundException("Không tìm thấy sinh viên với mã: " + studentCode));
        
        return convertToStudentResponse(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentListResponse getAllStudents(Pageable pageable) {
        Page<Student> studentPage = studentRepository.findAll(pageable);
        
        List<StudentResponse> studentResponses = studentPage.getContent().stream()
            .map(this::convertToStudentResponse)
            .collect(Collectors.toList());
        
        return new StudentListResponse(
            studentResponses,
            studentPage.getTotalElements(),
            studentPage.getTotalPages(),
            studentPage.getNumber(),
            studentPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentListResponse searchStudents(StudentSearchRequest searchRequest, Pageable pageable) {
        Page<Student> studentPage = studentRepository.searchStudents(
            searchRequest.getKeyword(),
            searchRequest.getClassName(),
            searchRequest.getMajor(),
            searchRequest.getCourseYear(),
            searchRequest.getGender(),
            pageable
        );
        
        List<StudentResponse> studentResponses = studentPage.getContent().stream()
            .map(this::convertToStudentResponse)
            .collect(Collectors.toList());
        
        return new StudentListResponse(
            studentResponses,
            studentPage.getTotalElements(),
            studentPage.getTotalPages(),
            studentPage.getNumber(),
            studentPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByClass(String className) {
        List<Student> students = studentRepository.findByClassName(className);
        
        return students.stream()
            .map(this::convertToStudentResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByMajor(String major) {
        List<Student> students = studentRepository.findByMajor(major);
        
        return students.stream()
            .map(this::convertToStudentResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByCourseYear(Integer courseYear) {
        List<Student> students = studentRepository.findByCourseYear(courseYear);
        
        return students.stream()
            .map(this::convertToStudentResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignStudentToClasses(ClassAssignmentRequest classAssignmentRequest) {
        Student student = studentRepository.findById(classAssignmentRequest.getStudentId())
            .orElseThrow(() -> new StudentNotFoundException(classAssignmentRequest.getStudentId()));
        
        for (Long classId : classAssignmentRequest.getClassIds()) {
            // Kiểm tra lớp tồn tại
            Course courseEntity = courseRepository.findById(classId)
                .orElseThrow(() -> new com.sms.exception.ClassNotFoundException(classId));
            
            // Kiểm tra sinh viên đã đăng ký lớp này chưa
            if (classStudentRepository.findByClassIdAndStudentId(classId, classAssignmentRequest.getStudentId()).isPresent()) {
                continue; // Bỏ qua nếu đã đăng ký
            }
            
            // Kiểm tra lớp còn chỗ không
            Long currentCount = classStudentRepository.countByClassId(classId);
            if (currentCount >= courseEntity.getMaxStudent()) {
                throw new ClassFullException(courseEntity.getClassCode());
            }
            
            ClassStudent classStudent = new ClassStudent();
            classStudent.setClassId(classId);
            classStudent.setStudentId(classAssignmentRequest.getStudentId());
            classStudentRepository.save(classStudent);
        }
    }
    
    @Override
    public void removeStudentFromClasses(Long studentId, List<Long> classIds) {
        studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        for (Long classId : classIds) {
            classStudentRepository.deleteByClassIdAndStudentId(classId, studentId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse.ClassInfo> getStudentClasses(Long studentId) {
        studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));
        
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        
        return classStudents.stream()
            .map(classStudent -> {
                Course courseEntity = courseRepository.findById(classStudent.getClassId())
                    .orElse(new Course());
                Subject subject = subjectRepository.findById(courseEntity.getSubjectId())
                    .orElse(new Subject());
                Teacher teacher = teacherRepository.findById(courseEntity.getTeacherId())
                    .orElse(new Teacher());
                
                return new StudentResponse.ClassInfo(
                    courseEntity.getId(),
                    courseEntity.getClassCode(),
                    subject.getSubjectName(),
                    teacher.getTeacherCode(), // Sử dụng teacherCode thay vì name
                    classStudent.getRegisteredAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] exportStudentsToExcel(StudentSearchRequest searchRequest) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách sinh viên");
            
            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"STT", "Mã SV", "Họ tên", "Email", "SĐT", "Giới tính", "Ngày sinh", 
                              "Địa chỉ", "Lớp", "Chuyên ngành", "Khóa học"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Lấy dữ liệu sinh viên
            List<Student> students = studentRepository.searchStudents(
                searchRequest.getKeyword(),
                searchRequest.getClassName(),
                searchRequest.getMajor(),
                searchRequest.getCourseYear(),
                searchRequest.getGender(),
                Pageable.unpaged()
            ).getContent();
            
            // Điền dữ liệu
            int rowNum = 1;
            for (Student student : students) {
                User user = userRepository.findById(student.getUserId()).orElse(new User());
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(student.getStudentCode());
                row.createCell(2).setCellValue(user.getFullName());
                row.createCell(3).setCellValue(user.getEmail());
                row.createCell(4).setCellValue(user.getPhone());
                row.createCell(5).setCellValue(student.getGender());
                row.createCell(6).setCellValue(student.getDob() != null ? student.getDob().toString() : "");
                row.createCell(7).setCellValue(student.getAddress());
                row.createCell(8).setCellValue(student.getClassName());
                row.createCell(9).setCellValue(student.getMajor());
                row.createCell(10).setCellValue(student.getCourseYear());
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất file Excel: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] exportStudentsToPDF(StudentSearchRequest searchRequest) {
        // Simplified PDF export - in real implementation, you would use iText or similar
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Danh sách sinh viên</title></head><body>");
        html.append("<h1>Danh sách sinh viên</h1>");
        html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        html.append("<tr><th>STT</th><th>Mã SV</th><th>Họ tên</th><th>Email</th><th>SĐT</th>");
        html.append("<th>Giới tính</th><th>Ngày sinh</th><th>Địa chỉ</th><th>Lớp</th><th>Chuyên ngành</th><th>Khóa học</th></tr>");
        
        List<Student> students = studentRepository.searchStudents(
            searchRequest.getKeyword(),
            searchRequest.getClassName(),
            searchRequest.getMajor(),
            searchRequest.getCourseYear(),
            searchRequest.getGender(),
            Pageable.unpaged()
        ).getContent();
        
        int index = 1;
        for (Student student : students) {
            User user = userRepository.findById(student.getUserId()).orElse(new User());
            html.append("<tr>");
            html.append("<td>").append(index++).append("</td>");
            html.append("<td>").append(student.getStudentCode()).append("</td>");
            html.append("<td>").append(user.getFullName()).append("</td>");
            html.append("<td>").append(user.getEmail()).append("</td>");
            html.append("<td>").append(user.getPhone()).append("</td>");
            html.append("<td>").append(student.getGender()).append("</td>");
            html.append("<td>").append(student.getDob() != null ? student.getDob().toString() : "").append("</td>");
            html.append("<td>").append(student.getAddress()).append("</td>");
            html.append("<td>").append(student.getClassName()).append("</td>");
            html.append("<td>").append(student.getMajor()).append("</td>");
            html.append("<td>").append(student.getCourseYear()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table></body></html>");
        
        // In a real implementation, you would convert HTML to PDF using iText
        return html.toString().getBytes();
    }
    
    private StudentResponse convertToStudentResponse(Student student) {
        User user = userRepository.findById(student.getUserId()).orElse(new User());
        List<StudentResponse.ClassInfo> classes = getStudentClasses(student.getId());
        
        return new StudentResponse(
            student.getId(),
            student.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            student.getStudentCode(),
            student.getGender(),
            student.getDob(),
            student.getAddress(),
            student.getClassName(),
            student.getMajor(),
            student.getCourseYear(),
            student.getCreatedAt(),
            user.getIsActive(),
            classes
        );
    }
}
