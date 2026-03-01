package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.admin.AdminPermission;
import com.aiflow.workflow.repository.admin.AdminPermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPermissionService {

    private final AdminPermissionRepository adminPermissionRepository;

    public List<Map<String, Object>> getPermissionTree() {
        List<AdminPermission> allPermissions = adminPermissionRepository.findAll();

        List<AdminPermission> rootPermissions = allPermissions.stream()
                .filter(p -> p.getParentId() == null)
                .sorted(Comparator.comparing(AdminPermission::getSortOrder))
                .collect(Collectors.toList());

        return rootPermissions.stream()
                .map(root -> buildPermissionNode(root, allPermissions))
                .collect(Collectors.toList());
    }

    public List<AdminPermission> getPermissionsByRole(Long roleId) {
        return adminPermissionRepository.findByRoleId(roleId);
    }

    public List<AdminPermission> getAllPermissions() {
        return adminPermissionRepository.findAll();
    }

    private Map<String, Object> buildPermissionNode(AdminPermission permission, List<AdminPermission> allPermissions) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", permission.getId());
        node.put("permissionName", permission.getPermissionName());
        node.put("permissionCode", permission.getPermissionCode());
        node.put("module", permission.getModule());
        node.put("action", permission.getAction());
        node.put("description", permission.getDescription());
        node.put("sortOrder", permission.getSortOrder());

        List<AdminPermission> children = allPermissions.stream()
                .filter(p -> permission.getId().equals(p.getParentId()))
                .sorted(Comparator.comparing(AdminPermission::getSortOrder))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            node.put("children", children.stream()
                    .map(child -> buildPermissionNode(child, allPermissions))
                    .collect(Collectors.toList()));
        }

        return node;
    }
}
