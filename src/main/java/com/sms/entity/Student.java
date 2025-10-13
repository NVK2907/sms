package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;
    
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "class_name", length = 50)
    private String className;
    
    @Column(name = "major", length = 100)
    private String major;
    
    @Column(name = "course_year")
    private Integer courseYear;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
