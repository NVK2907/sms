package com.sms.service.impl;

import com.sms.dto.response.StudentScheduleResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.StudentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentScheduleServiceImpl implements StudentScheduleService {
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentScheduleResponse> getWeeklySchedule(Long studentId, LocalDate startDate) {
        List<StudentScheduleResponse> weeklySchedule = new ArrayList<>();
        
        // Lấy danh sách lớp đã đăng ký
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        List<Long> classIds = classStudents.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        // Tạo lịch cho 7 ngày
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            String dayOfWeek = getDayOfWeek(date);
            
            List<StudentScheduleResponse.ScheduleItemResponse> scheduleItems = new ArrayList<>();
            
            // Lấy lịch học
            for (Long classId : classIds) {
                List<Schedule> schedules = scheduleRepository.findByClassIdAndDayOfWeek(classId, dayOfWeek);
                for (Schedule schedule : schedules) {
                    Course clazz = courseRepository.findById(classId).orElse(new Course());
                    Subject subject = subjectRepository.findById(clazz.getSubjectId()).orElse(new Subject());
                    
                    String teacherName = null;
                    if (clazz.getTeacherId() != null) {
                        Teacher teacher = teacherRepository.findById(clazz.getTeacherId()).orElse(null);
                        if (teacher != null) {
                            User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                            teacherName = teacherUser.getFullName();
                        }
                    }
                    
                    scheduleItems.add(new StudentScheduleResponse.ScheduleItemResponse(
                            clazz.getClassCode(),
                            subject.getSubjectName(),
                            teacherName,
                            schedule.getStartTime(),
                            schedule.getEndTime(),
                            schedule.getRoom(),
                            "class"
                    ));
                }
            }
            
            // Lấy lịch thi
            for (Long classId : classIds) {
                List<Exam> exams = examRepository.findByClassIdAndExamDate(classId, date);
                for (Exam exam : exams) {
                    Course clazz = courseRepository.findById(classId).orElse(new Course());
                    Subject subject = subjectRepository.findById(clazz.getSubjectId()).orElse(new Subject());
                    
                    String teacherName = null;
                    if (clazz.getTeacherId() != null) {
                        Teacher teacher = teacherRepository.findById(clazz.getTeacherId()).orElse(null);
                        if (teacher != null) {
                            User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                            teacherName = teacherUser.getFullName();
                        }
                    }
                    
                    scheduleItems.add(new StudentScheduleResponse.ScheduleItemResponse(
                            clazz.getClassCode(),
                            subject.getSubjectName(),
                            teacherName,
                            exam.getExamTime(),
                            null,
                            exam.getRoom(),
                            "exam"
                    ));
                }
            }
            
            // Sắp xếp theo thời gian
            scheduleItems.sort(Comparator.comparing(StudentScheduleResponse.ScheduleItemResponse::getStartTime));
            
            weeklySchedule.add(new StudentScheduleResponse(date, scheduleItems));
        }
        
        return weeklySchedule;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentScheduleResponse> getDailySchedule(Long studentId, LocalDate date) {
        return getWeeklySchedule(studentId, date).stream()
                .filter(schedule -> schedule.getDate().equals(date))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentScheduleResponse> getExamSchedule(Long studentId, Long semesterId) {
        List<StudentScheduleResponse> examSchedule = new ArrayList<>();
        
        // Lấy danh sách lớp đã đăng ký trong học kỳ
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        List<Long> classIds = classStudents.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        // Lấy tất cả lịch thi
        Map<LocalDate, List<StudentScheduleResponse.ScheduleItemResponse>> examMap = new HashMap<>();
        
        for (Long classId : classIds) {
            Course clazz = courseRepository.findById(classId).orElse(new Course());
            
            // Chỉ lấy lớp trong học kỳ được chỉ định
            if (!clazz.getSemesterId().equals(semesterId)) {
                continue;
            }
            
            List<Exam> exams = examRepository.findByClassId(classId);
            for (Exam exam : exams) {
                Subject subject = subjectRepository.findById(clazz.getSubjectId()).orElse(new Subject());
                
                String teacherName = null;
                if (clazz.getTeacherId() != null) {
                    Teacher teacher = teacherRepository.findById(clazz.getTeacherId()).orElse(null);
                    if (teacher != null) {
                        User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                        teacherName = teacherUser.getFullName();
                    }
                }
                
                StudentScheduleResponse.ScheduleItemResponse examItem = new StudentScheduleResponse.ScheduleItemResponse(
                        clazz.getClassCode(),
                        subject.getSubjectName(),
                        teacherName,
                        exam.getExamTime(),
                        null,
                        exam.getRoom(),
                        "exam"
                );
                
                examMap.computeIfAbsent(exam.getExamDate(), k -> new ArrayList<>()).add(examItem);
            }
        }
        
        // Chuyển đổi thành danh sách
        for (Map.Entry<LocalDate, List<StudentScheduleResponse.ScheduleItemResponse>> entry : examMap.entrySet()) {
            entry.getValue().sort(Comparator.comparing(StudentScheduleResponse.ScheduleItemResponse::getStartTime));
            examSchedule.add(new StudentScheduleResponse(entry.getKey(), entry.getValue()));
        }
        
        // Sắp xếp theo ngày
        examSchedule.sort(Comparator.comparing(StudentScheduleResponse::getDate));
        
        return examSchedule;
    }
    
    private String getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().name();
    }
}
