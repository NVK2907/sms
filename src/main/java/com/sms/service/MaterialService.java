package com.sms.service;

import com.sms.dto.request.MaterialRequest;
import com.sms.dto.response.MaterialResponse;

import java.util.List;

public interface MaterialService {
    MaterialResponse uploadMaterial(MaterialRequest request);
    MaterialResponse updateMaterial(Long materialId, MaterialRequest request);
    void deleteMaterial(Long materialId);
    List<MaterialResponse> getMaterialsByClass(Long classId);
    List<MaterialResponse> getMaterialsByTeacher(Long teacherId);
    MaterialResponse getMaterialById(Long materialId);
}
