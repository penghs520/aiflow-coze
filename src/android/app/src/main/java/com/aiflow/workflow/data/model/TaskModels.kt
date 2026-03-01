package com.aiflow.workflow.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

// 任务状态枚举
enum class TaskStatus(val value: Int) {
    @SerializedName("0")
    PENDING_SUBMIT(0),           // 待提交
    
    @SerializedName("1")
    QUEUED(1),                   // 排队中
    
    @SerializedName("2")
    PROCESSING(2),               // 处理中
    
    @SerializedName("3")
    COMPLETED(3),                // 已完成
    
    @SerializedName("4")
    FAILED(4),                   // 失败
    
    @SerializedName("5")
    CANCELLED(5);                // 已取消
    
    companion object {
        fun fromValue(value: Int): TaskStatus {
            return values().firstOrNull { it.value == value } ?: PENDING_SUBMIT
        }
    }
}

// 任务结果
data class TaskResult(
    @SerializedName("outputVideoUrl")
    val outputVideoUrl: String?,
    
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String?,
    
    @SerializedName("duration")
    val duration: String?,
    
    @SerializedName("resolution")
    val resolution: String?,
    
    @SerializedName("fileSize")
    val fileSize: String?
)

// 任务日志项
data class TaskLog(
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("level")
    val level: String,
    
    @SerializedName("message")
    val message: String
)

// 任务详情
data class Task(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("workflowId")
    val workflowId: String,
    
    @SerializedName("workflowName")
    val workflowName: String,
    
    @SerializedName("userId")
    val userId: Long,
    
    @SerializedName("status")
    val status: TaskStatus,
    
    @SerializedName("progress")
    val progress: Int,
    
    @SerializedName("estimatedPoints")
    val estimatedPoints: Int,
    
    @SerializedName("actualPoints")
    val actualPoints: Int?,
    
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
    
    @SerializedName("result")
    val result: TaskResult?,
    
    @SerializedName("errorMessage")
    val errorMessage: String?,
    
    @SerializedName("cozeTaskId")
    val cozeTaskId: String?,
    
    @SerializedName("startedAt")
    val startedAt: String?,
    
    @SerializedName("completedAt")
    val completedAt: String?,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String
) {
    // 是否可以进行操作
    val canCancel: Boolean
        get() = status == TaskStatus.QUEUED || status == TaskStatus.PROCESSING
    
    val canRetry: Boolean
        get() = status == TaskStatus.FAILED
}

// 任务列表请求参数
data class TaskListRequest(
    val page: Int = 1,
    val size: Int = 20,
    val status: TaskStatus? = null
)

// 任务提交进度
data class TaskSubmitProgress(
    val taskId: String,
    val progress: Int,
    val status: TaskStatus,
    val message: String?
)