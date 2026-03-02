package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.exception.TaskNotFoundException;
import com.aiflow.workflow.repository.TaskRepository;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.repository.WorkflowRepository;
import com.aiflow.workflow.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 任务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;
    private final CozeService cozeService;
    private final PointService pointService;
    private final WorkflowService workflowService;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    /**
     * 创建任务
     */
    @Transactional
    public Task createTask(Long userId, String workflowId, Map<String, Object> parameters, Map<String, Object> settings) {
        // 查找工作流
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new BusinessException("工作流不存在"));

        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 计算预估资源点
        Integer estimatedPoints = workflow.getBasePoints();

        // 检查资源点余额
        if (user.getPointsBalance() < estimatedPoints) {
            throw new BusinessException("资源点不足");
        }

        // 自动填充 mihe_key 参数（如果参数中存在 mihe_key 且为空字符串）
        if (parameters.containsKey("mihe_key")) {
            Object miheKeyValue = parameters.get("mihe_key");
            // 如果 mihe_key 为空字符串或 null，则从配置中填充
            if (miheKeyValue == null || (miheKeyValue instanceof String && ((String) miheKeyValue).isEmpty())) {
                String miheKey = cozeService.getMiheKey();
                if (miheKey != null && !miheKey.isEmpty()) {
                    parameters.put("mihe_key", miheKey);
                    log.info("自动填充 mihe_key 参数到工作流执行: workflowId={}", workflowId);
                } else {
                    log.warn("工作流需要 mihe_key 参数，但后端未配置 COZE_MIHE_KEY: workflowId={}", workflowId);
                }
            }
        }

        // 创建任务
        Task task = Task.builder()
                .id(String.valueOf(idGenerator.nextId()))
                .workflowId(workflowId)
                .userId(userId)
                .status(0) // 待提交
                .progress(0)
                .estimatedPoints(estimatedPoints)
                .build();

        try {
            task.setParameters(objectMapper.writeValueAsString(parameters));
        } catch (Exception e) {
            throw new BusinessException("参数序列化失败");
        }

        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);

        // 提交到扣子平台
        submitTaskToCoze(task);

        return task;
    }

    /**
     * 提交任务到扣子平台
     */
    @Transactional
    public void submitTaskToCoze(Task task) {
        try {
            // 解析参数
            Map<String, Object> parameters = objectMapper.readValue(task.getParameters(), Map.class);

            // 查询工作流获取cozeWorkflowId
            Workflow workflow = workflowRepository.findById(task.getWorkflowId())
                    .orElseThrow(() -> new BusinessException("工作流不存在"));

            // 调用扣子API
            CozeService.CozeExecuteResponse response = cozeService.executeWorkflow(
                    workflow.getCozeWorkflowId(),
                    parameters
            );

            // 更新任务状态
            task.setCozeTaskId(response.getExecuteId());
            task.setStatus(2); // 处理中
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            // 扣除资源点
            pointService.deductPoints(task.getUserId(), task.getEstimatedPoints(), task.getId());

            // 增加工作流使用次数
            workflowService.incrementUsageCount(task.getWorkflowId());

            log.info("任务 {} 已提交到扣子平台，executeId: {}", task.getId(), response.getExecuteId());
        } catch (Exception e) {
            log.error("提交任务失败", e);
            task.setStatus(4); // 失败
            task.setErrorMessage("提交失败: " + e.getMessage());
            taskRepository.save(task);
            throw new BusinessException("提交任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务列表
     */
    public PageResponse<Task> getTaskList(Long userId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Task> taskPage;
        if (status != null) {
            taskPage = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            taskPage = taskRepository.findByUserId(userId, pageable);
        }

        return new PageResponse<>(
                taskPage.getContent(),
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.hasNext(),
                taskPage.hasPrevious()
        );
    }

    /**
     * 获取任务详情
     */
    public Task getTaskDetail(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    /**
     * 取消任务
     */
    @Transactional
    public void cancelTask(String taskId) {
        Task task = getTaskDetail(taskId);

        if (task.getStatus() >= 3) {
            throw new BusinessException("任务已完成或已取消，无法取消");
        }

        task.setStatus(5); // 已取消
        taskRepository.save(task);

        // 退还资源点
        if (task.getActualPoints() != null && task.getActualPoints() > 0) {
            pointService.addPoints(task.getUserId(), task.getActualPoints(), 4, task.getId());
        }
    }

    /**
     * 重试任务
     */
    @Transactional
    public void retryTask(String taskId) {
        Task task = getTaskDetail(taskId);

        if (task.getStatus() != 4) {
            throw new BusinessException("只能重试失败的任务");
        }

        task.setStatus(0); // 待提交
        task.setProgress(0);
        task.setErrorMessage(null);
        taskRepository.save(task);

        submitTaskToCoze(task);
    }

    /**
     * 同步任务状态(定时任务)
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void syncTaskStatus() {
        List<Task> processingTasks = taskRepository.findByStatus(2);

        for (Task task : processingTasks) {
            try {
                // 查询工作流获取cozeWorkflowId
                Workflow workflow = workflowRepository.findById(task.getWorkflowId())
                        .orElse(null);
                if (workflow == null) {
                    log.error("同步任务状态失败: 工作流不存在, taskId={}", task.getId());
                    continue;
                }
                CozeService.CozeTaskStatus status = cozeService.queryTaskStatus(
                        workflow.getCozeWorkflowId(),
                        task.getCozeTaskId()
                );

                if (status.isCompleted()) {
                    task.setStatus(3); // 已完成
                    task.setProgress(100);
                    task.setResult(status.getResult());
                    task.setCompletedAt(LocalDateTime.now());
                    task.setActualPoints(task.getEstimatedPoints());
                } else if (status.isFailed()) {
                    task.setStatus(4); // 失败
                    task.setErrorMessage(status.getErrorMessage());
                    task.setCompletedAt(LocalDateTime.now());
                } else {
                    task.setProgress(status.getProgress());
                }

                taskRepository.save(task);
            } catch (Exception e) {
                log.error("同步任务状态失败: {}", task.getId(), e);
            }
        }
    }
}

