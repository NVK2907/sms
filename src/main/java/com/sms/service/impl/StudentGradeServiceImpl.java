package com.sms.service.impl;

import com.sms.dto.response.StudentGradeResponse;
import com.sms.entity.*;
import com.sms.repository.*;
import com.sms.service.StudentGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentGradeServiceImpl implements StudentGradeService {
    
    @Autowired
    private GradeRepository gradeRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentGradeResponse> getGradesByStudent(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .map(this::convertToStudentGradeResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentGradeResponse> getGradesByStudentAndSemester(Long studentId, Long semesterId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        
        return grades.stream()
                .map(grade -> {
                    Course clazz = courseRepository.findById(grade.getClassId())
                            .orElse(new Course());
                    return new Object[]{grade, clazz};
                })
                .filter(obj -> ((Course) obj[1]).getSemesterId().equals(semesterId))
                .map(obj -> convertToStudentGradeResponse((Grade) obj[0]))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentGradeResponse getGradeByClassAndStudent(Long studentId, Long classId) {
        Grade grade = gradeRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm của sinh viên trong lớp này"));
        
        return convertToStudentGradeResponse(grade);
    }
    
    @Override
    @Transactional(readOnly = true)
    public StudentGradeResponse.GPASummary getGPASummary(Long studentId) {
        List<StudentGradeResponse> grades = getGradesByStudent(studentId);
        
        // Tính GPA
        double totalPoints = 0.0;
        int totalCredits = 0;
        int completedCredits = 0;
        
        for (StudentGradeResponse grade : grades) {
            if (grade.getTotal() != null && grade.getCredits() != null) {
                totalCredits += grade.getCredits();
                
                if (grade.getTotal() >= 0) { // Có điểm
                    completedCredits += grade.getCredits();
                    totalPoints += convertToGpaPoints(grade.getTotal()) * grade.getCredits();
                }
            }
        }
        
        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
        
        return new StudentGradeResponse.GPASummary(
                gpa,
                totalCredits,
                completedCredits,
                grades
        );
    }
    
    private StudentGradeResponse convertToStudentGradeResponse(Grade grade) {
        Course clazz = courseRepository.findById(grade.getClassId()).orElse(null);
        Long subjectId = clazz != null ? clazz.getSubjectId() : null;
        Subject subject = subjectId != null
                ? subjectRepository.findById(subjectId).orElse(new Subject())
                : new Subject();
        
        return new StudentGradeResponse(
                grade.getId(),
                grade.getStudentId(),
                clazz.getClassCode(),
                subject.getSubjectName(),
                subject.getSubjectCode(),
                3, // Default credits, có thể cần thêm field credits vào Subject entity
                grade.getMidterm(),
                grade.getFinalGrade(),
                grade.getOther(),
                grade.getTotal(),
                convertToLetterGrade(grade.getTotal()),
                grade.getUpdatedAt()
        );
    }
    
    private String convertToLetterGrade(Float total) {
        if (total == null) {
            return "N/A";
        }
        // Đồng bộ thang điểm chữ với GradeServiceImpl
        if (total >= 9.0) return "A";
        if (total >= 8.0) return "B+";
        if (total >= 7.0) return "B";
        if (total >= 6.5) return "C+";
        if (total >= 5.5) return "C";
        if (total >= 5.0) return "D+";
        if (total >= 4.0) return "D";
        return "F";
    }
    
    private double convertToGpaPoints(Float total) {
        if (total == null) {
            return 0.0;
        }
        
        if (total >= 9.0) return 4.0;
        if (total >= 8.5) return 3.7;
        if (total >= 8.0) return 3.3;
        if (total >= 7.0) return 3.0;
        if (total >= 6.5) return 2.7;
        if (total >= 5.5) return 2.3;
        if (total >= 5.0) return 2.0;
        if (total >= 4.0) return 1.0;
        return 0.0;
    }
}
