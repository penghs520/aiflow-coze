package com.aiflow.workflow.data.repository

import com.aiflow.workflow.data.model.AuthResponse
import com.aiflow.workflow.data.model.ForgotPasswordRequest
import com.aiflow.workflow.data.model.LoginRequest
import com.aiflow.workflow.data.model.RegisterRequest
import com.aiflow.workflow.data.model.UserData
import com.aiflow.workflow.data.network.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    suspend fun login(username: String?, email: String?, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(username = username, email = email, password = password)
            val response = apiService.login(request)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(username = username, email = email, password = password)
            val response = apiService.register(request)
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val request = ForgotPasswordRequest(email = email)
            val response = apiService.forgotPassword(request)
            if (response.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(): Result<UserData> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccess && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}