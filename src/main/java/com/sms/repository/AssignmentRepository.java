package com.sms.repository;

import com.sms.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByClassId(Long classId);
    
    List<Assignment> findByTeacherId(Long teacherId);
    
    @Query("SELECT a FROM Assignment a WHERE a.classId = :classId AND a.teacherId = :teacherId")
    List<Assignment> findByClassIdAndTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);
    
    @Query("SELECT a FROM Assignment a WHERE a.deadline > :currentTime")
    List<Assignment> findActiveAssignments(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT a FROM Assignment a WHERE a.classId = :classId AND a.deadline > :currentTime")
    List<Assignment> findActiveAssignmentsByClassId(@Param("classId") Long classId, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT a FROM Assignment a WHERE a.title LIKE %:title%")
    List<Assignment> findByTitleContaining(@Param("title") String title);
}
