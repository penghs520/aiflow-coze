package com.aiflow.workflow.data.model

import com.google.gson.annotations.SerializedName

// 工作流列表项
data class Workflow(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("tags")
    val tags: List<String>?,
    
    @SerializedName("coverUrl")
    val coverUrl: String?,
    
    @SerializedName("previewVideoUrl")
    val previewVideoUrl: String?,
    
    @SerializedName("basePoints")
    val basePoints: Int,
    
    @SerializedName("status")
    val status: Int,
    
    @SerializedName("sortOrder")
    val sortOrder: Int,
    
    @SerializedName("usageCount")
    val usageCount: Int,
    
    @SerializedName("averageRating")
    val averageRating: Float,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

// 工作流详情
data class WorkflowDetail(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("tags")
    val tags: List<String>?,
    
    @SerializedName("coverUrl")
    val coverUrl: String?,
    
    @SerializedName("previewVideoUrl")
    val previewVideoUrl: String?,
    
    @SerializedName("basePoints")
    val basePoints: Int,
    
    @SerializedName("status")
    val status: Int,
    
    @SerializedName("parameterDefinition")
    val parameterDefinition: List<ParameterDefinition>,
    
    @SerializedName("statistics")
    val statistics: WorkflowStatistics?,
    
    @SerializedName("creatorId")
    val creatorId: Long?,
    
    @SerializedName("creatorName")
    val creatorName: String?,
    
    @SerializedName("cozeWorkflowId")
    val cozeWorkflowId: String,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

// 参数定义
data class ParameterDefinition(
    @SerializedName("key")
    val key: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("type")
    val type: ParameterType,
    
    @SerializedName("required")
    val required: Boolean,
    
    @SerializedName("default")
    val defaultValue: String?,
    
    @SerializedName("options")
    val options: List<ParameterOption>?,
    
    @SerializedName("constraints")
    val constraints: ParameterConstraints?
)

// 参数类型枚举
enum class ParameterType {
    @SerializedName("text")
    TEXT,
    
    @SerializedName("number")
    NUMBER,
    
    @SerializedName("select")
    SELECT,
    
    @SerializedName("multi_select")
    MULTI_SELECT,
    
    @SerializedName("boolean")
    BOOLEAN,
    
    @SerializedName("image_file")
    IMAGE_FILE,
    
    @SerializedName("video_file")
    VIDEO_FILE,
    
    @SerializedName("audio_file")
    AUDIO_FILE,
    
    @SerializedName("slider")
    SLIDER
}

// 参数选项
data class ParameterOption(
    @SerializedName("value")
    val value: String,
    
    @SerializedName("label")
    val label: String
)

// 参数约束
data class ParameterConstraints(
    @SerializedName("min")
    val min: String?,
    
    @SerializedName("max")
    val max: String?,
    
    @SerializedName("pattern")
    val pattern: String?,
    
    @SerializedName("maxSize")
    val maxSize: String?,
    
    @SerializedName("formats")
    val formats: List<String>?
)

// 工作流统计数据
data class WorkflowStatistics(
    @SerializedName("totalTasks")
    val totalTasks: Int,
    
    @SerializedName("successTasks")
    val successTasks: Int,
    
    @SerializedName("failedTasks")
    val failedTasks: Int,
    
    @SerializedName("successRate")
    val successRate: Float,
    
    @SerializedName("favoriteCount")
    val favoriteCount: Int
)

// 分页响应
data class PaginationResponse<T>(
    @SerializedName("list")
    val list: List<T>,
    
    @SerializedName("pagination")
    val pagination: PaginationInfo
)

// 分页信息
data class PaginationInfo(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("size")
    val size: Int,
    
    @SerializedName("total")
    val total: Long,
    
    @SerializedName("pages")
    val pages: Int
)

// 任务提交请求
data class TaskSubmitRequest(
    @SerializedName("workflowId")
    val workflowId: String,
    
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
    
    @SerializedName("settings")
    val settings: TaskSettings?
)

// 任务设置
data class TaskSettings(
    @SerializedName("priority")
    val priority: String, // "low", "normal", "high", "urgent"
    
    @SerializedName("notifyWhenComplete")
    val notifyWhenComplete: Boolean
)

// 任务提交响应
data class TaskSubmitResponse(
    @SerializedName("taskId")
    val taskId: String,
    
    @SerializedName("estimatedPoints")
    val estimatedPoints: Int,
    
    @SerializedName("queuePosition")
    val queuePosition: Int?
)