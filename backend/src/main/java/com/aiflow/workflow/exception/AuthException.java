package com.aiflow.workflow.exception;

/**
 * 认证异常
 */
public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(401, message);
    }
}
