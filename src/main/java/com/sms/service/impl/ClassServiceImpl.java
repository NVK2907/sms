package com.sms.service.impl;

import com.sms.dto.request.ClassRequest;
import com.sms.dto.response.ClassListResponse;
import com.sms.dto.response.ClassResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.ClassService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassServiceImpl implements ClassService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public ClassResponse createClass(ClassRequest classRequest) {
        // Kiểm tra mã lớp đã tồn tại
        if (courseRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new RuntimeException("Mã lớp đã tồn tại: " + classRequest.getClassCode());
        }
        
        // Kiểm tra môn học tồn tại
        subjectRepository.findById(classRequest.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + classRequest.getSubjectId()));
        
        // Kiểm tra học kỳ tồn tại
        semesterRepository.findById(classRequest.getSemesterId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + classRequest.getSemesterId()));
        
        // Kiểm tra giáo viên tồn tại (nếu có)
        if (classRequest.getTeacherId() != null) {
            teacherRepository.findById(classRequest.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + classRequest.getTeacherId()));
        }
        
        Course courseEntity = new Course();
        courseEntity.setClassCode(classRequest.getClassCode());
        courseEntity.setSubjectId(classRequest.getSubjectId());
        courseEntity.setSemesterId(classRequest.getSemesterId());
        courseEntity.setTeacherId(classRequest.getTeacherId());
        courseEntity.setMaxStudent(classRequest.getMaxStudent());
        
        Course savedCourse = courseRepository.save(courseEntity);
        return convertToClassResponse(savedCourse);
    }
    
    @Override
    public ClassResponse updateClass(Long classId, ClassRequest classRequest) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        // Kiểm tra mã lớp đã tồn tại (nếu có thay đổi)
        if (!classRequest.getClassCode().equals(courseEntity.getClassCode()) 
            && courseRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new RuntimeException("Mã lớp đã tồn tại: " + classRequest.getClassCode());
        }
        
        // Kiểm tra môn học tồn tại
        subjectRepository.findById(classRequest.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + classRequest.getSubjectId()));
        
        // Kiểm tra học kỳ tồn tại
        semesterRepository.findById(classRequest.getSemesterId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + classRequest.getSemesterId()));
        
        // Kiểm tra giáo viên tồn tại (nếu có)
        if (classRequest.getTeacherId() != null) {
            teacherRepository.findById(classRequest.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + classRequest.getTeacherId()));
        }
        
        courseEntity.setClassCode(classRequest.getClassCode());
        courseEntity.setSubjectId(classRequest.getSubjectId());
        courseEntity.setSemesterId(classRequest.getSemesterId());
        courseEntity.setTeacherId(classRequest.getTeacherId());
        courseEntity.setMaxStudent(classRequest.getMaxStudent());
        
        Course updatedCourse = courseRepository.save(courseEntity);
        return convertToClassResponse(updatedCourse);
    }
    
    @Override
    public void deleteClass(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        // Xóa tất cả class_student records
        classStudentRepository.deleteByClassId(classId);
        
        // Xóa lớp
        courseRepository.delete(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        return convertToClassResponse(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassByCode(String classCode) {
        Course courseEntity = courseRepository.findByClassCode(classCode)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với mã: " + classCode));
        
        return convertToClassResponse(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassListResponse getAllClasses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        
        List<ClassResponse> classResponses = coursePage.getContent().stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
        
        return new ClassListResponse(
            classResponses,
            coursePage.getTotalElements(),
            coursePage.getTotalPages(),
            coursePage.getNumber(),
            coursePage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassListResponse searchClasses(Pageable pageable, String keyword) {
        Page<Course> coursePage = courseRepository.searchClasses(keyword, pageable);
        
        List<ClassResponse> classResponses = coursePage.getContent().stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
        
        return new ClassListResponse(
            classResponses,
            coursePage.getTotalElements(),
            coursePage.getTotalPages(),
            coursePage.getNumber(),
            coursePage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySubject(Long subjectId) {
        subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        List<Course> courses = courseRepository.findBySubjectId(subjectId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySemester(Long semesterId) {
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findBySemesterId(semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByTeacher(Long teacherId) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySubjectAndSemester(Long subjectId, Long semesterId) {
        subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findBySubjectIdAndSemesterId(subjectId, semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByTeacherAndSemester(Long teacherId, Long semesterId) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignTeacherToClass(Long classId, Long teacherId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        courseEntity.setTeacherId(teacherId);
        courseRepository.save(courseEntity);
    }
    
    @Override
    public void removeTeacherFromClass(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        courseEntity.setTeacherId(null);
        courseRepository.save(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse.StudentInfo> getClassStudents(Long classId) {
        courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<ClassStudent> classStudents = classStudentRepository.findByClassId(classId);
        
        return classStudents.stream()
            .map(classStudent -> {
                Student student = studentRepository.findById(classStudent.getStudentId())
                    .orElse(new Student());
                User user = userRepository.findById(student.getUserId())
                    .orElse(new User());
                
                return new ClassResponse.StudentInfo(
                    student.getId(),
                    student.getStudentCode(),
                    user.getFullName(),
                    classStudent.getRegisteredAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public void importStudentsFromFile(Long classId, MultipartFile file) {
        // Kiểm tra lớp tồn tại
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        // Kiểm tra file
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new RuntimeException("File phải là định dạng Excel (.xlsx hoặc .xls)");
        }
        
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int skipCount = 0;
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // Validate header (dòng 0): STT, Mã SV, Họ Và Tên
            // Format: Cột 0 = STT, Cột 1 = Mã SV, Cột 2 = Họ Và Tên
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                Cell headerCell2 = headerRow.getCell(1);
                String header2 = getCellValueAsString(headerCell2);
                
                // Kiểm tra header có đúng format không (không bắt buộc, chỉ cảnh báo)
                if (header2 != null && !header2.trim().toLowerCase().contains("mã")) {
                    // Có thể log warning nhưng vẫn tiếp tục xử lý
                }
            }
            
            // Đọc từ dòng 1 (bỏ qua header ở dòng 0)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                
                // Lấy mã sinh viên từ cột thứ 2 (index 1) - cột "Mã SV"
                // Cột 0: STT, Cột 1: Mã SV, Cột 2: Họ Và Tên
                Cell studentCodeCell = row.getCell(1);
                if (studentCodeCell == null) continue;
                
                String studentCode = getCellValueAsString(studentCodeCell);
                if (studentCode == null || studentCode.trim().isEmpty()) {
                    continue;
                }
                
                studentCode = studentCode.trim();
                
                // Tìm sinh viên theo mã
                Student student = studentRepository.findByStudentCode(studentCode)
                    .orElse(null);
                
                if (student == null) {
                    errors.add("Dòng " + (rowIndex + 1) + ": Không tìm thấy sinh viên với mã " + studentCode);
                    skipCount++;
                    continue;
                }
                
                // Kiểm tra sinh viên đã đăng ký lớp này chưa
                if (classStudentRepository.findByClassIdAndStudentId(classId, student.getId()).isPresent()) {
                    skipCount++;
                    continue;
                }
                
                // Kiểm tra lớp còn chỗ không
                Long currentCount = classStudentRepository.countByClassId(classId);
                if (currentCount >= courseEntity.getMaxStudent()) {
                    errors.add("Dòng " + (rowIndex + 1) + ": Lớp đã đầy, không thể thêm thêm sinh viên");
                    skipCount++;
                    continue;
                }
                
                // Thêm sinh viên vào lớp
                ClassStudent classStudent = new ClassStudent();
                classStudent.setClassId(classId);
                classStudent.setStudentId(student.getId());
                
                try {
                    classStudentRepository.save(classStudent);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Dòng " + (rowIndex + 1) + ": Lỗi khi thêm sinh viên " + studentCode + " - " + e.getMessage());
                    skipCount++;
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
        
        // Nếu có lỗi, throw exception với thông báo chi tiết
        if (!errors.isEmpty()) {
            String errorMessage = String.format(
                "Import hoàn tất. Thành công: %d, Bỏ qua: %d. Các lỗi:\n%s",
                successCount, skipCount, String.join("\n", errors)
            );
            throw new RuntimeException(errorMessage);
        }
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Lấy số nguyên nếu là số nguyên, ngược lại lấy số thực
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private ClassResponse convertToClassResponse(Course courseEntity) {
        Subject subject = subjectRepository.findById(courseEntity.getSubjectId())
            .orElse(new Subject());
        
        Semester semester = semesterRepository.findById(courseEntity.getSemesterId())
            .orElse(new Semester());
        
        Teacher teacher = null;
        String teacherName = null;
        if (courseEntity.getTeacherId() != null) {
            teacher = teacherRepository.findById(courseEntity.getTeacherId()).orElse(null);
            if (teacher != null) {
                User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                teacherName = teacherUser.getFullName();
            }
        }
        
        Long currentStudentCount = classStudentRepository.countByClassId(courseEntity.getId());
        List<ClassResponse.StudentInfo> students = getClassStudents(courseEntity.getId());
        
        return new ClassResponse(
            courseEntity.getId(),
            courseEntity.getClassCode(),
            courseEntity.getSubjectId(),
            subject.getSubjectName(),
            subject.getSubjectCode(),
            courseEntity.getSemesterId(),
            semester.getName(),
            courseEntity.getTeacherId(),
            teacherName,
            courseEntity.getMaxStudent(),
            currentStudentCount.intValue(),
            courseEntity.getCreatedAt(),
            courseEntity.getStatus(),
            students
        );
    }
}
