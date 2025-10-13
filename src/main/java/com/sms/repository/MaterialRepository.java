package com.sms.repository;

import com.sms.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    List<Material> findByClassId(Long classId);
    
    List<Material> findByTeacherId(Long teacherId);
    
    @Query("SELECT m FROM Material m WHERE m.classId = :classId AND m.teacherId = :teacherId")
    List<Material> findByClassIdAndTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);
    
    @Query("SELECT m FROM Material m WHERE m.title LIKE %:title%")
    List<Material> findByTitleContaining(@Param("title") String title);
    
    @Query("SELECT COUNT(m) FROM Material m WHERE m.classId = :classId")
    Long countByClassId(@Param("classId") Long classId);
}
