package com.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subject {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "subject_code", unique = true, nullable = false, length = 20)
    private String subjectCode;
    
    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;
    
    @Column(name = "credit", nullable = false)
    private Integer credit;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
