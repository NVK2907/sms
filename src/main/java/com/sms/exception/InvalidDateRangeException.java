package com.sms.exception;

public class InvalidDateRangeException extends RuntimeException {
    
    public InvalidDateRangeException(String message) {
        super(message);
    }
    
    public InvalidDateRangeException() {
        super("Ngày bắt đầu phải trước ngày kết thúc");
    }
}
