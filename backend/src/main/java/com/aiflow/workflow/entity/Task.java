package com.aiflow.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 任务实体类
 * 对应数据库表 tasks
 */
@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_user_status", columnList = "user_id, status"),
        @Index(name = "idx_workflow_status", columnList = "workflow_id, status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Workflow workflow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    /**
     * 状态：0-待提交，1-排队中，2-处理中，3-已完成，4-失败，5-已取消
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "progress", nullable = false)
    @Builder.Default
    private Integer progress = 0;

    /**
     * 任务参数，存储为JSON
     */
    @Column(name = "parameters", nullable = false, columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String parameters;

    @Column(name = "estimated_points")
    private Integer estimatedPoints;

    @Column(name = "actual_points")
    private Integer actualPoints;

    /**
     * 执行结果，存储为JSON
     */
    @Column(name = "result", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String result;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "coze_task_id", length = 100)
    private String cozeTaskId;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 状态常量
    public static final int STATUS_PENDING_SUBMISSION = 0;
    public static final int STATUS_QUEUED = 1;
    public static final int STATUS_PROCESSING = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_FAILED = 4;
    public static final int STATUS_CANCELLED = 5;

    // 业务方法
    public boolean isPending() {
        return status == STATUS_PENDING_SUBMISSION;
    }

    public boolean isQueued() {
        return status == STATUS_QUEUED;
    }

    public boolean isProcessing() {
        return status == STATUS_PROCESSING;
    }

    public boolean isCompleted() {
        return status == STATUS_COMPLETED;
    }

    public boolean isFailed() {
        return status == STATUS_FAILED;
    }

    public boolean isCancelled() {
        return status == STATUS_CANCELLED;
    }

    public boolean canBeCancelled() {
        return status == STATUS_QUEUED || status == STATUS_PROCESSING;
    }

    public boolean canBeRetried() {
        return status == STATUS_FAILED;
    }

    public void startProcessing() {
        if (status != STATUS_QUEUED) {
            throw new IllegalStateException("只有排队中的任务可以开始处理");
        }
        status = STATUS_PROCESSING;
        startedAt = LocalDateTime.now();
        progress = 10; // 初始进度
    }

    public void updateProgress(Integer newProgress) {
        if (newProgress < 0 || newProgress > 100) {
            throw new IllegalArgumentException("进度必须在0-100之间");
        }
        progress = newProgress;
    }

    public void complete(String resultJson) {
        status = STATUS_COMPLETED;
        progress = 100;
        result = resultJson;
        completedAt = LocalDateTime.now();
    }

    public void fail(String error) {
        status = STATUS_FAILED;
        errorMessage = error;
        completedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("任务当前状态不可取消");
        }
        status = STATUS_CANCELLED;
        completedAt = LocalDateTime.now();
    }
}