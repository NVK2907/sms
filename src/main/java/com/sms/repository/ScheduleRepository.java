package com.sms.repository;

import com.sms.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByClassId(Long classId);
    
    List<Schedule> findByDayOfWeek(String dayOfWeek);
    
    @Query("SELECT s FROM Schedule s WHERE s.classId = :classId AND s.dayOfWeek = :dayOfWeek")
    List<Schedule> findByClassIdAndDayOfWeek(@Param("classId") Long classId, @Param("dayOfWeek") String dayOfWeek);
    
    @Query("SELECT s FROM Schedule s WHERE s.room = :room")
    List<Schedule> findByRoom(@Param("room") String room);
    
    @Query("SELECT s FROM Schedule s WHERE s.room = :room AND s.dayOfWeek = :dayOfWeek")
    List<Schedule> findByRoomAndDayOfWeek(@Param("room") String room, @Param("dayOfWeek") String dayOfWeek);
    
    @Query("SELECT s FROM Schedule s JOIN Course c ON s.classId = c.id WHERE c.teacherId = :teacherId AND s.dayOfWeek = :dayOfWeek")
    List<Schedule> findByTeacherIdAndDayOfWeek(@Param("teacherId") Long teacherId, @Param("dayOfWeek") Integer dayOfWeek);
}
