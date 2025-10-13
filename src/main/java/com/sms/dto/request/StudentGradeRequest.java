package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeRequest {
    private Long classId;
    private Long studentId;
    private Float midterm;
    private Float finalGrade;
    private Float other;
}
