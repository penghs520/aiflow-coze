package com.aiflow.workflow.exception;

/**
 * 工作流不存在异常
 */
public class WorkflowNotFoundException extends BusinessException {

    public WorkflowNotFoundException(String workflowId) {
        super(404, "工作流不存在: " + workflowId);
    }
}
