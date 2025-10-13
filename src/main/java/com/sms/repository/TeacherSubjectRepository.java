package com.sms.repository;

import com.sms.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    
    List<TeacherSubject> findByTeacherId(Long teacherId);
    
    List<TeacherSubject> findBySubjectId(Long subjectId);
    
    @Query("SELECT ts FROM TeacherSubject ts WHERE ts.teacherId = :teacherId AND ts.subjectId = :subjectId")
    Optional<TeacherSubject> findByTeacherIdAndSubjectId(@Param("teacherId") Long teacherId, @Param("subjectId") Long subjectId);
    
    void deleteByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    
    void deleteByTeacherId(Long teacherId);
    
    void deleteBySubjectId(Long subjectId);
}
