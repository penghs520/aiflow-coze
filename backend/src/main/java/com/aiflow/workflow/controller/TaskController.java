package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.CreateTaskRequest;
import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制器
 */
@Tag(name = "任务接口")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "创建任务")
    @PostMapping
    public Result<Task> createTask(Authentication authentication,
                                    @Valid @RequestBody CreateTaskRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Task task = taskService.createTask(
                userId,
                request.getWorkflowId(),
                request.getParameters(),
                request.getSettings()
        );
        return Result.success(task);
    }

    @Operation(summary = "获取任务列表")
    @GetMapping
    public Result<PageResponse<Task>> getTaskList(
            Authentication authentication,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        PageResponse<Task> tasks = taskService.getTaskList(userId, status, page, size);
        return Result.success(tasks);
    }

    @Operation(summary = "获取任务详情")
    @GetMapping("/{id}")
    public Result<Task> getTaskDetail(@PathVariable String id) {
        Task task = taskService.getTaskDetail(id);
        return Result.success(task);
    }

    @Operation(summary = "取消任务")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelTask(@PathVariable String id) {
        taskService.cancelTask(id);
        return Result.success();
    }

    @Operation(summary = "重试任务")
    @PostMapping("/{id}/retry")
    public Result<Void> retryTask(@PathVariable String id) {
        taskService.retryTask(id);
        return Result.success();
    }
}
