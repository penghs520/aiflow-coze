package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.admin.AdminUserRequest;
import com.aiflow.workflow.dto.admin.AdminUserResponse;
import com.aiflow.workflow.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端管理员管理控制器
 */
@RestController
@RequestMapping("/admin/v1/admins")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 分页查询管理员
     */
    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> listAdmins(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminUserService.listAdmins(pageable));
    }

    /**
     * 根据状态查询管理员
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdminUserResponse>> listByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(adminUserService.listByStatus(status));
    }

    /**
     * 搜索管理员
     */
    @GetMapping("/search")
    public ResponseEntity<List<AdminUserResponse>> searchAdmins(@RequestParam String keyword) {
        return ResponseEntity.ok(adminUserService.searchAdmins(keyword));
    }

    /**
     * 获取管理员详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getAdmin(id));
    }

    /**
     * 创建管理员
     */
    @PostMapping
    public ResponseEntity<AdminUserResponse> createAdmin(@Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminUserService.createAdmin(request));
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminUserResponse> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(adminUserService.updateAdmin(id, request));
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminUserService.deleteAdmin(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        adminUserService.resetPassword(id, request.get("newPassword"));
        return ResponseEntity.ok().build();
    }

    /**
     * 启用管理员
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableAdmin(@PathVariable Long id) {
        adminUserService.toggleStatus(id, 1);
        return ResponseEntity.ok().build();
    }

    /**
     * 禁用管理员
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableAdmin(@PathVariable Long id) {
        adminUserService.toggleStatus(id, 0);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        ids.forEach(adminUserService::deleteAdmin);
        return ResponseEntity.ok().build();
    }
}
