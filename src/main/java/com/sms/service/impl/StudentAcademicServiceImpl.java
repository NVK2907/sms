package com.sms.service.impl;

import com.sms.dto.request.ClassRegistrationRequest;
import com.sms.dto.response.StudentClassResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.StudentAcademicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentAcademicServiceImpl implements StudentAcademicService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentClassResponse> getAvailableClasses(Long studentId) {
        // Lấy tất cả lớp học
        List<Course> allClasses = courseRepository.findAll();
        
        // Lấy danh sách lớp đã đăng ký
        List<ClassStudent> registeredClasses = classStudentRepository.findByStudentId(studentId);
        List<Long> registeredClassIds = registeredClasses.stream()
                .map(ClassStudent::getClassId)
                .collect(Collectors.toList());
        
        // Lọc ra các lớp chưa đăng ký
        return allClasses.stream()
                .filter(clazz -> !registeredClassIds.contains(clazz.getId()))
                .map(this::convertToStudentClassResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentClassResponse> getRegisteredClasses(Long studentId) {
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        
        return classStudents.stream()
                .map(classStudent -> {
                    Course clazz = courseRepository.findById(classStudent.getClassId())
                            .orElse(new Course());
                    return convertToStudentClassResponse(clazz, true);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentClassResponse> getRegisteredClassesBySemester(Long studentId, Long semesterId) {
        List<ClassStudent> classStudents = classStudentRepository.findByStudentId(studentId);
        
        return classStudents.stream()
                .map(classStudent -> {
                    Course clazz = courseRepository.findById(classStudent.getClassId())
                            .orElse(new Course());
                    return clazz;
                })
                .filter(clazz -> clazz.getSemesterId().equals(semesterId))
                .map(clazz -> convertToStudentClassResponse(clazz, true))
                .collect(Collectors.toList());
    }
    
    @Override
    public void registerForClass(ClassRegistrationRequest request) {
        // Kiểm tra sinh viên tồn tại
        studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên với ID: " + request.getStudentId()));
        
        // Kiểm tra lớp tồn tại
        Course clazz = courseRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + request.getClassId()));
        
        // Kiểm tra đã đăng ký chưa
        if (classStudentRepository.findByClassIdAndStudentId(request.getClassId(), request.getStudentId()).isPresent()) {
            throw new RuntimeException("Sinh viên đã đăng ký lớp này");
        }
        
        // Kiểm tra lớp còn chỗ không
        Long currentStudentCount = classStudentRepository.countByClassId(request.getClassId());
        if (currentStudentCount >= clazz.getMaxStudent()) {
            throw new RuntimeException("Lớp đã đầy, không thể đăng ký");
        }
        
        // Đăng ký lớp
        ClassStudent classStudent = new ClassStudent();
        classStudent.setClassId(request.getClassId());
        classStudent.setStudentId(request.getStudentId());
        
        classStudentRepository.save(classStudent);
    }
    
    @Override
    public void unregisterFromClass(Long studentId, Long classId) {
        // Kiểm tra sinh viên đã đăng ký lớp này chưa
        ClassStudent classStudent = classStudentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Sinh viên chưa đăng ký lớp này"));
        
        classStudentRepository.delete(classStudent);
    }
    
    private StudentClassResponse convertToStudentClassResponse(Course clazz) {
        return convertToStudentClassResponse(clazz, false);
    }
    
    private StudentClassResponse convertToStudentClassResponse(Course clazz, boolean isRegistered) {
        Subject subject = subjectRepository.findById(clazz.getSubjectId())
                .orElse(new Subject());
        
        Semester semester = semesterRepository.findById(clazz.getSemesterId())
                .orElse(new Semester());
        
        String teacherName = null;
        if (clazz.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(clazz.getTeacherId()).orElse(null);
            if (teacher != null) {
                User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                teacherName = teacherUser.getFullName();
            }
        }
        
        Long currentStudentCount = classStudentRepository.countByClassId(clazz.getId());
        
        // Lấy lịch học
        List<Schedule> schedules = scheduleRepository.findByClassId(clazz.getId());
        List<StudentClassResponse.ScheduleResponse> scheduleResponses = schedules.stream()
                .map(schedule -> new StudentClassResponse.ScheduleResponse(
                        schedule.getDayOfWeek(),
                        schedule.getStartTime() != null ? schedule.getStartTime().toString() : null,
                        schedule.getEndTime() != null ? schedule.getEndTime().toString() : null,
                        schedule.getRoom()
                ))
                .collect(Collectors.toList());
        
        return new StudentClassResponse(
                clazz.getId(),
                clazz.getClassCode(),
                subject.getSubjectName(),
                subject.getSubjectCode(),
                semester.getName(),
                teacherName,
                clazz.getMaxStudent(),
                currentStudentCount.intValue(),
                clazz.getCreatedAt(),
                isRegistered,
                scheduleResponses
        );
    }
}
