package com.sms.service;

import com.sms.dto.request.ClassRequest;
import com.sms.dto.response.ClassListResponse;
import com.sms.dto.response.ClassResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassService {
    
    /**
     * Tạo lớp học mới
     */
    ClassResponse createClass(ClassRequest classRequest);
    
    /**
     * Cập nhật thông tin lớp học
     */
    ClassResponse updateClass(Long classId, ClassRequest classRequest);
    
    /**
     * Xóa lớp học
     */
    void deleteClass(Long classId);
    
    /**
     * Lấy thông tin lớp học theo ID
     */
    ClassResponse getClassById(Long classId);
    
    /**
     * Lấy thông tin lớp học theo mã lớp
     */
    ClassResponse getClassByCode(String classCode);
    
    /**
     * Lấy danh sách lớp học với phân trang
     */
    ClassListResponse getAllClasses(Pageable pageable);
    
    /**
     * Tìm kiếm lớp học với phân trang
     */
    ClassListResponse searchClasses(Pageable pageable, String keyword);
    
    /**
     * Lấy danh sách lớp học theo môn học
     */
    List<ClassResponse> getClassesBySubject(Long subjectId);
    
    /**
     * Lấy danh sách lớp học theo học kỳ
     */
    List<ClassResponse> getClassesBySemester(Long semesterId);
    
    /**
     * Lấy danh sách lớp học theo giáo viên
     */
    List<ClassResponse> getClassesByTeacher(Long teacherId);
    
    /**
     * Lấy danh sách lớp học theo môn học và học kỳ
     */
    List<ClassResponse> getClassesBySubjectAndSemester(Long subjectId, Long semesterId);
    
    /**
     * Lấy danh sách lớp học theo giáo viên và học kỳ
     */
    List<ClassResponse> getClassesByTeacherAndSemester(Long teacherId, Long semesterId);
    
    /**
     * Phân công giáo viên giảng dạy
     */
    void assignTeacherToClass(Long classId, Long teacherId);
    
    /**
     * Hủy phân công giáo viên
     */
    void removeTeacherFromClass(Long classId);
    
    /**
     * Lấy danh sách sinh viên trong lớp
     */
    List<ClassResponse.StudentInfo> getClassStudents(Long classId);
    
    /**
     * Import sinh viên vào lớp học từ file Excel
     */
    void importStudentsFromFile(Long classId, org.springframework.web.multipart.MultipartFile file);
}
