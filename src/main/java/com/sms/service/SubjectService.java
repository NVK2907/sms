package com.sms.service;

import com.sms.dto.request.SubjectRequest;
import com.sms.dto.response.SubjectListResponse;
import com.sms.dto.response.SubjectResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {
    
    /**
     * Tạo môn học mới
     */
    SubjectResponse createSubject(SubjectRequest subjectRequest);
    
    /**
     * Cập nhật thông tin môn học
     */
    SubjectResponse updateSubject(Long subjectId, SubjectRequest subjectRequest);
    
    /**
     * Xóa môn học
     */
    void deleteSubject(Long subjectId);
    
    /**
     * Lấy thông tin môn học theo ID
     */
    SubjectResponse getSubjectById(Long subjectId);
    
    /**
     * Lấy thông tin môn học theo mã môn học
     */
    SubjectResponse getSubjectByCode(String subjectCode);
    
    /**
     * Lấy danh sách môn học với phân trang và tìm kiếm tùy chọn
     */
    SubjectListResponse getAllSubjects(Pageable pageable);
    
    /**
     * Tìm kiếm môn học với phân trang theo keyword
     */
    SubjectListResponse searchSubjectsByKeyword(String keyword, Pageable pageable);
    
    /**
     * Tìm kiếm môn học theo tên
     */
    List<SubjectResponse> searchSubjectsByName(String subjectName);
    
    /**
     * Lấy danh sách môn học theo số tín chỉ
     */
    List<SubjectResponse> getSubjectsByCredit(Integer credit);
    
    /**
     * Lấy danh sách môn học theo khoảng tín chỉ
     */
    List<SubjectResponse> getSubjectsByCreditRange(Integer minCredit, Integer maxCredit);
}
