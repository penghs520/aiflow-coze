package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.admin.AdminRoleRequest;
import com.aiflow.workflow.entity.admin.AdminPermission;
import com.aiflow.workflow.entity.admin.AdminRole;
import com.aiflow.workflow.service.admin.AdminRoleService;
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
 * 管理端角色管理控制器
 */
@RestController
@RequestMapping("/admin/v1/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    /**
     * 分页查询角色
     */
    @GetMapping
    public Result<Page<AdminRole>> listRoles(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminRoleService.listRoles(pageable));
    }

    /**
     * 根据状态查询角色
     */
    @GetMapping("/status/{status}")
    public Result<List<AdminRole>> listByStatus(@PathVariable Integer status) {
        return Result.success(adminRoleService.listByStatus(status));
    }

    /**
     * 获取所有活跃角色
     */
    @GetMapping("/active")
    public Result<List<AdminRole>> listActiveRoles() {
        return Result.success(adminRoleService.listActiveRoles());
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<AdminRole> getRole(@PathVariable Long id) {
        return Result.success(adminRoleService.getRole(id));
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<AdminRole> createRole(@Valid @RequestBody AdminRoleRequest request) {
        return Result.success(adminRoleService.createRole(request));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<AdminRole> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoleRequest request) {
        return Result.success(adminRoleService.updateRole(id, request));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public Result<List<AdminPermission>> getRolePermissions(@PathVariable Long id) {
        return Result.success(adminRoleService.getRolePermissions(id));
    }

    /**
     * 分配权限给角色
     */
    @PutMapping("/{id}/permissions")
    public Result<Void> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        adminRoleService.assignPermissions(id, permissionIds);
        return Result.success();
    }

    /**
     * 启用角色
     */
    @PutMapping("/{id}/enable")
    public Result<Void> enableRole(@PathVariable Long id) {
        adminRoleService.toggleStatus(id, 1);
        return Result.success();
    }

    /**
     * 禁用角色
     */
    @PutMapping("/{id}/disable")
    public Result<Void> disableRole(@PathVariable Long id) {
        adminRoleService.toggleStatus(id, 0);
        return Result.success();
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        ids.forEach(adminRoleService::deleteRole);
        return Result.success();
    }
}
