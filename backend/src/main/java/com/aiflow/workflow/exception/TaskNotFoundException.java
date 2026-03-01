package com.aiflow.workflow.exception;

/**
 * 任务不存在异常
 */
public class TaskNotFoundException extends BusinessException {

    public TaskNotFoundException(String taskId) {
        super(404, "任务不存在: " + taskId);
    }
}
