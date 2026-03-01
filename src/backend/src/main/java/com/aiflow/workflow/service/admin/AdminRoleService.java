package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.dto.admin.AdminRoleRequest;
import com.aiflow.workflow.entity.admin.AdminRole;
import com.aiflow.workflow.entity.admin.AdminRolePermission;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.admin.AdminRolePermissionRepository;
import com.aiflow.workflow.repository.admin.AdminRoleRepository;
import com.aiflow.workflow.repository.admin.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final AdminRoleRepository adminRoleRepository;
    private final AdminRolePermissionRepository rolePermissionRepository;
    private final AdminUserRepository adminUserRepository;

    public List<AdminRole> getAllRoles() {
        return adminRoleRepository.findAll();
    }

    public List<AdminRole> getActiveRoles() {
        return adminRoleRepository.findByStatus(1);
    }

    public AdminRole getRoleById(Long id) {
        return adminRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
    }

    @Transactional
    public AdminRole createRole(AdminRoleRequest request) {
        if (adminRoleRepository.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException("角色编码已存在");
        }

        AdminRole role = AdminRole.builder()
                .roleName(request.getRoleName())
                .roleCode(request.getRoleCode())
                .description(request.getDescription())
                .isSystem(false)
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();

        return adminRoleRepository.save(role);
    }

    @Transactional
    public AdminRole updateRole(Long id, AdminRoleRequest request) {
        AdminRole role = adminRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if (role.getIsSystem()) {
            throw new BusinessException("系统内置角色不可修改");
        }

        if (!role.getRoleCode().equals(request.getRoleCode())) {
            if (adminRoleRepository.existsByRoleCode(request.getRoleCode())) {
                throw new BusinessException("角色编码已存在");
            }
            role.setRoleCode(request.getRoleCode());
        }

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }

        return adminRoleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        AdminRole role = adminRoleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if (role.getIsSystem()) {
            throw new BusinessException("系统内置角色不可删除");
        }

        long userCount = adminUserRepository.countByRoleId(id);
        if (userCount > 0) {
            throw new BusinessException("该角色下还有管理员，无法删除");
        }

        rolePermissionRepository.deleteByRoleId(id);
        adminRoleRepository.deleteById(id);
    }

    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        AdminRole role = adminRoleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("角色不存在"));

        rolePermissionRepository.deleteByRoleId(roleId);

        List<AdminRolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> AdminRolePermission.builder()
                        .roleId(roleId)
                        .permissionId(permissionId)
                        .build())
                .collect(Collectors.toList());

        rolePermissionRepository.saveAll(rolePermissions);
    }

    public List<Long> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId).stream()
                .map(AdminRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }
}
