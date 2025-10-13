package com.sms.service.impl;

import com.sms.dto.response.StudentMaterialResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.StudentMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentMaterialServiceImpl implements StudentMaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentMaterialResponse> getMaterialsByStudent(Long studentId) {
        // Lấy danh sách lớp đã đăng ký
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        List<Long> classIds = classStudents.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        // Lấy tài liệu từ các lớp đã đăng ký
        return classIds.stream()
                .flatMap(classId -> materialRepository.findByClassId(classId).stream())
                .map(this::convertToStudentMaterialResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentMaterialResponse> getMaterialsByClass(Long studentId, Long classId) {
        // Kiểm tra sinh viên đã đăng ký lớp này chưa
        classStudentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên chưa đăng ký lớp này"));
        
        List<Material> materials = materialRepository.findByClassId(classId);
        return materials.stream()
                .map(this::convertToStudentMaterialResponse)
                .collect(Collectors.toList());
    }
    
    private StudentMaterialResponse convertToStudentMaterialResponse(Material material) {
        Course clazz = courseRepository.findById(material.getClassId())
                .orElse(new Course());
        Subject subject = subjectRepository.findById(clazz.getSubjectId())
                .orElse(new Subject());
        
        // Lấy tên file từ đường dẫn
        String fileName = "";
        if (material.getFilePath() != null && !material.getFilePath().isEmpty()) {
            String[] pathParts = material.getFilePath().split("/");
            fileName = pathParts[pathParts.length - 1];
        }
        
        return new StudentMaterialResponse(
                material.getId(),
                clazz.getClassCode(),
                subject.getSubjectName(),
                material.getTitle(),
                material.getFilePath(),
                fileName,
                material.getUploadedAt()
        );
    }
}
