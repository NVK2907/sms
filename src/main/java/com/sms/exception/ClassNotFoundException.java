package com.sms.exception;

public class ClassNotFoundException extends RuntimeException {
    
    public ClassNotFoundException(String message) {
        super(message);
    }
    
    public ClassNotFoundException(Long classId) {
        super("Không tìm thấy lớp học với ID: " + classId);
    }
}
