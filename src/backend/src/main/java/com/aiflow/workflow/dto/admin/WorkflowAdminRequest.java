package com.aiflow.workflow.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class WorkflowAdminRequest {

    @NotBlank(message = "工作流名称不能为空")
    private String name;

    private String description;

    private String category;

    private String iconUrl;

    private Map<String, Object> inputSchema;

    private Map<String, Object> outputSchema;

    private Long pointsCost;

    private Integer status;
}
