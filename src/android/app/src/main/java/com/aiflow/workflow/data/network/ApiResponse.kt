package com.aiflow.workflow.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("timestamp")
    val timestamp: Long
) {
    val isSuccess: Boolean
        get() = code == 200
}