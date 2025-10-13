package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_code", unique = true, nullable = false, length = 20)
    private String classCode;
    
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    
    @Column(name = "semester_id", nullable = false)
    private Long semesterId;
    
    @Column(name = "teacher_id")
    private Long teacherId;
    
    @Column(name = "max_student")
    private Integer maxStudent = 50;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
