package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.service.admin.AdminUserManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.aiflow.workflow.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端用户管理控制器
 */
@RestController
@RequestMapping("/admin/v1/users")
@RequiredArgsConstructor
public class AdminAppUserController {

    private final AdminUserManageService adminUserManageService;

    /**
     * 分页查询用户
     */
    @GetMapping
    public Result<Page<User>> listUsers(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminUserManageService.listUsers(pageable));
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        return Result.success(adminUserManageService.searchUsers(keyword));
    }

    /**
     * 根据状态查询用户
     */
    @GetMapping("/status/{status}")
    public Result<List<User>> listByStatus(@PathVariable Integer status) {
        return Result.success(adminUserManageService.listByStatus(status));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(adminUserManageService.getUser(id));
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    public Result<Void> disableUser(@PathVariable Long id) {
        adminUserManageService.disableUser(id);
        return Result.success();
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    public Result<Void> enableUser(@PathVariable Long id) {
        adminUserManageService.enableUser(id);
        return Result.success();
    }

    /**
     * 调整用户资源点
     */
    @PutMapping("/{id}/points")
    public Result<Void> adjustPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long points = ((Number) request.get("points")).longValue();
        String reason = (String) request.get("reason");
        adminUserManageService.adjustPoints(id, points, reason);
        return Result.success();
    }

    /**
     * 批量禁用用户
     */
    @PutMapping("/batch/disable")
    public Result<Void> batchDisable(@RequestBody List<Long> ids) {
        adminUserManageService.batchDisable(ids);
        return Result.success();
    }

    /**
     * 批量启用用户
     */
    @PutMapping("/batch/enable")
    public Result<Void> batchEnable(@RequestBody List<Long> ids) {
        adminUserManageService.batchEnable(ids);
        return Result.success();
    }

    /**
     * 统计用户数量
     */
    @GetMapping("/stats/count")
    public Result<Map<String, Long>> countUsers() {
        return Result.success(Map.of(
                "total", adminUserManageService.countUsers(),
                "active", adminUserManageService.countActiveUsers()
        ));
    }
}
