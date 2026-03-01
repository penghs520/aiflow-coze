package com.aiflow.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 创建任务请求
 */
@Data
public class CreateTaskRequest {

    /**
     * 工作流ID
     */
    @NotBlank(message = "工作流ID不能为空")
    private String workflowId;

    /**
     * 任务参数
     */
    @NotNull(message = "任务参数不能为空")
    private Map<String, Object> parameters;

    /**
     * 任务设置
     */
    private Map<String, Object> settings;
}
