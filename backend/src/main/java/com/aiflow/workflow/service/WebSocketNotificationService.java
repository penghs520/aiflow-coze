package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.TaskNotificationDTO;
import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * WebSocket 通知服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;

    /**
     * 发送任务完成通知
     */
    public void sendTaskCompletionNotification(Task task) {
        try {
            // 查询工作流信息
            Workflow workflow = workflowRepository.findById(task.getWorkflowId())
                    .orElse(null);

            // 查询用户信息
            User user = userRepository.findById(task.getUserId())
                    .orElse(null);

            // 构建通知消息
            TaskNotificationDTO notification = TaskNotificationDTO.builder()
                    .type(task.getStatus() == Task.STATUS_COMPLETED ?
                            "TASK_COMPLETED" : "TASK_FAILED")
                    .taskId(task.getId())
                    .workflowId(task.getWorkflowId())
                    .workflowName(workflow != null ? workflow.getName() : "未知工作流")
                    .userNickname(user != null ? user.getNickname() : "未知用户")
                    .status(task.getStatus())
                    .progress(task.getProgress())
                    .result(task.getResult())
                    .errorMessage(task.getErrorMessage())
                    .completedAt(task.getCompletedAt())
                    .actualPoints(task.getActualPoints())
                    .notificationTime(LocalDateTime.now())
                    .build();

            // 向所有订阅 /topic/task-notifications 的客户端广播
            messagingTemplate.convertAndSend("/topic/task-notifications", notification);

            log.info("发送任务完成通知: taskId={}, status={}, type={}",
                    task.getId(), task.getStatus(), notification.getType());

        } catch (Exception e) {
            log.error("发送 WebSocket 通知失败: taskId={}", task.getId(), e);
        }
    }
}
