package com.sms.repository;

import com.sms.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    Optional<Teacher> findByTeacherCode(String teacherCode);
    
    Optional<Teacher> findByUserId(Long userId);
    
    List<Teacher> findByDepartment(String department);
    
    List<Teacher> findByTitle(String title);
    
    @Query("SELECT t FROM Teacher t WHERE t.department = :department AND t.title = :title")
    List<Teacher> findByDepartmentAndTitle(@Param("department") String department, @Param("title") String title);
    
    @Query("SELECT t FROM Teacher t JOIN User u ON t.userId = u.id WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.teacherCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Teacher> searchTeachers(@Param("keyword") String keyword);
    
    boolean existsByTeacherCode(String teacherCode);
    
    boolean existsByUserId(Long userId);
}
