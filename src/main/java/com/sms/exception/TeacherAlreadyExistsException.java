package com.sms.exception;

public class TeacherAlreadyExistsException extends RuntimeException {
    
    public TeacherAlreadyExistsException(String message) {
        super(message);
    }
    
    public TeacherAlreadyExistsException(String field, String value) {
        super(field + " '" + value + "' đã tồn tại");
    }
}
