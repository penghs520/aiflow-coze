package com.aiflow.workflow.data.repository

import com.aiflow.workflow.data.model.PaginationResponse
import com.aiflow.workflow.data.model.Task
import com.aiflow.workflow.data.model.TaskStatus
import com.aiflow.workflow.data.model.TaskSubmitRequest
import com.aiflow.workflow.data.model.TaskSubmitResponse
import com.aiflow.workflow.data.model.Workflow
import com.aiflow.workflow.data.model.WorkflowDetail
import com.aiflow.workflow.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkflowRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * 获取工作流列表
     */
    suspend fun getWorkflows(
        page: Int = 1,
        size: Int = 20,
        category: String? = null,
        sort: String? = null,
        search: String? = null
    ): Result<PaginationResponse<Workflow>> {
        return try {
            val response = apiService.getWorkflows(page, size, category, sort, search)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取工作流详情
     */
    suspend fun getWorkflowDetail(id: String): Result<WorkflowDetail> {
        return try {
            val response = apiService.getWorkflowDetail(id)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取工作流分类
     */
    suspend fun getWorkflowCategories(): Result<List<String>> {
        return try {
            val response = apiService.getWorkflowCategories()
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取热门排行
     */
    suspend fun getWorkflowRanking(type: String = "daily", limit: Int = 10): Result<List<Workflow>> {
        return try {
            val response = apiService.getWorkflowRanking(type, limit)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 收藏/取消收藏工作流
     */
    suspend fun toggleFavorite(id: String): Result<Unit> {
        return try {
            val response = apiService.toggleFavorite(id)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 提交任务
     */
    suspend fun submitTask(request: TaskSubmitRequest): Result<TaskSubmitResponse> {
        return try {
            val response = apiService.submitTask(request)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取任务列表
     */
    suspend fun getTasks(
        page: Int = 1,
        size: Int = 20,
        status: TaskStatus? = null
    ): Result<PaginationResponse<Task>> {
        return try {
            val response = apiService.getTasks(page, size, status)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取任务详情
     */
    suspend fun getTaskDetail(id: String): Result<Task> {
        return try {
            val response = apiService.getTaskDetail(id)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 取消任务
     */
    suspend fun cancelTask(id: String): Result<Unit> {
        return try {
            val response = apiService.cancelTask(id)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 重新运行任务
     */
    suspend fun retryTask(id: String): Result<Unit> {
        return try {
            val response = apiService.retryTask(id)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}