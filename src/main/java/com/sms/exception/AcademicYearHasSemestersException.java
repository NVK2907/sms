package com.sms.exception;

public class AcademicYearHasSemestersException extends RuntimeException {
    
    public AcademicYearHasSemestersException(String message) {
        super(message);
    }
    
    public AcademicYearHasSemestersException() {
        super("Không thể xóa năm học vì còn học kỳ thuộc năm học này");
    }
}
