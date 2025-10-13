package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "midterm")
    private Float midterm;
    
    @Column(name = "final")
    private Float finalGrade;
    
    @Column(name = "other")
    private Float other;
    
    @Column(name = "total")
    private Float total;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
