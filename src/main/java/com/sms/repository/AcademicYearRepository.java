package com.sms.repository;

import com.sms.entity.AcademicYear;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    
    Optional<AcademicYear> findByName(String name);
    
    @Query("SELECT ay FROM AcademicYear ay WHERE :date BETWEEN ay.startDate AND ay.endDate")
    Optional<AcademicYear> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT ay FROM AcademicYear ay WHERE ay.startDate <= :date AND ay.endDate >= :date")
    List<AcademicYear> findActiveAcademicYears(@Param("date") LocalDate date);
    
    boolean existsByName(String name);
    
    @Query("SELECT ay FROM AcademicYear ay ORDER BY ay.startDate DESC")
    Page<AcademicYear> findAllOrderByStartDateDesc(Pageable pageable);
}
