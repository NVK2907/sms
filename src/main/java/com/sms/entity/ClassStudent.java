package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
    
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
}
