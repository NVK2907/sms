package com.sms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {
    private Long classId;
    private LocalDate attendanceDate;
    private List<StudentAttendanceRequest> studentAttendances;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceRequest {
        private Long studentId;
        private String status; // present, absent, late
    }
}
