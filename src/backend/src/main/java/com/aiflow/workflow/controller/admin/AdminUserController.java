package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.admin.AdminUserRequest;
import com.aiflow.workflow.dto.admin.AdminUserResponse;
import com.aiflow.workflow.service.admin.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.aiflow.workflow.dto.Result;
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
    public Result<Page<AdminUserResponse>> listAdmins(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminUserService.listAdmins(pageable));
    }

    /**
     * 根据状态查询管理员
     */
    @GetMapping("/status/{status}")
    public Result<List<AdminUserResponse>> listByStatus(@PathVariable Integer status) {
        return Result.success(adminUserService.listByStatus(status));
    }

    /**
     * 搜索管理员
     */
    @GetMapping("/search")
    public Result<List<AdminUserResponse>> searchAdmins(@RequestParam String keyword) {
        return Result.success(adminUserService.searchAdmins(keyword));
    }

    /**
     * 获取管理员详情
     */
    @GetMapping("/{id}")
    public Result<AdminUserResponse> getAdmin(@PathVariable Long id) {
        return Result.success(adminUserService.getAdmin(id));
    }

    /**
     * 创建管理员
     */
    @PostMapping
    public Result<AdminUserResponse> createAdmin(@Valid @RequestBody AdminUserRequest request) {
        return Result.success(adminUserService.createAdmin(request));
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{id}")
    public Result<AdminUserResponse> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserRequest request) {
        return Result.success(adminUserService.updateAdmin(id, request));
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminUserService.deleteAdmin(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        adminUserService.resetPassword(id, request.get("newPassword"));
        return Result.success();
    }

    /**
     * 启用管理员
     */
    @PutMapping("/{id}/enable")
    public Result<Void> enableAdmin(@PathVariable Long id) {
        adminUserService.toggleStatus(id, 1);
        return Result.success();
    }

    /**
     * 禁用管理员
     */
    @PutMapping("/{id}/disable")
    public Result<Void> disableAdmin(@PathVariable Long id) {
        adminUserService.toggleStatus(id, 0);
        return Result.success();
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        ids.forEach(adminUserService::deleteAdmin);
        return Result.success();
    }
}
