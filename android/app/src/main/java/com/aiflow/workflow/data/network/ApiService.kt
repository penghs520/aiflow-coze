package com.aiflow.workflow.data.network

import com.aiflow.workflow.data.model.AuthResponse
import com.aiflow.workflow.data.model.ForgotPasswordRequest
import com.aiflow.workflow.data.model.LoginRequest
import com.aiflow.workflow.data.model.PaginationResponse
import com.aiflow.workflow.data.model.RegisterRequest
import com.aiflow.workflow.data.model.Task
import com.aiflow.workflow.data.model.TaskStatus
import com.aiflow.workflow.data.model.TaskSubmitRequest
import com.aiflow.workflow.data.model.TaskSubmitResponse
import com.aiflow.workflow.data.model.UserData
import com.aiflow.workflow.data.model.Workflow
import com.aiflow.workflow.data.model.WorkflowDetail
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthResponse>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ApiResponse<Unit>
    
    @POST("auth/refresh")
    suspend fun refreshToken(): ApiResponse<AuthResponse>
    
    @GET("users/profile")
    suspend fun getUserProfile(): ApiResponse<UserData>
    
    // 工作流相关接口
    
    @GET("workflows")
    suspend fun getWorkflows(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<PaginationResponse<Workflow>>
    
    @GET("workflows/{id}")
    suspend fun getWorkflowDetail(
        @Path("id") id: String
    ): ApiResponse<WorkflowDetail>
    
    @GET("workflows/categories")
    suspend fun getWorkflowCategories(): ApiResponse<List<String>>
    
    @GET("workflows/ranking")
    suspend fun getWorkflowRanking(
        @Query("type") type: String = "daily",
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<Workflow>>
    
    @POST("workflows/{id}/favorite")
    suspend fun toggleFavorite(
        @Path("id") id: String
    ): ApiResponse<Unit>
    
    // 任务相关接口
    
    @POST("tasks")
    suspend fun submitTask(
        @Body request: TaskSubmitRequest
    ): ApiResponse<TaskSubmitResponse>
    
    @GET("tasks")
    suspend fun getTasks(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("status") status: TaskStatus? = null
    ): ApiResponse<PaginationResponse<Task>>
    
    @GET("tasks/{id}")
    suspend fun getTaskDetail(
        @Path("id") id: String
    ): ApiResponse<Task>
    
    @POST("tasks/{id}/cancel")
    suspend fun cancelTask(
        @Path("id") id: String
    ): ApiResponse<Unit>
    
    @POST("tasks/{id}/retry")
    suspend fun retryTask(
        @Path("id") id: String
    ): ApiResponse<Unit>
}