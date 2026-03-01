package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.admin.AdminPermission;
import com.aiflow.workflow.service.admin.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import com.aiflow.workflow.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端权限管理控制器
 */
@RestController
@RequestMapping("/admin/v1/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> getPermissionTree() {
        return Result.success(adminPermissionService.getPermissionTree());
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping
    public Result<List<AdminPermission>> listPermissions() {
        return Result.success(adminPermissionService.listAllPermissions());
    }

    /**
     * 根据模块查询权限
     */
    @GetMapping("/module/{module}")
    public Result<List<AdminPermission>> listByModule(@PathVariable String module) {
        return Result.success(adminPermissionService.listByModule(module));
    }

    /**
     * 根据角色ID获取权限
     */
    @GetMapping("/role/{roleId}")
    public Result<List<AdminPermission>> getPermissionsByRole(@PathVariable Long roleId) {
        return Result.success(adminPermissionService.getPermissionsByRole(roleId));
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public Result<AdminPermission> getPermission(@PathVariable Long id) {
        return Result.success(adminPermissionService.getPermission(id));
    }
}
