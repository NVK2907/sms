package com.sms.exception;

public class AcademicYearAlreadyExistsException extends RuntimeException {
    
    public AcademicYearAlreadyExistsException(String message) {
        super(message);
    }
    
    public AcademicYearAlreadyExistsException(String name, boolean isName) {
        super("Tên năm học đã tồn tại: " + name);
    }
}
