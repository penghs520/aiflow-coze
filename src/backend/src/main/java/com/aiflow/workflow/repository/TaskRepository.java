package com.aiflow.workflow.repository;

import com.aiflow.workflow.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {

    /**
     * 根据用户ID查找任务
     */
    List<Task> findByUser_Id(Long userId);

    /**
     * 根据用户ID和状态查找任务
     */
    List<Task> findByUser_IdAndStatus(Long userId, Integer status);

    /**
     * 根据用户ID分页查找任务
     */
    Page<Task> findByUser_Id(Long userId, Pageable pageable);

    /**
     * 根据用户ID和状态分页查找任务
     */
    Page<Task> findByUser_IdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 根据工作流ID查找任务
     */
    List<Task> findByWorkflowId(String workflowId);

    /**
     * 根据工作流ID和状态查找任务
     */
    List<Task> findByWorkflowIdAndStatus(String workflowId, Integer status);

    /**
     * 根据状态查找任务
     */
    List<Task> findByStatus(Integer status);

    /**
     * 根据状态和创建时间范围查找任务
     */
    List<Task> findByStatusAndCreatedAtBetween(Integer status, LocalDateTime start, LocalDateTime end);

    /**
     * 查找需要处理的任务（排队中）
     */
    List<Task> findByStatusOrderByCreatedAtAsc(Integer status);

    /**
     * 统计用户的任务数量
     */
    long countByUser_Id(Long userId);

    /**
     * 统计用户指定状态的任务数量
     */
    long countByUser_IdAndStatus(Long userId, Integer status);

    /**
     * 根据扣子任务ID查找任务
     */
    Optional<Task> findByCozeTaskId(String cozeTaskId);

    /**
     * 查找超时未完成的任务
     */
    @Query("SELECT t FROM Task t WHERE t.status IN (1, 2) AND t.createdAt < :timeoutTime")
    List<Task> findTimeoutTasks(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 查找需要状态同步的任务（处理中但扣子任务已完成）
     */
    @Query(value = "SELECT * FROM tasks t WHERE t.status = 2 AND t.coze_task_id IS NOT NULL", nativeQuery = true)
    List<Task> findTasksNeedSync();
}