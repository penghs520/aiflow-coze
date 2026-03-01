package com.aiflow.workflow.exception;

/**
 * 资源点不足异常
 */
public class InsufficientPointsException extends BusinessException {

    public InsufficientPointsException(String message) {
        super(400, message);
    }

    public InsufficientPointsException() {
        super(400, "资源点不足");
    }
}
