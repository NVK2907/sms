package com.sms.exception;

public class SubjectAlreadyExistsException extends RuntimeException {
    
    public SubjectAlreadyExistsException(String message) {
        super(message);
    }
    
    public SubjectAlreadyExistsException(String field, String value) {
        super(field + " '" + value + "' đã tồn tại");
    }
}
