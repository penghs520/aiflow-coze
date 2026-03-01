package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.admin.OperationLog;
import com.aiflow.workflow.service.admin.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端操作日志控制器
 */
@RestController
@RequestMapping("/admin/v1/logs")
@RequiredArgsConstructor
public class AdminOperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public ResponseEntity<Page<OperationLog>> listLogs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(operationLogService.listLogs(pageable));
    }

    /**
     * 根据管理员ID查询日志
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<Page<OperationLog>> listByAdmin(
            @PathVariable Long adminId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(operationLogService.listByAdmin(adminId, pageable));
    }

    /**
     * 根据模块查询日志
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<Page<OperationLog>> listByModule(
            @PathVariable String module,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(operationLogService.listByModule(module, pageable));
    }

    /**
     * 根据时间范围查询日志
     */
    @GetMapping("/range")
    public ResponseEntity<Page<OperationLog>> listByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(operationLogService.listByDateRange(startTime, endTime, pageable));
    }

    /**
     * 获取日志详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperationLog> getLog(@PathVariable Long id) {
        return ResponseEntity.ok(operationLogService.getLog(id));
    }
}