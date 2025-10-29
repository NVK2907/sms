package com.sms.repository;

import com.sms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByClassCode(String classCode);
    
    List<Course> findBySubjectId(Long subjectId);
    
    List<Course> findBySemesterId(Long semesterId);
    
    List<Course> findByTeacherId(Long teacherId);
    
    @Query("SELECT c FROM Course c WHERE c.subjectId = :subjectId AND c.semesterId = :semesterId")
    List<Course> findBySubjectIdAndSemesterId(@Param("subjectId") Long subjectId, @Param("semesterId") Long semesterId);
    
    @Query("SELECT c FROM Course c WHERE c.teacherId = :teacherId AND c.semesterId = :semesterId")
    List<Course> findByTeacherIdAndSemesterId(@Param("teacherId") Long teacherId, @Param("semesterId") Long semesterId);
    
    boolean existsByClassCode(String classCode);
    
    @Query("SELECT c FROM Course c " +
           "LEFT JOIN Subject s ON c.subjectId = s.id " +
           "LEFT JOIN Semester sem ON c.semesterId = sem.id " +
           "LEFT JOIN Teacher t ON c.teacherId = t.id " +
           "LEFT JOIN User u ON t.userId = u.id " +
           "WHERE (:keyword IS NULL OR :keyword = '' OR " +
           "       LOWER(c.classCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(sem.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchClasses(@Param("keyword") String keyword, Pageable pageable);
}
