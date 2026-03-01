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
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<AdminRole>> listRoles(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminRoleService.listRoles(pageable));
    }

    /**
     * 根据状态查询角色
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AdminRole>> listByStatus(@PathVariable Integer status) {
        return ResponseEntity.ok(adminRoleService.listByStatus(status));
    }

    /**
     * 获取所有活跃角色
     */
    @GetMapping("/active")
    public ResponseEntity<List<AdminRole>> listActiveRoles() {
        return ResponseEntity.ok(adminRoleService.listActiveRoles());
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdminRole> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(adminRoleService.getRole(id));
    }

    /**
     * 创建角色
     */
    @PostMapping
    public ResponseEntity<AdminRole> createRole(@Valid @RequestBody AdminRoleRequest request) {
        return ResponseEntity.ok(adminRoleService.createRole(request));
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ResponseEntity<AdminRole> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoleRequest request) {
        return ResponseEntity.ok(adminRoleService.updateRole(id, request));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<AdminPermission>> getRolePermissions(@PathVariable Long id) {
        return ResponseEntity.ok(adminRoleService.getRolePermissions(id));
    }

    /**
     * 分配权限给角色
     */
    @PutMapping("/{id}/permissions")
    public ResponseEntity<Void> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        adminRoleService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 启用角色
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableRole(@PathVariable Long id) {
        adminRoleService.toggleStatus(id, 1);
        return ResponseEntity.ok().build();
    }

    /**
     * 禁用角色
     */
    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableRole(@PathVariable Long id) {
        adminRoleService.toggleStatus(id, 0);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        ids.forEach(adminRoleService::deleteRole);
        return ResponseEntity.ok().build();
    }
}
