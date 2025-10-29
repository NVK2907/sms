package com.sms.service.impl;

import com.sms.dto.response.TeacherScheduleResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.TeacherScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class TeacherScheduleServiceImpl implements TeacherScheduleService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherScheduleResponse> getWeeklySchedule(Long teacherId, LocalDate startDate) {
        List<TeacherScheduleResponse> weeklySchedule = new ArrayList<>();
        
        // Lấy 7 ngày từ startDate
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            List<TeacherScheduleResponse.ScheduleItemResponse> scheduleItems = new ArrayList<>();
            
            // Lấy lịch học
            List<Schedule> schedules = scheduleRepository.findByTeacherIdAndDayOfWeek(teacherId, date.getDayOfWeek().getValue());
            for (Schedule schedule : schedules) {
                Course course = courseRepository.findById(schedule.getClassId()).orElse(new Course());
                Subject subject = subjectRepository.findById(course.getSubjectId()).orElse(new Subject());
                
                // Đếm số sinh viên trong lớp
                long studentCount = classStudentRepository.countByClassId(course.getId());
                
                scheduleItems.add(new TeacherScheduleResponse.ScheduleItemResponse(
                        course.getClassCode(),
                        subject.getSubjectName(),
                        String.valueOf(studentCount),
                        schedule.getStartTime(),
                        schedule.getEndTime(),
                        schedule.getRoom(),
                        "class"
                ));
            }
            
            // Lấy lịch thi
            List<Exam> exams = examRepository.findByTeacherIdAndExamDate(teacherId, date);
            for (Exam exam : exams) {
                Course course = courseRepository.findById(exam.getClassId()).orElse(new Course());
                Subject subject = subjectRepository.findById(course.getSubjectId()).orElse(new Subject());
                
                // Đếm số sinh viên trong lớp
                long studentCount = classStudentRepository.countByClassId(course.getId());
                
                scheduleItems.add(new TeacherScheduleResponse.ScheduleItemResponse(
                        course.getClassCode(),
                        subject.getSubjectName(),
                        String.valueOf(studentCount),
                        exam.getExamTime(),
                        null,
                        exam.getRoom(),
                        "exam"
                ));
            }
            
            // Sắp xếp theo thời gian
            scheduleItems.sort(Comparator.comparing(TeacherScheduleResponse.ScheduleItemResponse::getStartTime));
            
            weeklySchedule.add(new TeacherScheduleResponse(date, scheduleItems));
        }
        
        return weeklySchedule;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherScheduleResponse> getDailySchedule(Long teacherId, LocalDate date) {
        List<TeacherScheduleResponse.ScheduleItemResponse> scheduleItems = new ArrayList<>();
        
        // Lấy lịch học
        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndDayOfWeek(teacherId, date.getDayOfWeek().getValue());
        for (Schedule schedule : schedules) {
            Course course = courseRepository.findById(schedule.getClassId()).orElse(new Course());
            Subject subject = subjectRepository.findById(course.getSubjectId()).orElse(new Subject());
            
            // Đếm số sinh viên trong lớp
            long studentCount = classStudentRepository.countByClassId(course.getId());
            
            scheduleItems.add(new TeacherScheduleResponse.ScheduleItemResponse(
                    course.getClassCode(),
                    subject.getSubjectName(),
                    String.valueOf(studentCount),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getRoom(),
                    "class"
            ));
        }
        
        // Lấy lịch thi
        List<Exam> exams = examRepository.findByTeacherIdAndExamDate(teacherId, date);
        for (Exam exam : exams) {
            Course course = courseRepository.findById(exam.getClassId()).orElse(new Course());
            Subject subject = subjectRepository.findById(course.getSubjectId()).orElse(new Subject());
            
            // Đếm số sinh viên trong lớp
            long studentCount = classStudentRepository.countByClassId(course.getId());
            
            scheduleItems.add(new TeacherScheduleResponse.ScheduleItemResponse(
                    course.getClassCode(),
                    subject.getSubjectName(),
                    String.valueOf(studentCount),
                    exam.getExamTime(),
                    null,
                    exam.getRoom(),
                    "exam"
            ));
        }
        
        // Sắp xếp theo thời gian
        scheduleItems.sort(Comparator.comparing(TeacherScheduleResponse.ScheduleItemResponse::getStartTime));
        
        return List.of(new TeacherScheduleResponse(date, scheduleItems));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherScheduleResponse> getExamSchedule(Long teacherId, Long semesterId) {
        Map<LocalDate, List<TeacherScheduleResponse.ScheduleItemResponse>> examMap = new HashMap<>();
        
        // Lấy tất cả lớp của giáo viên trong học kỳ
        List<Course> courses = courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId);
        
        for (Course course : courses) {
            List<Exam> exams = examRepository.findByClassId(course.getId());
            for (Exam exam : exams) {
                Subject subject = subjectRepository.findById(course.getSubjectId()).orElse(new Subject());
                
                // Đếm số sinh viên trong lớp
                long studentCount = classStudentRepository.countByClassId(course.getId());
                
                TeacherScheduleResponse.ScheduleItemResponse examItem = new TeacherScheduleResponse.ScheduleItemResponse(
                        course.getClassCode(),
                        subject.getSubjectName(),
                        String.valueOf(studentCount),
                        exam.getExamTime(),
                        null,
                        exam.getRoom(),
                        "exam"
                );
                
                examMap.computeIfAbsent(exam.getExamDate(), k -> new ArrayList<>()).add(examItem);
            }
        }
        
        // Chuyển đổi thành danh sách
        List<TeacherScheduleResponse> examSchedule = new ArrayList<>();
        for (Map.Entry<LocalDate, List<TeacherScheduleResponse.ScheduleItemResponse>> entry : examMap.entrySet()) {
            List<TeacherScheduleResponse.ScheduleItemResponse> items = entry.getValue();
            items.sort(Comparator.comparing(TeacherScheduleResponse.ScheduleItemResponse::getStartTime));
            examSchedule.add(new TeacherScheduleResponse(entry.getKey(), items));
        }
        
        examSchedule.sort(Comparator.comparing(TeacherScheduleResponse::getDate));
        return examSchedule;
    }
}
