package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.service.admin.AdminTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.aiflow.workflow.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端任务监控控制器
 */
@RestController
@RequestMapping("/admin/v1/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final AdminTaskService adminTaskService;

    /**
     * 分页查询任务
     */
    @GetMapping
    public Result<Page<Task>> listTasks(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminTaskService.listTasks(pageable));
    }

    /**
     * 根据状态查询任务
     */
    @GetMapping("/status/{status}")
    public Result<List<Task>> listByStatus(@PathVariable Integer status) {
        return Result.success(adminTaskService.listByStatus(status));
    }

    /**
     * 根据用户ID查询任务
     */
    @GetMapping("/user/{userId}")
    public Result<Page<Task>> listByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminTaskService.listByUser(userId, pageable));
    }

    /**
     * 根据工作流ID查询任务
     */
    @GetMapping("/workflow/{workflowId}")
    public Result<List<Task>> listByWorkflow(@PathVariable String workflowId) {
        return Result.success(adminTaskService.listByWorkflow(workflowId));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public Result<Task> getTask(@PathVariable String id) {
        return Result.success(adminTaskService.getTask(id));
    }

    /**
     * 取消任务
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelTask(@PathVariable String id) {
        adminTaskService.cancelTask(id);
        return Result.success();
    }

    /**
     * 批量取消任务
     */
    @PutMapping("/batch/cancel")
    public Result<Void> batchCancel(@RequestBody List<String> ids) {
        adminTaskService.batchCancel(ids);
        return Result.success();
    }

    /**
     * 查询超时任务
     */
    @GetMapping("/timeout")
    public Result<List<Task>> findTimeoutTasks(@RequestParam(defaultValue = "60") int timeoutMinutes) {
        return Result.success(adminTaskService.findTimeoutTasks(timeoutMinutes));
    }

    /**
     * 获取处理中的任务
     */
    @GetMapping("/processing")
    public Result<List<Task>> getProcessingTasks() {
        return Result.success(adminTaskService.getProcessingTasks());
    }

    /**
     * 获取排队中的任务
     */
    @GetMapping("/queued")
    public Result<List<Task>> getQueuedTasks() {
        return Result.success(adminTaskService.getQueuedTasks());
    }

    /**
     * 统计任务数量
     */
    @GetMapping("/stats/count")
    public Result<Map<String, Long>> countTasks(@RequestParam(required = false) Integer status) {
        if (status != null) {
            return Result.success(Map.of("count", adminTaskService.countByStatus(status)));
        }
        return Result.success(Map.of(
                "total", adminTaskService.countTasks(),
                "today", adminTaskService.countTodayTasks()
        ));
    }
}
