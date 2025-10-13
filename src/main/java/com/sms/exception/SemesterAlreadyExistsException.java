package com.sms.exception;

public class SemesterAlreadyExistsException extends RuntimeException {
    
    public SemesterAlreadyExistsException(String message) {
        super(message);
    }
    
    public SemesterAlreadyExistsException(String name, Long academicYearId) {
        super("Tên học kỳ đã tồn tại trong năm học này: " + name);
    }
}
