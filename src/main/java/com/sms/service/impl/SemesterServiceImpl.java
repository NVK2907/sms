package com.sms.service.impl;

import com.sms.dto.request.*;
import com.sms.dto.response.AcademicYearListResponse;
import com.sms.dto.response.AcademicYearResponse;
import com.sms.dto.response.SemesterListResponse;
import com.sms.dto.response.SemesterResponse;
import com.sms.entity.AcademicYear;
import com.sms.entity.Semester;
import com.sms.exception.*;
import com.sms.repository.AcademicYearRepository;
import com.sms.repository.SemesterRepository;
import com.sms.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SemesterServiceImpl implements SemesterService {
    
    @Autowired
    private AcademicYearRepository academicYearRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    // Academic Year Management
    
    @Override
    public AcademicYearResponse createAcademicYear(AcademicYearRequest academicYearRequest) {
        // Kiểm tra tên năm học đã tồn tại
        if (academicYearRepository.existsByName(academicYearRequest.getName())) {
            throw new AcademicYearAlreadyExistsException(academicYearRequest.getName(), true);
        }
        
        // Kiểm tra ngày hợp lệ
        if (academicYearRequest.getStartDate().isAfter(academicYearRequest.getEndDate())) {
            throw new InvalidDateRangeException();
        }
        
        AcademicYear academicYear = new AcademicYear();
        academicYear.setName(academicYearRequest.getName());
        academicYear.setStartDate(academicYearRequest.getStartDate());
        academicYear.setEndDate(academicYearRequest.getEndDate());
        
        AcademicYear savedAcademicYear = academicYearRepository.save(academicYear);
        return convertToAcademicYearResponse(savedAcademicYear);
    }
    
    @Override
    public AcademicYearResponse updateAcademicYear(Long academicYearId, AcademicYearRequest academicYearRequest) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new AcademicYearNotFoundException(academicYearId));
        
        // Kiểm tra tên năm học đã tồn tại (nếu có thay đổi)
        if (!academicYearRequest.getName().equals(academicYear.getName()) 
            && academicYearRepository.existsByName(academicYearRequest.getName())) {
            throw new AcademicYearAlreadyExistsException(academicYearRequest.getName(), true);
        }
        
        // Kiểm tra ngày hợp lệ
        if (academicYearRequest.getStartDate().isAfter(academicYearRequest.getEndDate())) {
            throw new InvalidDateRangeException();
        }
        
        academicYear.setName(academicYearRequest.getName());
        academicYear.setStartDate(academicYearRequest.getStartDate());
        academicYear.setEndDate(academicYearRequest.getEndDate());
        
        AcademicYear updatedAcademicYear = academicYearRepository.save(academicYear);
        return convertToAcademicYearResponse(updatedAcademicYear);
    }
    
    @Override
    public void deleteAcademicYear(Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new AcademicYearNotFoundException(academicYearId));
        
        // Kiểm tra có học kỳ nào thuộc năm học này không
        List<Semester> semesters = semesterRepository.findByAcademicYearId(academicYearId);
        if (!semesters.isEmpty()) {
            throw new AcademicYearHasSemestersException();
        }
        
        academicYearRepository.delete(academicYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getAcademicYearById(Long academicYearId) {
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new AcademicYearNotFoundException(academicYearId));
        
        return convertToAcademicYearResponse(academicYear);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AcademicYearListResponse getAllAcademicYears(Pageable pageable) {
        Page<AcademicYear> academicYearPage = academicYearRepository.findAllOrderByStartDateDesc(pageable);
        
        List<AcademicYearResponse> academicYearResponses = academicYearPage.getContent().stream()
            .map(this::convertToAcademicYearResponse)
            .collect(Collectors.toList());
        
        return new AcademicYearListResponse(
            academicYearResponses,
            academicYearPage.getTotalElements(),
            academicYearPage.getTotalPages(),
            academicYearPage.getNumber(),
            academicYearPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponse getCurrentAcademicYear() {
        LocalDate today = LocalDate.now();
        AcademicYear currentAcademicYear = academicYearRepository.findByDate(today)
            .orElseThrow(() -> new AcademicYearNotFoundException("Không tìm thấy năm học hiện tại"));
        
        return convertToAcademicYearResponse(currentAcademicYear);
    }
    
    // Semester Management
    
    @Override
    public SemesterResponse createSemester(SemesterRequest semesterRequest) {
        // Kiểm tra năm học tồn tại
        AcademicYear academicYear = academicYearRepository.findById(semesterRequest.getAcademicYearId())
            .orElseThrow(() -> new AcademicYearNotFoundException(semesterRequest.getAcademicYearId()));
        
        // Kiểm tra tên học kỳ đã tồn tại trong năm học
        if (semesterRepository.findByAcademicYearIdAndName(semesterRequest.getAcademicYearId(), semesterRequest.getName()).isPresent()) {
            throw new SemesterAlreadyExistsException(semesterRequest.getName(), semesterRequest.getAcademicYearId());
        }
        
        // Kiểm tra ngày hợp lệ
        if (semesterRequest.getStartDate().isAfter(semesterRequest.getEndDate())) {
            throw new InvalidDateRangeException();
        }
        
        // Kiểm tra ngày học kỳ nằm trong khoảng năm học
        if (semesterRequest.getStartDate().isBefore(academicYear.getStartDate()) 
            || semesterRequest.getEndDate().isAfter(academicYear.getEndDate())) {
            throw new InvalidDateRangeException("Ngày học kỳ phải nằm trong khoảng thời gian của năm học");
        }
        
        Semester semester = new Semester();
        semester.setAcademicYearId(semesterRequest.getAcademicYearId());
        semester.setName(semesterRequest.getName());
        semester.setStartDate(semesterRequest.getStartDate());
        semester.setEndDate(semesterRequest.getEndDate());
        semester.setIsOpen(semesterRequest.getIsOpen() != null ? semesterRequest.getIsOpen() : true);
        
        Semester savedSemester = semesterRepository.save(semester);
        return convertToSemesterResponse(savedSemester);
    }
    
    @Override
    public SemesterResponse updateSemester(Long semesterId, SemesterRequest semesterRequest) {
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new SemesterNotFoundException(semesterId));
        
        // Kiểm tra năm học tồn tại
        AcademicYear academicYear = academicYearRepository.findById(semesterRequest.getAcademicYearId())
            .orElseThrow(() -> new AcademicYearNotFoundException(semesterRequest.getAcademicYearId()));
        
        // Kiểm tra tên học kỳ đã tồn tại trong năm học (nếu có thay đổi)
        if (!semesterRequest.getName().equals(semester.getName()) 
            && semesterRepository.findByAcademicYearIdAndName(semesterRequest.getAcademicYearId(), semesterRequest.getName()).isPresent()) {
            throw new SemesterAlreadyExistsException(semesterRequest.getName(), semesterRequest.getAcademicYearId());
        }
        
        // Kiểm tra ngày hợp lệ
        if (semesterRequest.getStartDate().isAfter(semesterRequest.getEndDate())) {
            throw new InvalidDateRangeException();
        }
        
        // Kiểm tra ngày học kỳ nằm trong khoảng năm học
        if (semesterRequest.getStartDate().isBefore(academicYear.getStartDate()) 
            || semesterRequest.getEndDate().isAfter(academicYear.getEndDate())) {
            throw new InvalidDateRangeException("Ngày học kỳ phải nằm trong khoảng thời gian của năm học");
        }
        
        semester.setAcademicYearId(semesterRequest.getAcademicYearId());
        semester.setName(semesterRequest.getName());
        semester.setStartDate(semesterRequest.getStartDate());
        semester.setEndDate(semesterRequest.getEndDate());
        if (semesterRequest.getIsOpen() != null) {
            semester.setIsOpen(semesterRequest.getIsOpen());
        }
        
        Semester updatedSemester = semesterRepository.save(semester);
        return convertToSemesterResponse(updatedSemester);
    }
    
    @Override
    public void deleteSemester(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new SemesterNotFoundException(semesterId));
        
        semesterRepository.delete(semester);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SemesterResponse getSemesterById(Long semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new SemesterNotFoundException(semesterId));
        
        return convertToSemesterResponse(semester);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SemesterListResponse getAllSemesters(Pageable pageable) {
        Page<Semester> semesterPage = semesterRepository.findAllOrderByStartDateDesc(pageable);
        
        List<SemesterResponse> semesterResponses = semesterPage.getContent().stream()
            .map(this::convertToSemesterResponse)
            .collect(Collectors.toList());
        
        return new SemesterListResponse(
            semesterResponses,
            semesterPage.getTotalElements(),
            semesterPage.getTotalPages(),
            semesterPage.getNumber(),
            semesterPage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponse> getSemestersByAcademicYear(Long academicYearId) {
        academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new AcademicYearNotFoundException(academicYearId));
        
        List<Semester> semesters = semesterRepository.findByAcademicYearId(academicYearId);
        
        return semesters.stream()
            .map(this::convertToSemesterResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public SemesterResponse getCurrentSemester() {
        LocalDate today = LocalDate.now();
        Semester currentSemester = semesterRepository.findByDate(today)
            .orElseThrow(() -> new SemesterNotFoundException("Không tìm thấy học kỳ hiện tại"));
        
        return convertToSemesterResponse(currentSemester);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SemesterResponse> getOpenSemesters() {
        List<Semester> openSemesters = semesterRepository.findByIsOpenTrue();
        
        return openSemesters.stream()
            .map(this::convertToSemesterResponse)
            .collect(Collectors.toList());
    }
    
    // Registration Management
    
    @Override
    public void changeRegistrationStatus(Long semesterId, RegistrationStatusRequest registrationStatusRequest) {
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new SemesterNotFoundException(semesterId));
        
        semester.setIsOpen(registrationStatusRequest.getIsOpen());
        semesterRepository.save(semester);
    }
    
    @Override
    public void closeAllRegistrations() {
        semesterRepository.updateAllRegistrationStatus(false);
    }
    
    @Override
    public void openAllRegistrations() {
        semesterRepository.updateAllRegistrationStatus(true);
    }
    
    // Helper Methods
    
    private AcademicYearResponse convertToAcademicYearResponse(AcademicYear academicYear) {
        List<Semester> semesters = semesterRepository.findByAcademicYearId(academicYear.getId());
        
        List<AcademicYearResponse.SemesterInfo> semesterInfos = semesters.stream()
            .map(semester -> new AcademicYearResponse.SemesterInfo(
                semester.getId(),
                semester.getName(),
                semester.getIsOpen(),
                semester.getStartDate(),
                semester.getEndDate()
            ))
            .collect(Collectors.toList());
        
        return new AcademicYearResponse(
            academicYear.getId(),
            academicYear.getName(),
            academicYear.getStartDate(),
            academicYear.getEndDate(),
            semesterInfos
        );
    }
    
    private SemesterResponse convertToSemesterResponse(Semester semester) {
        AcademicYear academicYear = academicYearRepository.findById(semester.getAcademicYearId())
            .orElse(new AcademicYear()); // Fallback nếu không tìm thấy
        
        return new SemesterResponse(
            semester.getId(),
            semester.getAcademicYearId(),
            academicYear.getName(),
            semester.getName(),
            semester.getIsOpen(),
            semester.getStartDate(),
            semester.getEndDate()
        );
    }
}
