package com.sms.exception;

public class StudentAlreadyExistsException extends RuntimeException {
    
    public StudentAlreadyExistsException(String message) {
        super(message);
    }
    
    public StudentAlreadyExistsException(String field, String value) {
        super(field + " '" + value + "' đã tồn tại");
    }
}
