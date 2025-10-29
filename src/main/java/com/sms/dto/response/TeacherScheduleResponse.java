package com.sms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherScheduleResponse {
    
    private LocalDate date;
    private List<ScheduleItemResponse> scheduleItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleItemResponse {
        private String classCode;
        private String subjectName;
        private String studentCount;
        private LocalTime startTime;
        private LocalTime endTime;
        private String room;
        private String type; // "class" hoặc "exam"
    }
}
