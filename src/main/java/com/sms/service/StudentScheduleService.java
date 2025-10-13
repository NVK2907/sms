package com.sms.service;

import com.sms.dto.response.StudentScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface StudentScheduleService {
    List<StudentScheduleResponse> getWeeklySchedule(Long studentId, LocalDate startDate);
    List<StudentScheduleResponse> getDailySchedule(Long studentId, LocalDate date);
    List<StudentScheduleResponse> getExamSchedule(Long studentId, Long semesterId);
}
