package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.admin.AdminPermission;
import com.aiflow.workflow.service.admin.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Map<String, Object>>> getPermissionTree() {
        return ResponseEntity.ok(adminPermissionService.getPermissionTree());
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping
    public ResponseEntity<List<AdminPermission>> listPermissions() {
        return ResponseEntity.ok(adminPermissionService.listAllPermissions());
    }

    /**
     * 根据模块查询权限
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<List<AdminPermission>> listByModule(@PathVariable String module) {
        return ResponseEntity.ok(adminPermissionService.listByModule(module));
    }

    /**
     * 根据角色ID获取权限
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<AdminPermission>> getPermissionsByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(adminPermissionService.getPermissionsByRole(roleId));
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminPermission> getPermission(@PathVariable Long id) {
        return ResponseEntity.ok(adminPermissionService.getPermission(id));
    }
}
