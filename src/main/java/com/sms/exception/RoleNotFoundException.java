package com.sms.exception;

public class RoleNotFoundException extends RuntimeException {
    
    public RoleNotFoundException(String message) {
        super(message);
    }
    
    public RoleNotFoundException(Long roleId) {
        super("Không tìm thấy vai trò với ID: " + roleId);
    }
}
