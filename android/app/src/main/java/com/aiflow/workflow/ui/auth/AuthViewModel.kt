package com.aiflow.workflow.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiflow.workflow.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState
    
    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState
    
    private val _forgotPasswordState = MutableStateFlow<AuthState>(AuthState.Idle)
    val forgotPasswordState: StateFlow<AuthState> = _forgotPasswordState
    
    fun login(username: String?, email: String?, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = authRepository.login(username, email, password)
            _loginState.value = if (result.isSuccess) {
                // TODO: Save auth data to preferences
                AuthState.Success("登录成功")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "登录失败")
            }
        }
    }
    
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = authRepository.register(username, email, password)
            _registerState.value = if (result.isSuccess) {
                // TODO: Save auth data to preferences
                AuthState.Success("注册成功")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "注册失败")
            }
        }
    }
    
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = AuthState.Loading
            val result = authRepository.forgotPassword(email)
            _forgotPasswordState.value = if (result.isSuccess) {
                AuthState.Success("重置链接已发送")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "发送失败")
            }
        }
    }
    
    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }
    
    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }
    
    fun resetForgotPasswordState() {
        _forgotPasswordState.value = AuthState.Idle
    }
}