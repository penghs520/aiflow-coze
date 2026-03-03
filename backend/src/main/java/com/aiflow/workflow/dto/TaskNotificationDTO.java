package com.aiflow.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务通知消息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNotificationDTO {

    /**
     * 通知类型：TASK_COMPLETED, TASK_FAILED
     */
    private String type;

    /**
     * 任务 ID
     */
    private String taskId;

    /**
     * 工作流 ID
     */
    private String workflowId;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 任务状态：3-已完成，4-失败
     */
    private Integer status;

    /**
     * 进度
     */
    private Integer progress;

    /**
     * 执行结果（JSON 字符串）
     */
    private String result;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 消耗资源点
     */
    private Integer actualPoints;

    /**
     * 通知时间
     */
    private LocalDateTime notificationTime;
}
