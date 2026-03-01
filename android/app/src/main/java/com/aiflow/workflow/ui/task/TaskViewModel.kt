package com.aiflow.workflow.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiflow.workflow.data.model.PaginationResponse
import com.aiflow.workflow.data.model.Task
import com.aiflow.workflow.data.model.TaskStatus
import com.aiflow.workflow.data.repository.WorkflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 任务列表状态
data class TaskListState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val selectedStatus: TaskStatus? = null
)

// 任务操作状态
data class TaskOperationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {

    // 任务列表状态
    private val _taskListState = MutableStateFlow(TaskListState())
    val taskListState: StateFlow<TaskListState> = _taskListState.asStateFlow()

    // 任务操作状态
    private val _taskOperationState = MutableStateFlow(TaskOperationState())
    val taskOperationState: StateFlow<TaskOperationState> = _taskOperationState.asStateFlow()

    // 当前选中的任务（用于详情）
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    /**
     * 加载任务列表（第一页）
     */
    fun loadTasks(status: TaskStatus? = null) {
        viewModelScope.launch {
            _taskListState.update { it.copy(isLoading = true, error = null) }
            
            val result = workflowRepository.getTasks(
                page = 1,
                size = 20,
                status = status
            )
            
            if (result.isSuccess) {
                val data = result.getOrThrow()
                _taskListState.update {
                    it.copy(
                        tasks = data.list,
                        isLoading = false,
                        page = data.pagination.page,
                        hasMore = data.pagination.page < data.pagination.pages,
                        selectedStatus = status
                    )
                }
            } else {
                _taskListState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "加载失败"
                    )
                }
            }
        }
    }

    /**
     * 刷新任务列表
     */
    fun refreshTasks() {
        viewModelScope.launch {
            _taskListState.update { it.copy(isRefreshing = true, error = null) }
            
            val result = workflowRepository.getTasks(
                page = 1,
                size = 20,
                status = _taskListState.value.selectedStatus
            )
            
            if (result.isSuccess) {
                val data = result.getOrThrow()
                _taskListState.update {
                    it.copy(
                        tasks = data.list,
                        isRefreshing = false,
                        page = data.pagination.page,
                        hasMore = data.pagination.page < data.pagination.pages
                    )
                }
            } else {
                _taskListState.update {
                    it.copy(
                        isRefreshing = false,
                        error = result.exceptionOrNull()?.message ?: "刷新失败"
                    )
                }
            }
        }
    }

    /**
     * 加载更多任务
     */
    fun loadMoreTasks() {
        val currentState = _taskListState.value
        if (currentState.isLoadingMore || !currentState.hasMore) return
        
        viewModelScope.launch {
            _taskListState.update { it.copy(isLoadingMore = true) }
            
            val result = workflowRepository.getTasks(
                page = currentState.page + 1,
                size = 20,
                status = currentState.selectedStatus
            )
            
            if (result.isSuccess) {
                val data = result.getOrThrow()
                val newTasks = currentState.tasks + data.list
                _taskListState.update {
                    it.copy(
                        tasks = newTasks,
                        isLoadingMore = false,
                        page = data.pagination.page,
                        hasMore = data.pagination.page < data.pagination.pages
                    )
                }
            } else {
                _taskListState.update {
                    it.copy(
                        isLoadingMore = false,
                        error = result.exceptionOrNull()?.message ?: "加载更多失败"
                    )
                }
            }
        }
    }

    /**
     * 根据状态筛选任务
     */
    fun filterByStatus(status: TaskStatus?) {
        loadTasks(status)
    }

    /**
     * 取消任务
     */
    fun cancelTask(taskId: String) {
        viewModelScope.launch {
            _taskOperationState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            
            val result = workflowRepository.cancelTask(taskId)
            
            if (result.isSuccess) {
                // 取消成功后刷新列表
                refreshTasks()
                _taskOperationState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "任务已取消"
                    )
                }
            } else {
                _taskOperationState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "取消任务失败"
                    )
                }
            }
        }
    }

    /**
     * 重试任务
     */
    fun retryTask(taskId: String) {
        viewModelScope.launch {
            _taskOperationState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            
            val result = workflowRepository.retryTask(taskId)
            
            if (result.isSuccess) {
                // 重试成功后刷新列表
                refreshTasks()
                _taskOperationState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "任务已重新提交"
                    )
                }
            } else {
                _taskOperationState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "重试任务失败"
                    )
                }
            }
        }
    }

    /**
     * 选择任务（用于查看详情）
     */
    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    /**
     * 清除操作状态
     */
    fun clearOperationState() {
        _taskOperationState.value = TaskOperationState()
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _taskListState.update { it.copy(error = null) }
        _taskOperationState.update { it.copy(error = null) }
    }
}