package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequest {
    private Long classId;
    private Long teacherId;
    private String title;
    private String filePath;
}
