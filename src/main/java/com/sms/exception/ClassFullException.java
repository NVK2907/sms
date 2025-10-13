package com.sms.exception;

public class ClassFullException extends RuntimeException {
    
    public ClassFullException(String message) {
        super(message);
    }
    
    public ClassFullException(String classCode, boolean isClassCode) {
        super("Lớp " + classCode + " đã đầy");
    }
}
