package com.sms.repository;

import com.sms.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByAssignmentId(Long assignmentId);
    
    List<Submission> findByStudentId(Long studentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignmentId = :assignmentId AND s.studentId = :studentId")
    Optional<Submission> findByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignmentId = :assignmentId")
    Long countByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignmentId = :assignmentId AND s.score IS NOT NULL")
    Long countGradedSubmissionsByAssignmentId(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT AVG(s.score) FROM Submission s WHERE s.assignmentId = :assignmentId AND s.score IS NOT NULL")
    Double getAverageScoreByAssignmentId(@Param("assignmentId") Long assignmentId);
}
