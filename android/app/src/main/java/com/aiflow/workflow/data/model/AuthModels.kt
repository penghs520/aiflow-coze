package com.aiflow.workflow.data.model

import com.google.gson.annotations.SerializedName

// Login Request
data class LoginRequest(
    @SerializedName("username")
    val username: String? = null,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("password")
    val password: String
)

// Register Request
data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

// Forgot Password Request
data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

// Auth Response
data class AuthResponse(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("tokenType")
    val tokenType: String,
    
    @SerializedName("expiresIn")
    val expiresIn: Int,
    
    @SerializedName("refreshToken")
    val refreshToken: String,
    
    @SerializedName("user")
    val user: UserData
)

// User Data
data class UserData(
    @SerializedName("id")
    val id: Long,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    
    @SerializedName("pointsBalance")
    val pointsBalance: Long,
    
    @SerializedName("status")
    val status: Int,
    
    @SerializedName("lastLoginAt")
    val lastLoginAt: String?
)