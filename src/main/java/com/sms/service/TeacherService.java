package com.sms.service;

import com.sms.dto.request.TeacherRequest;
import com.sms.dto.request.TeacherUpdateRequest;
import com.sms.dto.response.TeacherListResponse;
import com.sms.dto.response.TeacherResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    
    /**
     * Tạo giáo viên mới
     */
    TeacherResponse createTeacher(TeacherRequest teacherRequest);
    
    /**
     * Cập nhật thông tin giáo viên
     */
    TeacherResponse updateTeacher(Long teacherId, TeacherUpdateRequest teacherUpdateRequest);
    
    /**
     * Xóa giáo viên
     */
    void deleteTeacher(Long teacherId);
    
    /**
     * Lấy thông tin giáo viên theo ID
     */
    TeacherResponse getTeacherById(Long teacherId);
    
    /**
     * Lấy thông tin giáo viên theo mã giáo viên
     */
    TeacherResponse getTeacherByCode(String teacherCode);
    
    /**
     * Lấy danh sách giáo viên với phân trang
     */
    TeacherListResponse getAllTeachers(Pageable pageable, Boolean isActive);
    
    /**
     * Tìm kiếm giáo viên theo keyword
     */
    TeacherListResponse searchTeachers(String keyword, Pageable pageable, Boolean isActive);
    
    /**
     * Lấy danh sách giáo viên theo khoa/bộ môn
     */
    List<TeacherResponse> getTeachersByDepartment(String department);
    
    /**
     * Lấy danh sách giáo viên theo chức danh
     */
    List<TeacherResponse> getTeachersByTitle(String title);
    
    /**
     * Gán môn học cho giáo viên
     */
    void assignSubjectsToTeacher(Long teacherId, List<Long> subjectIds);
    
    /**
     * Hủy gán môn học của giáo viên
     */
    void removeSubjectsFromTeacher(Long teacherId, List<Long> subjectIds);
    
    /**
     * Lấy danh sách môn học của giáo viên
     */
    List<TeacherResponse.SubjectInfo> getTeacherSubjects(Long teacherId);
}
