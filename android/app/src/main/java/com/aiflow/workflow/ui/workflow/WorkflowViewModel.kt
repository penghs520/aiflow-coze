package com.aiflow.workflow.ui.workflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiflow.workflow.data.model.PaginationResponse
import com.aiflow.workflow.data.model.Task
import com.aiflow.workflow.data.model.TaskStatus
import com.aiflow.workflow.data.model.TaskSubmitRequest
import com.aiflow.workflow.data.model.TaskSubmitResponse
import com.aiflow.workflow.data.model.Workflow
import com.aiflow.workflow.data.model.WorkflowDetail
import com.aiflow.workflow.data.repository.WorkflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 工作流状态
sealed class WorkflowState {
    object Idle : WorkflowState()
    object Loading : WorkflowState()
    data class Success(val data: Any? = null) : WorkflowState()
    data class Error(val message: String) : WorkflowState()
}

// 工作流列表状态
data class WorkflowListState(
    val workflows: List<Workflow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val selectedCategory: String? = null,
    val sortOption: String? = null,
    val searchQuery: String? = null
)

// 工作流详情状态
data class WorkflowDetailState(
    val workflow: WorkflowDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// 任务提交状态
data class TaskSubmitState(
    val isLoading: Boolean = false,
    val submittedTask: TaskSubmitResponse? = null,
    val error: String? = null
)

@HiltViewModel
class WorkflowViewModel @Inject constructor(
    private val workflowRepository: WorkflowRepository
) : ViewModel() {
    
    // 工作流列表相关状态
    private val _workflowListState = MutableStateFlow(WorkflowListState())
    val workflowListState: StateFlow<WorkflowListState> = _workflowListState
    
    // 工作流详情相关状态
    private val _workflowDetailState = MutableStateFlow(WorkflowDetailState())
    val workflowDetailState: StateFlow<WorkflowDetailState> = _workflowDetailState
    
    // 任务提交相关状态
    private val _taskSubmitState = MutableStateFlow(TaskSubmitState())
    val taskSubmitState: StateFlow<TaskSubmitState> = _taskSubmitState
    
    // 分类列表
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories
    
    // 热门排行
    private val _ranking = MutableStateFlow<List<Workflow>>(emptyList())
    val ranking: StateFlow<List<Workflow>> = _ranking
    
    init {
        loadCategories()
        loadRanking()
    }
    
    /**
     * 加载工作流列表
     */
    fun loadWorkflows(
        page: Int = 1,
        category: String? = null,
        sort: String? = null,
        search: String? = null
    ) {
        viewModelScope.launch {
            _workflowListState.value = _workflowListState.value.copy(
                isLoading = true,
                error = null
            )
            
            val result = workflowRepository.getWorkflows(
                page = page,
                category = category,
                sort = sort,
                search = search
            )
            
            if (result.isSuccess) {
                val data = result.getOrThrow()
                val currentWorkflows = if (page == 1) {
                    emptyList()
                } else {
                    _workflowListState.value.workflows
                }
                
                val newWorkflows = currentWorkflows + data.list
                val hasMore = data.pagination.page < data.pagination.pages
                
                _workflowListState.value = _workflowListState.value.copy(
                    workflows = newWorkflows,
                    page = data.pagination.page,
                    hasMore = hasMore,
                    isLoading = false,
                    selectedCategory = category,
                    sortOption = sort,
                    searchQuery = search
                )
            } else {
                _workflowListState.value = _workflowListState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 刷新工作流列表
     */
    fun refreshWorkflows() {
        loadWorkflows(page = 1)
    }
    
    /**
     * 加载更多工作流
     */
    fun loadMoreWorkflows() {
        val currentState = _workflowListState.value
        if (!currentState.isLoading && currentState.hasMore) {
            loadWorkflows(
                page = currentState.page + 1,
                category = currentState.selectedCategory,
                sort = currentState.sortOption,
                search = currentState.searchQuery
            )
        }
    }
    
    /**
     * 加载工作流详情
     */
    fun loadWorkflowDetail(id: String) {
        viewModelScope.launch {
            _workflowDetailState.value = _workflowDetailState.value.copy(
                isLoading = true,
                error = null
            )
            
            val result = workflowRepository.getWorkflowDetail(id)
            
            if (result.isSuccess) {
                _workflowDetailState.value = _workflowDetailState.value.copy(
                    workflow = result.getOrThrow(),
                    isLoading = false
                )
            } else {
                _workflowDetailState.value = _workflowDetailState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载分类列表
     */
    private fun loadCategories() {
        viewModelScope.launch {
            val result = workflowRepository.getWorkflowCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrThrow()
            }
        }
    }
    
    /**
     * 加载热门排行
     */
    private fun loadRanking() {
        viewModelScope.launch {
            val result = workflowRepository.getWorkflowRanking()
            if (result.isSuccess) {
                _ranking.value = result.getOrThrow()
            }
        }
    }
    
    /**
     * 提交任务
     */
    fun submitTask(request: TaskSubmitRequest) {
        viewModelScope.launch {
            _taskSubmitState.value = _taskSubmitState.value.copy(
                isLoading = true,
                error = null
            )
            
            val result = workflowRepository.submitTask(request)
            
            if (result.isSuccess) {
                _taskSubmitState.value = _taskSubmitState.value.copy(
                    submittedTask = result.getOrThrow(),
                    isLoading = false
                )
            } else {
                _taskSubmitState.value = _taskSubmitState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "提交失败"
                )
            }
        }
    }
    
    /**
     * 切换收藏状态
     */
    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            workflowRepository.toggleFavorite(id)
        }
    }
    
    /**
     * 重置工作流详情状态
     */
    fun resetWorkflowDetail() {
        _workflowDetailState.value = WorkflowDetailState()
    }
    
    /**
     * 重置任务提交状态
     */
    fun resetTaskSubmit() {
        _taskSubmitState.value = TaskSubmitState()
    }
}