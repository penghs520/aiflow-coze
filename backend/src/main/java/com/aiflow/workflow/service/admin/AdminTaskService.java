package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端任务监控服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTaskService {

    private final TaskRepository taskRepository;

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
}
