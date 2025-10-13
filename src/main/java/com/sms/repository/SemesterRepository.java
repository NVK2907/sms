package com.sms.repository;

import com.sms.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    
    List<Semester> findByAcademicYearId(Long academicYearId);
    
    Optional<Semester> findByAcademicYearIdAndName(Long academicYearId, String name);
    
    List<Semester> findByIsOpenTrue();
    
    @Query("SELECT s FROM Semester s WHERE s.academicYearId = :academicYearId AND s.isOpen = true")
    List<Semester> findOpenSemestersByAcademicYear(@Param("academicYearId") Long academicYearId);
    
    @Query("SELECT s FROM Semester s WHERE :date BETWEEN s.startDate AND s.endDate")
    Optional<Semester> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Semester s ORDER BY s.startDate DESC")
    Page<Semester> findAllOrderByStartDateDesc(Pageable pageable);
    
    @Modifying
    @Query("UPDATE Semester s SET s.isOpen = :isOpen")
    void updateAllRegistrationStatus(@Param("isOpen") Boolean isOpen);
}
