package com.sms.service.impl;

import com.sms.dto.request.MaterialRequest;
import com.sms.dto.response.MaterialResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    public MaterialResponse uploadMaterial(MaterialRequest request) {
        // Kiểm tra lớp tồn tại
        Course classEntity = courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        Material material = new Material();
        material.setClassId(request.getClassId());
        material.setTeacherId(request.getTeacherId());
        material.setTitle(request.getTitle());
        material.setFilePath(request.getFilePath());
        
        Material savedMaterial = materialRepository.save(material);
        return convertToMaterialResponse(savedMaterial);
    }
    
    @Override
    public MaterialResponse updateMaterial(Long materialId, MaterialRequest request) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + materialId));
        
        // Kiểm tra lớp tồn tại
        courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        material.setClassId(request.getClassId());
        material.setTeacherId(request.getTeacherId());
        material.setTitle(request.getTitle());
        material.setFilePath(request.getFilePath());
        
        Material updatedMaterial = materialRepository.save(material);
        return convertToMaterialResponse(updatedMaterial);
    }
    
    @Override
    public void deleteMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + materialId));
        
        materialRepository.delete(material);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByClass(Long classId) {
        courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<Material> materials = materialRepository.findByClassId(classId);
        return materials.stream()
                .map(this::convertToMaterialResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsByTeacher(Long teacherId) {
        List<Material> materials = materialRepository.findByTeacherId(teacherId);
        return materials.stream()
                .map(this::convertToMaterialResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + materialId));
        
        return convertToMaterialResponse(material);
    }
    
    private MaterialResponse convertToMaterialResponse(Material material) {
        Course classEntity = courseRepository.findById(material.getClassId())
                .orElse(new Course());
        Subject subject = subjectRepository.findById(classEntity.getSubjectId())
                .orElse(new Subject());
        
        // Lấy tên file từ đường dẫn
        String fileName = "";
        if (material.getFilePath() != null && !material.getFilePath().isEmpty()) {
            String[] pathParts = material.getFilePath().split("/");
            fileName = pathParts[pathParts.length - 1];
        }
        
        return new MaterialResponse(
                material.getId(),
                classEntity.getClassCode(),
                subject.getSubjectName(),
                material.getTitle(),
                material.getFilePath(),
                fileName,
                material.getUploadedAt()
        );
    }
}
