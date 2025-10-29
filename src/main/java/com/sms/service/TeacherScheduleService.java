package com.sms.service;

import com.sms.dto.response.TeacherScheduleResponse;

import java.time.LocalDate;
import java.util.List;

public interface TeacherScheduleService {
    
    /**
     * Lấy lịch học theo tuần của giáo viên
     */
    List<TeacherScheduleResponse> getWeeklySchedule(Long teacherId, LocalDate startDate);
    
    /**
     * Lấy lịch học theo ngày của giáo viên
     */
    List<TeacherScheduleResponse> getDailySchedule(Long teacherId, LocalDate date);
    
    /**
     * Lấy lịch thi của giáo viên trong học kỳ
     */
    List<TeacherScheduleResponse> getExamSchedule(Long teacherId, Long semesterId);
}
