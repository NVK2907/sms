package com.sms.exception;

public class SemesterNotFoundException extends RuntimeException {
    
    public SemesterNotFoundException(String message) {
        super(message);
    }
    
    public SemesterNotFoundException(Long semesterId) {
        super("Không tìm thấy học kỳ với ID: " + semesterId);
    }
}
