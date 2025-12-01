package com.sms.service.impl;

import com.sms.dto.request.SubjectRequest;
import com.sms.dto.response.SubjectListResponse;
import com.sms.dto.response.SubjectResponse;
import com.sms.entity.Subject;
import com.sms.repository.SubjectRepository;
import com.sms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    public SubjectResponse createSubject(SubjectRequest subjectRequest) {
        // Kiểm tra mã môn học đã tồn tại
        if (subjectRepository.existsBySubjectCode(subjectRequest.getSubjectCode())) {
            throw new RuntimeException("Mã môn học đã tồn tại: " + subjectRequest.getSubjectCode());
        }
        
        Subject subject = new Subject();
        subject.setSubjectCode(subjectRequest.getSubjectCode());
        subject.setSubjectName(subjectRequest.getSubjectName());
        subject.setCredit(subjectRequest.getCredit());
        subject.setDescription(subjectRequest.getDescription());
        
        Subject savedSubject = subjectRepository.save(subject);
        return convertToSubjectResponse(savedSubject);
    }
    
    @Override
    public SubjectResponse updateSubject(Long subjectId, SubjectRequest subjectRequest) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        // Kiểm tra mã môn học đã tồn tại (nếu có thay đổi)
        if (!subjectRequest.getSubjectCode().equals(subject.getSubjectCode()) 
            && subjectRepository.existsBySubjectCode(subjectRequest.getSubjectCode())) {
            throw new RuntimeException("Mã môn học đã tồn tại: " + subjectRequest.getSubjectCode());
        }
        
        subject.setSubjectCode(subjectRequest.getSubjectCode());
        subject.setSubjectName(subjectRequest.getSubjectName());
        subject.setCredit(subjectRequest.getCredit());
        subject.setDescription(subjectRequest.getDescription());
        
        Subject updatedSubject = subjectRepository.save(subject);
        return convertToSubjectResponse(updatedSubject);
    }
    
    @Override
    public void deleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        subjectRepository.delete(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        return convertToSubjectResponse(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectByCode(String subjectCode) {
        Subject subject = subjectRepository.findBySubjectCode(subjectCode)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với mã: " + subjectCode));
        
        return convertToSubjectResponse(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectListResponse getAllSubjects(Pageable pageable) {
        Page<Subject> subjectPage = subjectRepository.findAll(pageable);
        
        List<SubjectResponse> subjectResponses = subjectPage.getContent().stream()
            .map(this::convertToSubjectResponse)
            .collect(Collectors.toList());
        
        return new SubjectListResponse(
            subjectResponses,
            subjectPage.getTotalElements(),
            subjectPage.getTotalPages(),
            subjectPage.getNumber(),
            subjectPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubjectListResponse searchSubjectsByKeyword(String keyword, Pageable pageable) {
        Page<Subject> subjectPage = subjectRepository.findSubjectsByKeyword(keyword, pageable);
        
        List<SubjectResponse> subjectResponses = subjectPage.getContent().stream()
            .map(this::convertToSubjectResponse)
            .collect(Collectors.toList());
        
        return new SubjectListResponse(
            subjectResponses,
            subjectPage.getTotalElements(),
            subjectPage.getTotalPages(),
            subjectPage.getNumber(),
            subjectPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> searchSubjectsByName(String subjectName) {
        List<Subject> subjects = subjectRepository.findBySubjectNameContainingIgnoreCase(subjectName);
        
        return subjects.stream()
            .map(this::convertToSubjectResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByCredit(Integer credit) {
        List<Subject> subjects = subjectRepository.findByCredit(credit);
        
        return subjects.stream()
            .map(this::convertToSubjectResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getSubjectsByCreditRange(Integer minCredit, Integer maxCredit) {
        List<Subject> subjects = subjectRepository.findByCreditRange(minCredit, maxCredit);
        
        return subjects.stream()
            .map(this::convertToSubjectResponse)
            .collect(Collectors.toList());
    }
    
    private SubjectResponse convertToSubjectResponse(Subject subject) {
        return new SubjectResponse(
            subject.getId(),
            subject.getSubjectCode(),
            subject.getSubjectName(),
            subject.getCredit(),
            subject.getDescription(),
            subject.getStatus()
        );
    }
}
