package com.sms.service;

import com.sms.dto.request.AttendanceRequest;
import com.sms.dto.response.AttendanceResponse;
import com.sms.dto.response.AttendanceReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse recordAttendance(AttendanceRequest request);
    AttendanceResponse updateAttendance(Long attendanceId, AttendanceRequest request);
    void deleteAttendance(Long attendanceId);
    List<AttendanceResponse> getAttendanceByClass(Long classId);
    List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, LocalDate date);
    List<AttendanceResponse> getAttendanceByStudent(Long studentId);
    AttendanceReportResponse generateAttendanceReport(Long classId);
}
