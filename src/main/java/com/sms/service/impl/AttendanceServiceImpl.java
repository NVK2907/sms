package com.sms.service.impl;

import com.sms.dto.request.AttendanceRequest;
import com.sms.dto.response.AttendanceResponse;
import com.sms.dto.response.AttendanceReportResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        // Kiểm tra lớp tồn tại
        Course classEntity = courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        // Xóa điểm danh cũ nếu có
        attendanceRepository.deleteByClassIdAndAttendanceDate(request.getClassId(), request.getAttendanceDate());
        
        // Tạo điểm danh mới cho từng sinh viên
        for (AttendanceRequest.StudentAttendanceRequest studentAttendance : request.getStudentAttendances()) {
            Attendance attendance = new Attendance();
            attendance.setClassId(request.getClassId());
            attendance.setStudentId(studentAttendance.getStudentId());
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setStatus(studentAttendance.getStatus());
            
            attendanceRepository.save(attendance);
        }
        
        List<AttendanceResponse> attendances = getAttendanceByClassAndDate(request.getClassId(), request.getAttendanceDate());
        return attendances.isEmpty() ? null : attendances.get(0);
    }
    
    @Override
    public AttendanceResponse updateAttendance(Long attendanceId, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm danh với ID: " + attendanceId));
        
        attendance.setClassId(request.getClassId());
        attendance.setAttendanceDate(request.getAttendanceDate());
        
        // Cập nhật trạng thái điểm danh
        if (!request.getStudentAttendances().isEmpty()) {
            AttendanceRequest.StudentAttendanceRequest studentAttendance = request.getStudentAttendances().get(0);
            attendance.setStatus(studentAttendance.getStatus());
        }
        
        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return convertToAttendanceResponse(updatedAttendance);
    }
    
    @Override
    public void deleteAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm danh với ID: " + attendanceId));
        
        attendanceRepository.delete(attendance);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClass(Long classId) {
        courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<Attendance> attendances = attendanceRepository.findByClassId(classId);
        return attendances.stream()
                .map(this::convertToAttendanceResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByClassAndDate(Long classId, LocalDate date) {
        courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<Attendance> attendances = attendanceRepository.findByClassIdAndAttendanceDate(classId, date);
        
        return attendances.stream()
                .map(this::convertToAttendanceResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByStudent(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + studentId));
        
        List<Attendance> attendances = attendanceRepository.findByStudentId(studentId);
        return attendances.stream()
                .map(this::convertToAttendanceResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AttendanceReportResponse generateAttendanceReport(Long classId) {
        Course classEntity = courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        Subject subject = subjectRepository.findById(classEntity.getSubjectId())
                .orElse(new Subject());
        
        Semester semester = semesterRepository.findById(classEntity.getSemesterId())
                .orElse(new Semester());
        
        // Lấy tất cả điểm danh của lớp
        List<Attendance> allAttendances = attendanceRepository.findByClassId(classId);
        
        // Đếm tổng số buổi học
        long totalSessions = allAttendances.stream()
                .map(Attendance::getAttendanceDate)
                .distinct()
                .count();
        
        // Nhóm theo sinh viên và tính tổng kết
        List<AttendanceReportResponse.StudentAttendanceSummaryResponse> studentSummaries = 
                allAttendances.stream()
                        .collect(Collectors.groupingBy(Attendance::getStudentId))
                        .entrySet()
                        .stream()
                        .map(entry -> {
                            Long studentId = entry.getKey();
                            List<Attendance> studentAttendances = entry.getValue();
                            
                            Student student = studentRepository.findById(studentId)
                                    .orElse(new Student());
                            User user = userRepository.findById(student.getUserId())
                                    .orElse(new User());
                            
                            int presentCount = (int) studentAttendances.stream()
                                    .filter(a -> "present".equals(a.getStatus()))
                                    .count();
                            
                            int absentCount = (int) studentAttendances.stream()
                                    .filter(a -> "absent".equals(a.getStatus()))
                                    .count();
                            
                            int lateCount = (int) studentAttendances.stream()
                                    .filter(a -> "late".equals(a.getStatus()))
                                    .count();
                            
                            double attendanceRate = totalSessions > 0 ? 
                                    (double) presentCount / totalSessions * 100 : 0.0;
                            
                            return new AttendanceReportResponse.StudentAttendanceSummaryResponse(
                                    student.getStudentCode(),
                                    user.getFullName(),
                                    presentCount,
                                    absentCount,
                                    lateCount,
                                    attendanceRate
                            );
                        })
                        .collect(Collectors.toList());
        
        return new AttendanceReportResponse(
                classEntity.getClassCode(),
                subject.getSubjectName(),
                semester.getName(),
                (int) totalSessions,
                studentSummaries
        );
    }
    
    private AttendanceResponse convertToAttendanceResponse(Attendance attendance) {
        Course classEntity = courseRepository.findById(attendance.getClassId())
                .orElse(new Course());
        Subject subject = subjectRepository.findById(classEntity.getSubjectId())
                .orElse(new Subject());
        
        // Lấy danh sách sinh viên trong buổi điểm danh này
        List<Attendance> allAttendances = attendanceRepository.findByClassIdAndAttendanceDate(
                attendance.getClassId(), attendance.getAttendanceDate());
        
        List<AttendanceResponse.StudentAttendanceResponse> studentAttendances = allAttendances.stream()
                .map(att -> {
                    Student student = studentRepository.findById(att.getStudentId())
                            .orElse(new Student());
                    User user = userRepository.findById(student.getUserId())
                            .orElse(new User());
                    
                    return new AttendanceResponse.StudentAttendanceResponse(
                            student.getId(),
                            student.getStudentCode(),
                            user.getFullName(),
                            att.getStatus()
                    );
                })
                .collect(Collectors.toList());
        
        return new AttendanceResponse(
                attendance.getId(),
                classEntity.getClassCode(),
                subject.getSubjectName(),
                attendance.getAttendanceDate(),
                attendance.getRecordedAt(),
                studentAttendances
        );
    }
}
