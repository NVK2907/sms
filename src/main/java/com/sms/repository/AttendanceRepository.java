package com.sms.repository;

import com.sms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByClassId(Long classId);
    
    List<Attendance> findByStudentId(Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.classId = :classId AND a.studentId = :studentId")
    List<Attendance> findByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.classId = :classId AND a.attendanceDate = :date")
    List<Attendance> findByClassIdAndDate(@Param("classId") Long classId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.classId = :classId AND a.studentId = :studentId AND a.attendanceDate = :date")
    Optional<Attendance> findByClassIdAndStudentIdAndDate(@Param("classId") Long classId, @Param("studentId") Long studentId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.classId = :classId AND a.attendanceDate = :date AND a.status = 'present'")
    Long countPresentByClassIdAndDate(@Param("classId") Long classId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.classId = :classId AND a.attendanceDate = :date")
    Long countTotalByClassIdAndDate(@Param("classId") Long classId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.classId = :classId AND a.status = 'present'")
    Long countPresentByStudentIdAndClassId(@Param("studentId") Long studentId, @Param("classId") Long classId);
    
    @Query("SELECT a FROM Attendance a WHERE a.classId = :classId AND a.attendanceDate = :attendanceDate")
    List<Attendance> findByClassIdAndAttendanceDate(@Param("classId") Long classId, @Param("attendanceDate") LocalDate attendanceDate);
    
    void deleteByClassIdAndAttendanceDate(Long classId, LocalDate attendanceDate);
}
