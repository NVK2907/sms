package com.sms.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String field, String value) {
        super(field + " '" + value + "' đã tồn tại");
    }
}
