package com.sms.repository;

import com.sms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    
    Optional<Subject> findBySubjectCode(String subjectCode);
    
    List<Subject> findBySubjectNameContainingIgnoreCase(String subjectName);
    
    List<Subject> findByCredit(Integer credit);
    
    @Query("SELECT s FROM Subject s WHERE s.credit >= :minCredit AND s.credit <= :maxCredit")
    List<Subject> findByCreditRange(@Param("minCredit") Integer minCredit, @Param("maxCredit") Integer maxCredit);
    
    boolean existsBySubjectCode(String subjectCode);
}
