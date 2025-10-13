package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private LocalDate attendanceDate;
    private LocalDateTime recordedAt;
    private List<StudentAttendanceResponse> studentAttendances;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceResponse {
        private Long studentId;
        private String studentCode;
        private String studentName;
        private String status;
    }
}
