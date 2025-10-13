package com.sms.repository;

import com.sms.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    List<Exam> findByClassId(Long classId);
    
    List<Exam> findByExamDate(LocalDate examDate);
    
    @Query("SELECT e FROM Exam e WHERE e.classId = :classId AND e.examDate = :examDate")
    List<Exam> findByClassIdAndExamDate(@Param("classId") Long classId, @Param("examDate") LocalDate examDate);
    
    @Query("SELECT e FROM Exam e WHERE e.room = :room")
    List<Exam> findByRoom(@Param("room") String room);
    
    @Query("SELECT e FROM Exam e WHERE e.room = :room AND e.examDate = :examDate")
    List<Exam> findByRoomAndExamDate(@Param("room") String room, @Param("examDate") LocalDate examDate);
    
    @Query("SELECT e FROM Exam e WHERE e.examDate >= :startDate AND e.examDate <= :endDate")
    List<Exam> findByExamDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
