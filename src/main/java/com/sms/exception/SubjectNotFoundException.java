package com.sms.exception;

public class SubjectNotFoundException extends RuntimeException {
    
    public SubjectNotFoundException(String message) {
        super(message);
    }
    
    public SubjectNotFoundException(Long subjectId) {
        super("Không tìm thấy môn học với ID: " + subjectId);
    }
}
