package com.sms.exception;

public class AcademicYearNotFoundException extends RuntimeException {
    
    public AcademicYearNotFoundException(String message) {
        super(message);
    }
    
    public AcademicYearNotFoundException(Long academicYearId) {
        super("Không tìm thấy năm học với ID: " + academicYearId);
    }
}
