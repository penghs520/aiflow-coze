package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.repository.TaskRepository;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.repository.WorkflowRepository;
import com.aiflow.workflow.service.CozeService;
import com.aiflow.workflow.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端任务监控服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTaskService {

    private final TaskRepository taskRepository;
    private final WorkflowRepository workflowRepository;
    private final UserRepository userRepository;
    private final CozeService cozeService;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    /**
     * 分页查询任务
     */
    public Page<Task> listTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    /**
     * 根据状态查询任务
     */
    public List<Task> listByStatus(Integer status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * 根据用户ID查询任务
     */
    public Page<Task> listByUser(Long userId, Pageable pageable) {
        return taskRepository.findByUser_Id(userId, pageable);
    }

    /**
     * 根据工作流ID查询任务
     */
    public List<Task> listByWorkflow(String workflowId) {
        return taskRepository.findByWorkflowId(workflowId);
    }

    /**
     * 获取任务详情
     */
    public Task getTask(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
    }

    /**
     * 取消任务
     */
    @Transactional
    public void cancelTask(String id) {
        Task task = getTask(id);

        if (!task.canBeCancelled()) {
            throw new RuntimeException("任务当前状态不可取消");
        }

        task.cancel();
        taskRepository.save(task);

        log.info("管理员取消任务: {}", id);
    }

    /**
     * 批量取消任务
     */
    @Transactional
    public void batchCancel(List<String> ids) {
        ids.forEach(this::cancelTask);
    }

    /**
     * 查询超时任务
     */
    public List<Task> findTimeoutTasks(int timeoutMinutes) {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return taskRepository.findTimeoutTasks(timeoutTime);
    }

    /**
     * 统计任务数量
     */
    public long countTasks() {
        return taskRepository.count();
    }

    /**
     * 统计指定状态的任务数量
     */
    public long countByStatus(Integer status) {
        return taskRepository.findByStatus(status).size();
    }

    /**
     * 统计今日任务数量
     */
    public long countTodayTasks() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return taskRepository.findByStatusAndCreatedAtBetween(null, startOfDay, endOfDay).size();
    }

    /**
     * 获取处理中的任务列表
     */
    public List<Task> getProcessingTasks() {
        return taskRepository.findByStatus(Task.STATUS_PROCESSING);
    }

    /**
     * 获取排队中的任务列表
     */
    public List<Task> getQueuedTasks() {
        return taskRepository.findByStatusOrderByCreatedAtAsc(Task.STATUS_QUEUED);
    }

    /**
     * 管理员执行工作流（内测使用，不检查资源点）
     *
     * @param workflowId 工作流ID
     * @param userId     执行用户ID（用于记录）
     * @param parameters 任务参数
     * @return 创建的任务
     */
    @Transactional
    public Task executeWorkflow(String workflowId, Long userId, Map<String, Object> parameters) {
        // 查找工作流
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建任务
        Task task = Task.builder()
                .id(String.valueOf(idGenerator.nextId()))
                .workflow(workflow)
                .user(user)
                .status(Task.STATUS_PENDING_SUBMISSION)
                .progress(0)
                .estimatedPoints(0) // 内测任务不消耗资源点
                .build();

        try {
            task.setParameters(objectMapper.writeValueAsString(parameters));
        } catch (Exception e) {
            throw new RuntimeException("参数序列化失败");
        }

        task.setCreatedAt(LocalDateTime.now());
        task = taskRepository.save(task);

        // 提交到扣子平台
        submitTaskToCoze(task);

        log.info("管理员执行工作流: workflowId={}, taskId={}", workflowId, task.getId());

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

            // 调用扣子API
            CozeService.CozeExecuteResponse response = cozeService.executeWorkflow(
                    task.getWorkflow().getCozeWorkflowId(),
                    parameters
            );

            // 更新任务状态
            task.setCozeTaskId(response.getExecuteId());
            task.setStatus(Task.STATUS_PROCESSING);
            task.setStartedAt(LocalDateTime.now());
            taskRepository.save(task);

            log.info("任务 {} 已提交到扣子平台，executeId: {}", task.getId(), response.getExecuteId());
        } catch (Exception e) {
            log.error("提交任务失败", e);
            task.setStatus(Task.STATUS_FAILED);
            task.setErrorMessage("提交失败: " + e.getMessage());
            taskRepository.save(task);
            throw new RuntimeException("提交任务失败: " + e.getMessage());
        }
    }
}
