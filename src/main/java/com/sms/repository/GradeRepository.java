package com.sms.repository;

import com.sms.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    List<Grade> findByClassId(Long classId);
    
    List<Grade> findByStudentId(Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.classId = :classId AND g.studentId = :studentId")
    Optional<Grade> findByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.classId = :classId ORDER BY g.total DESC")
    List<Grade> findByClassIdOrderByTotalDesc(@Param("classId") Long classId);
    
    @Query("SELECT AVG(g.total) FROM Grade g WHERE g.classId = :classId")
    Double getAverageGradeByClassId(@Param("classId") Long classId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.classId = :classId")
    Long countByClassId(@Param("classId") Long classId);
}
