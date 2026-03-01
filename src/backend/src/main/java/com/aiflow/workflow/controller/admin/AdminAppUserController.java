package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.service.admin.AdminUserManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<User>> listUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserManageService.listUsers(pageable));
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(adminUserManageService.searchUsers(keyword));
    }

    /**
     * 根据状态查询用户
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> listByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(adminUserManageService.listByStatus(status));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserManageService.getUser(id));
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        adminUserManageService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        adminUserManageService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 调整用户资源点
     */
    @PutMapping("/{id}/points")
    public ResponseEntity<Void> adjustPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long points = ((Number) request.get("points")).longValue();
        String reason = (String) request.get("reason");
        adminUserManageService.adjustPoints(id, points, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量禁用用户
     */
    @PutMapping("/batch/disable")
    public ResponseEntity<Void> batchDisable(@RequestBody List<Long> ids) {
        adminUserManageService.batchDisable(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量启用用户
     */
    @PutMapping("/batch/enable")
    public ResponseEntity<Void> batchEnable(@RequestBody List<Long> ids) {
        adminUserManageService.batchEnable(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 统计用户数量
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Long>> countUsers() {
        return ResponseEntity.ok(Map.of(
                "total", adminUserManageService.countUsers(),
                "active", adminUserManageService.countActiveUsers()
        ));
    }
}
