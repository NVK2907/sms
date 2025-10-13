package com.sms.exception;

public class PasswordMismatchException extends RuntimeException {
    
    public PasswordMismatchException(String message) {
        super(message);
    }
    
    public PasswordMismatchException() {
        super("Mật khẩu xác nhận không khớp");
    }
}
