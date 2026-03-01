package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.Task;
import com.aiflow.workflow.service.admin.AdminTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<Task>> listTasks(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminTaskService.listTasks(pageable));
    }

    /**
     * 根据状态查询任务
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> listByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(adminTaskService.listByStatus(status));
    }

    /**
     * 根据用户ID查询任务
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Task>> listByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminTaskService.listByUser(userId, pageable));
    }

    /**
     * 根据工作流ID查询任务
     */
    @GetMapping("/workflow/{workflowId}")
    public ResponseEntity<List<Task>> listByWorkflow(@PathVariable String workflowId) {
        return ResponseEntity.ok(adminTaskService.listByWorkflow(workflowId));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable String id) {
        return ResponseEntity.ok(adminTaskService.getTask(id));
    }

    /**
     * 取消任务
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTask(@PathVariable String id) {
        adminTaskService.cancelTask(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量取消任务
     */
    @PutMapping("/batch/cancel")
    public ResponseEntity<Void> batchCancel(@RequestBody List<String> ids) {
        adminTaskService.batchCancel(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询超时任务
     */
    @GetMapping("/timeout")
    public ResponseEntity<List<Task>> findTimeoutTasks(@RequestParam(defaultValue = "60") int timeoutMinutes) {
        return ResponseEntity.ok(adminTaskService.findTimeoutTasks(timeoutMinutes));
    }

    /**
     * 获取处理中的任务
     */
    @GetMapping("/processing")
    public ResponseEntity<List<Task>> getProcessingTasks() {
        return ResponseEntity.ok(adminTaskService.getProcessingTasks());
    }

    /**
     * 获取排队中的任务
     */
    @GetMapping("/queued")
    public ResponseEntity<List<Task>> getQueuedTasks() {
        return ResponseEntity.ok(adminTaskService.getQueuedTasks());
    }

    /**
     * 统计任务数量
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Long>> countTasks(@RequestParam(required = false) Integer status) {
        if (status != null) {
            return ResponseEntity.ok(Map.of("count", adminTaskService.countByStatus(status)));
        }
        return ResponseEntity.ok(Map.of(
                "total", adminTaskService.countTasks(),
                "today", adminTaskService.countTodayTasks()
        ));
    }
}
