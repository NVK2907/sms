package com.sms.repository;

import com.sms.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    
    List<ClassStudent> findByClassId(Long classId);
    
    List<ClassStudent> findByStudentId(Long studentId);
    
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.classId = :classId AND cs.studentId = :studentId")
    Optional<ClassStudent> findByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(cs) FROM ClassStudent cs WHERE cs.classId = :classId")
    Long countByClassId(@Param("classId") Long classId);
    
    @Query("SELECT COUNT(cs) FROM ClassStudent cs WHERE cs.studentId = :studentId")
    Long countByStudentId(@Param("studentId") Long studentId);
    
    void deleteByClassIdAndStudentId(Long classId, Long studentId);
    
    void deleteByClassId(Long classId);
    
    void deleteByStudentId(Long studentId);
    
    @Modifying
    @Query(value = "SELECT setval('class_students_id_seq', COALESCE((SELECT MAX(id) FROM class_students), 1), true)", nativeQuery = true)
    void fixSequence();
}
