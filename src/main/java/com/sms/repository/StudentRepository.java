package com.sms.repository;

import com.sms.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentCode(String studentCode);
    
    Optional<Student> findByUserId(Long userId);
    
    List<Student> findByClassName(String className);
    
    List<Student> findByMajor(String major);
    
    List<Student> findByCourseYear(Integer courseYear);
    
    @Query("SELECT s FROM Student s WHERE s.className = :className AND s.major = :major")
    List<Student> findByClassNameAndMajor(@Param("className") String className, @Param("major") String major);
    
    boolean existsByStudentCode(String studentCode);
    
    boolean existsByUserId(Long userId);
    
    @Query("SELECT s FROM Student s WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.major) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:className IS NULL OR :className = '' OR s.className = :className) AND " +
           "(:major IS NULL OR :major = '' OR s.major = :major) AND " +
           "(:courseYear IS NULL OR s.courseYear = :courseYear) AND " +
           "(:gender IS NULL OR :gender = '' OR s.gender = :gender)")
    Page<Student> searchStudents(@Param("keyword") String keyword,
                                @Param("className") String className,
                                @Param("major") String major,
                                @Param("courseYear") Integer courseYear,
                                @Param("gender") String gender,
                                Pageable pageable);
}
