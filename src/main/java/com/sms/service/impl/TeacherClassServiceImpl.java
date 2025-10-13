package com.sms.service.impl;

import com.sms.dto.response.TeacherClassResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.TeacherClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherClassServiceImpl implements TeacherClassService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getClassesByTeacher(Long teacherId) {
        List<Course> classes = courseRepository.findByTeacherId(teacherId);
        return classes.stream()
                .map(this::convertToTeacherClassResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getClassesByTeacherAndSemester(Long teacherId, Long semesterId) {
        List<Course> classes = courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId);
        return classes.stream()
                .map(this::convertToTeacherClassResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TeacherClassResponse getClassWithStudents(Long classId) {
        Course classEntity = courseRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        return convertToTeacherClassResponse(classEntity);
    }
    
    private TeacherClassResponse convertToTeacherClassResponse(Course classEntity) {
        Subject subject = subjectRepository.findById(classEntity.getSubjectId())
                .orElse(new Subject());
        
        Semester semester = semesterRepository.findById(classEntity.getSemesterId())
                .orElse(new Semester());
        
        Long currentStudentCount = classStudentRepository.countByClassId(classEntity.getId());
        
        List<ClassStudent> classStudents = classStudentRepository.findByClassId(classEntity.getId());
        List<TeacherClassResponse.StudentInClassResponse> students = classStudents.stream()
                .map(classStudent -> {
                    Student student = studentRepository.findById(classStudent.getStudentId())
                            .orElse(new Student());
                    User user = userRepository.findById(student.getUserId())
                            .orElse(new User());
                    
                    return new TeacherClassResponse.StudentInClassResponse(
                            student.getId(),
                            student.getStudentCode(),
                            user.getFullName(),
                            user.getEmail(),
                            student.getClassName(),
                            student.getMajor(),
                            student.getCourseYear()
                    );
                })
                .collect(Collectors.toList());
        
        return new TeacherClassResponse(
                classEntity.getId(),
                classEntity.getClassCode(),
                subject.getSubjectName(),
                semester.getName(),
                classEntity.getMaxStudent(),
                currentStudentCount.intValue(),
                classEntity.getCreatedAt(),
                students
        );
    }
}
