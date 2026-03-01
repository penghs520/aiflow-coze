package com.aiflow.workflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiflow.workflow.data.model.UserData
import com.aiflow.workflow.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 充值选项
data class RechargeOption(
    val id: Int,
    val points: Long,
    val price: String,
    val description: String
)

// 充值状态
data class RechargeState(
    val userData: UserData? = null,
    val isLoading: Boolean = false,
    val isRecharging: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val selectedOption: RechargeOption? = null
)

@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _rechargeState = MutableStateFlow(RechargeState())
    val rechargeState: StateFlow<RechargeState> = _rechargeState.asStateFlow()

    // 充值选项列表
    val rechargeOptions = listOf(
        RechargeOption(1, 5000L, "¥5.00", "入门套餐"),
        RechargeOption(2, 10000L, "¥9.00", "实惠套餐"),
        RechargeOption(3, 20000L, "¥16.00", "尊享套餐")
    )

    init {
        loadUserData()
    }

    /**
     * 加载用户数据
     */
    fun loadUserData() {
        viewModelScope.launch {
            _rechargeState.update { it.copy(isLoading = true, error = null) }
            
            val result = authRepository.getUserProfile()
            
            if (result.isSuccess) {
                _rechargeState.update {
                    it.copy(
                        userData = result.getOrThrow(),
                        isLoading = false
                    )
                }
            } else {
                _rechargeState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "加载用户信息失败"
                    )
                }
            }
        }
    }

    /**
     * 选择充值选项
     */
    fun selectRechargeOption(option: RechargeOption) {
        _rechargeState.update { it.copy(selectedOption = option) }
    }

    /**
     * 执行充值（模拟）
     */
    fun performRecharge() {
        val selectedOption = _rechargeState.value.selectedOption
        val currentUserData = _rechargeState.value.userData
        
        if (selectedOption == null) {
            _rechargeState.update { it.copy(error = "请选择充值套餐") }
            return
        }
        
        if (currentUserData == null) {
            _rechargeState.update { it.copy(error = "用户信息未加载") }
            return
        }
        
        viewModelScope.launch {
            _rechargeState.update { it.copy(isRecharging = true, error = null) }
            
            // 模拟网络请求延迟
            kotlinx.coroutines.delay(1500)
            
            // 模拟充值成功：更新本地余额
            val updatedUserData = currentUserData.copy(
                pointsBalance = currentUserData.pointsBalance + selectedOption.points
            )
            
            _rechargeState.update {
                it.copy(
                    userData = updatedUserData,
                    isRecharging = false,
                    successMessage = "充值成功！获得 ${selectedOption.points} 资源点"
                )
            }
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _rechargeState.update { it.copy(error = null) }
    }

    /**
     * 清除成功消息
     */
    fun clearSuccessMessage() {
        _rechargeState.update { it.copy(successMessage = null) }
    }

    /**
     * 清除选中状态
     */
    fun clearSelection() {
        _rechargeState.update { it.copy(selectedOption = null) }
    }
}