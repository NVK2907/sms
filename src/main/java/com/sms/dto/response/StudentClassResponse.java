package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentClassResponse {
    private Long id;
    private String classCode;
    private String subjectName;
    private String subjectCode;
    private String semesterName;
    private String teacherName;
    private Integer maxStudent;
    private Integer currentStudentCount;
    private LocalDateTime createdAt;
    private Boolean isRegistered;
    private List<ScheduleResponse> schedules;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleResponse {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String room;
    }
}
