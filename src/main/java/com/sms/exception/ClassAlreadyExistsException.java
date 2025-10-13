package com.sms.exception;

public class ClassAlreadyExistsException extends RuntimeException {
    
    public ClassAlreadyExistsException(String message) {
        super(message);
    }
    
    public ClassAlreadyExistsException(String field, String value) {
        super(field + " '" + value + "' đã tồn tại");
    }
}
