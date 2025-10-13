package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "status", length = 10)
    private String status = "present";
    
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    
    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
