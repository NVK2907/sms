package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportResponse {
    private String classCode;
    private String subjectName;
    private String semesterName;
    private Integer totalSessions;
    private List<StudentAttendanceSummaryResponse> studentSummaries;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceSummaryResponse {
        private String studentCode;
        private String studentName;
        private Integer presentCount;
        private Integer absentCount;
        private Integer lateCount;
        private Double attendanceRate;
    }
}
