package com.sms.repository;

import com.sms.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("SELECT s FROM Subject s WHERE " +
           "(:keyword IS NULL OR LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Subject> findSubjectsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsBySubjectCode(String subjectCode);
}
