package com.sms.service;

import com.sms.dto.request.*;
import com.sms.dto.response.AcademicYearListResponse;
import com.sms.dto.response.AcademicYearResponse;
import com.sms.dto.response.SemesterListResponse;
import com.sms.dto.response.SemesterResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SemesterService {
    
    // Academic Year Management
    
    /**
     * Tạo năm học mới
     */
    AcademicYearResponse createAcademicYear(AcademicYearRequest academicYearRequest);
    
    /**
     * Cập nhật thông tin năm học
     */
    AcademicYearResponse updateAcademicYear(Long academicYearId, AcademicYearRequest academicYearRequest);
    
    /**
     * Xóa năm học
     */
    void deleteAcademicYear(Long academicYearId);
    
    /**
     * Lấy thông tin năm học theo ID
     */
    AcademicYearResponse getAcademicYearById(Long academicYearId);
    
    /**
     * Lấy danh sách năm học với phân trang
     */
    AcademicYearListResponse getAllAcademicYears(Pageable pageable);
    
    /**
     * Lấy năm học hiện tại
     */
    AcademicYearResponse getCurrentAcademicYear();
    
    // Semester Management
    
    /**
     * Tạo học kỳ mới
     */
    SemesterResponse createSemester(SemesterRequest semesterRequest);
    
    /**
     * Cập nhật thông tin học kỳ
     */
    SemesterResponse updateSemester(Long semesterId, SemesterRequest semesterRequest);
    
    /**
     * Xóa học kỳ
     */
    void deleteSemester(Long semesterId);
    
    /**
     * Lấy thông tin học kỳ theo ID
     */
    SemesterResponse getSemesterById(Long semesterId);
    
    /**
     * Lấy danh sách học kỳ với phân trang
     */
    SemesterListResponse getAllSemesters(Pageable pageable);
    
    /**
     * Lấy danh sách học kỳ theo năm học
     */
    List<SemesterResponse> getSemestersByAcademicYear(Long academicYearId);
    
    /**
     * Lấy học kỳ hiện tại
     */
    SemesterResponse getCurrentSemester();
    
    /**
     * Lấy danh sách học kỳ đang mở đăng ký
     */
    List<SemesterResponse> getOpenSemesters();
    
    // Registration Management
    
    /**
     * Đóng/mở đăng ký học phần cho học kỳ
     */
    void changeRegistrationStatus(Long semesterId, RegistrationStatusRequest registrationStatusRequest);
    
    /**
     * Đóng tất cả đăng ký học phần
     */
    void closeAllRegistrations();
    
    /**
     * Mở tất cả đăng ký học phần
     */
    void openAllRegistrations();
}
