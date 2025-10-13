package com.sms.exception;

public class TeacherNotFoundException extends RuntimeException {
    
    public TeacherNotFoundException(String message) {
        super(message);
    }
    
    public TeacherNotFoundException(Long teacherId) {
        super("Không tìm thấy giáo viên với ID: " + teacherId);
    }
}
