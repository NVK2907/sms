package com.sms.service;

import com.sms.dto.request.*;
import com.sms.dto.response.StudentListResponse;
import com.sms.dto.response.StudentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    
    /**
     * Tạo sinh viên mới
     */
    StudentResponse createStudent(StudentRequest studentRequest);
    
    /**
     * Cập nhật thông tin sinh viên
     */
    StudentResponse updateStudent(Long studentId, StudentUpdateRequest studentUpdateRequest);
    
    /**
     * Xóa sinh viên
     */
    void deleteStudent(Long studentId);
    
    /**
     * Lấy thông tin sinh viên theo ID
     */
    StudentResponse getStudentById(Long studentId);
    
    /**
     * Lấy thông tin sinh viên theo mã sinh viên
     */
    StudentResponse getStudentByCode(String studentCode);
    
    /**
     * Lấy danh sách sinh viên với phân trang
     */
    StudentListResponse getAllStudents(Pageable pageable);
    
    /**
     * Tìm kiếm và lọc sinh viên
     */
    StudentListResponse searchStudents(StudentSearchRequest searchRequest, Pageable pageable);
    
    /**
     * Lấy danh sách sinh viên theo lớp
     */
    List<StudentResponse> getStudentsByClass(String className);
    
    /**
     * Lấy danh sách sinh viên theo chuyên ngành
     */
    List<StudentResponse> getStudentsByMajor(String major);
    
    /**
     * Lấy danh sách sinh viên theo khóa học
     */
    List<StudentResponse> getStudentsByCourseYear(Integer courseYear);
    
    /**
     * Gán sinh viên vào lớp học
     */
    void assignStudentToClasses(ClassAssignmentRequest classAssignmentRequest);
    
    /**
     * Hủy gán sinh viên khỏi lớp học
     */
    void removeStudentFromClasses(Long studentId, List<Long> classIds);
    
    /**
     * Lấy danh sách lớp học của sinh viên
     */
    List<StudentResponse.ClassInfo> getStudentClasses(Long studentId);
    
    /**
     * Xuất danh sách sinh viên ra Excel
     */
    byte[] exportStudentsToExcel(StudentSearchRequest searchRequest);
    
    /**
     * Xuất danh sách sinh viên ra PDF
     */
    byte[] exportStudentsToPDF(StudentSearchRequest searchRequest);
}
