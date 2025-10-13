package com.sms.exception;

public class StudentNotFoundException extends RuntimeException {
    
    public StudentNotFoundException(String message) {
        super(message);
    }
    
    public StudentNotFoundException(Long studentId) {
        super("Không tìm thấy sinh viên với ID: " + studentId);
    }
}
