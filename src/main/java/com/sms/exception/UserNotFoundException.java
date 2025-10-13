package com.sms.exception;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long userId) {
        super("Không tìm thấy người dùng với ID: " + userId);
    }
}
