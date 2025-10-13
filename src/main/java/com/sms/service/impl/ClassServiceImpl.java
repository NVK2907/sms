package com.sms.service.impl;

import com.sms.dto.request.ClassRequest;
import com.sms.dto.response.ClassListResponse;
import com.sms.dto.response.ClassResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassServiceImpl implements ClassService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SemesterRepository semesterRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private ClassStudentRepository classStudentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public ClassResponse createClass(ClassRequest classRequest) {
        // Kiểm tra mã lớp đã tồn tại
        if (courseRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new RuntimeException("Mã lớp đã tồn tại: " + classRequest.getClassCode());
        }
        
        // Kiểm tra môn học tồn tại
        subjectRepository.findById(classRequest.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + classRequest.getSubjectId()));
        
        // Kiểm tra học kỳ tồn tại
        semesterRepository.findById(classRequest.getSemesterId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + classRequest.getSemesterId()));
        
        // Kiểm tra giáo viên tồn tại (nếu có)
        if (classRequest.getTeacherId() != null) {
            teacherRepository.findById(classRequest.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + classRequest.getTeacherId()));
        }
        
        Course courseEntity = new Course();
        courseEntity.setClassCode(classRequest.getClassCode());
        courseEntity.setSubjectId(classRequest.getSubjectId());
        courseEntity.setSemesterId(classRequest.getSemesterId());
        courseEntity.setTeacherId(classRequest.getTeacherId());
        courseEntity.setMaxStudent(classRequest.getMaxStudent());
        
        Course savedCourse = courseRepository.save(courseEntity);
        return convertToClassResponse(savedCourse);
    }
    
    @Override
    public ClassResponse updateClass(Long classId, ClassRequest classRequest) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        // Kiểm tra mã lớp đã tồn tại (nếu có thay đổi)
        if (!classRequest.getClassCode().equals(courseEntity.getClassCode()) 
            && courseRepository.existsByClassCode(classRequest.getClassCode())) {
            throw new RuntimeException("Mã lớp đã tồn tại: " + classRequest.getClassCode());
        }
        
        // Kiểm tra môn học tồn tại
        subjectRepository.findById(classRequest.getSubjectId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + classRequest.getSubjectId()));
        
        // Kiểm tra học kỳ tồn tại
        semesterRepository.findById(classRequest.getSemesterId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + classRequest.getSemesterId()));
        
        // Kiểm tra giáo viên tồn tại (nếu có)
        if (classRequest.getTeacherId() != null) {
            teacherRepository.findById(classRequest.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + classRequest.getTeacherId()));
        }
        
        courseEntity.setClassCode(classRequest.getClassCode());
        courseEntity.setSubjectId(classRequest.getSubjectId());
        courseEntity.setSemesterId(classRequest.getSemesterId());
        courseEntity.setTeacherId(classRequest.getTeacherId());
        courseEntity.setMaxStudent(classRequest.getMaxStudent());
        
        Course updatedCourse = courseRepository.save(courseEntity);
        return convertToClassResponse(updatedCourse);
    }
    
    @Override
    public void deleteClass(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        // Xóa tất cả class_student records
        classStudentRepository.deleteByClassId(classId);
        
        // Xóa lớp
        courseRepository.delete(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        return convertToClassResponse(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassByCode(String classCode) {
        Course courseEntity = courseRepository.findByClassCode(classCode)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với mã: " + classCode));
        
        return convertToClassResponse(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClassListResponse getAllClasses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        
        List<ClassResponse> classResponses = coursePage.getContent().stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
        
        return new ClassListResponse(
            classResponses,
            coursePage.getTotalElements(),
            coursePage.getTotalPages(),
            coursePage.getNumber(),
            coursePage.getSize()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySubject(Long subjectId) {
        subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        List<Course> courses = courseRepository.findBySubjectId(subjectId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySemester(Long semesterId) {
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findBySemesterId(semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByTeacher(Long teacherId) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        List<Course> courses = courseRepository.findByTeacherId(teacherId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesBySubjectAndSemester(Long subjectId, Long semesterId) {
        subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + subjectId));
        
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findBySubjectIdAndSemesterId(subjectId, semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByTeacherAndSemester(Long teacherId, Long semesterId) {
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        semesterRepository.findById(semesterId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ với ID: " + semesterId));
        
        List<Course> courses = courseRepository.findByTeacherIdAndSemesterId(teacherId, semesterId);
        
        return courses.stream()
            .map(this::convertToClassResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void assignTeacherToClass(Long classId, Long teacherId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên với ID: " + teacherId));
        
        courseEntity.setTeacherId(teacherId);
        courseRepository.save(courseEntity);
    }
    
    @Override
    public void removeTeacherFromClass(Long classId) {
        Course courseEntity = courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        courseEntity.setTeacherId(null);
        courseRepository.save(courseEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassResponse.StudentInfo> getClassStudents(Long classId) {
        courseRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp với ID: " + classId));
        
        List<ClassStudent> classStudents = classStudentRepository.findByClassId(classId);
        
        return classStudents.stream()
            .map(classStudent -> {
                Student student = studentRepository.findById(classStudent.getStudentId())
                    .orElse(new Student());
                User user = userRepository.findById(student.getUserId())
                    .orElse(new User());
                
                return new ClassResponse.StudentInfo(
                    student.getId(),
                    student.getStudentCode(),
                    user.getFullName(),
                    classStudent.getRegisteredAt()
                );
            })
            .collect(Collectors.toList());
    }
    
    private ClassResponse convertToClassResponse(Course courseEntity) {
        Subject subject = subjectRepository.findById(courseEntity.getSubjectId())
            .orElse(new Subject());
        
        Semester semester = semesterRepository.findById(courseEntity.getSemesterId())
            .orElse(new Semester());
        
        Teacher teacher = null;
        String teacherName = null;
        if (courseEntity.getTeacherId() != null) {
            teacher = teacherRepository.findById(courseEntity.getTeacherId()).orElse(null);
            if (teacher != null) {
                User teacherUser = userRepository.findById(teacher.getUserId()).orElse(new User());
                teacherName = teacherUser.getFullName();
            }
        }
        
        Long currentStudentCount = classStudentRepository.countByClassId(courseEntity.getId());
        List<ClassResponse.StudentInfo> students = getClassStudents(courseEntity.getId());
        
        return new ClassResponse(
            courseEntity.getId(),
            courseEntity.getClassCode(),
            courseEntity.getSubjectId(),
            subject.getSubjectName(),
            subject.getSubjectCode(),
            courseEntity.getSemesterId(),
            semester.getName(),
            courseEntity.getTeacherId(),
            teacherName,
            courseEntity.getMaxStudent(),
            currentStudentCount.intValue(),
            courseEntity.getCreatedAt(),
            students
        );
    }
}
