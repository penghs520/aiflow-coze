package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.dto.admin.AdminUserRequest;
import com.aiflow.workflow.dto.admin.AdminUserResponse;
import com.aiflow.workflow.entity.admin.AdminRole;
import com.aiflow.workflow.entity.admin.AdminUser;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.admin.AdminRoleRepository;
import com.aiflow.workflow.repository.admin.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<AdminUserResponse> getAdminList(String keyword, Pageable pageable) {
        Page<AdminUser> adminPage;
        if (keyword != null && !keyword.isEmpty()) {
            adminPage = adminUserRepository.findByRealNameContainingOrUsernameContaining(
                    keyword, keyword, pageable);
        } else {
            adminPage = adminUserRepository.findAll(pageable);
        }

        return adminPage.map(this::convertToResponse);
    }

    public AdminUserResponse getAdminById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));
        return convertToResponse(adminUser);
    }

    @Transactional
    public AdminUserResponse createAdmin(AdminUserRequest request) {
        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        if (!adminRoleRepository.existsById(request.getRoleId())) {
            throw new BusinessException("角色不存在");
        }

        AdminUser adminUser = AdminUser.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .realName(request.getRealName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .avatarUrl(request.getAvatarUrl())
                .roleId(request.getRoleId())
                .status(request.getStatus() != null ? request.getStatus() : 1)
                .build();

        adminUser = adminUserRepository.save(adminUser);
        return convertToResponse(adminUser);
    }

    @Transactional
    public AdminUserResponse updateAdmin(Long id, AdminUserRequest request) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        if (!adminUser.getUsername().equals(request.getUsername())) {
            if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new BusinessException("用户名已存在");
            }
            adminUser.setUsername(request.getUsername());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (!adminRoleRepository.existsById(request.getRoleId())) {
            throw new BusinessException("角色不存在");
        }

        adminUser.setRealName(request.getRealName());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhone(request.getPhone());
        adminUser.setAvatarUrl(request.getAvatarUrl());
        adminUser.setRoleId(request.getRoleId());
        if (request.getStatus() != null) {
            adminUser.setStatus(request.getStatus());
        }

        adminUser = adminUserRepository.save(adminUser);
        return convertToResponse(adminUser);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        long adminCount = adminUserRepository.countByStatus(1);
        if (adminCount <= 1 && adminUser.getStatus() == 1) {
            throw new BusinessException("至少保留一个启用的管理员");
        }

        adminUserRepository.deleteById(id);
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        adminUser.setPasswordHash(passwordEncoder.encode(newPassword));
        adminUserRepository.save(adminUser);
    }

    @Transactional
    public void toggleStatus(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        if (adminUser.getStatus() == 1) {
            long activeCount = adminUserRepository.countByStatus(1);
            if (activeCount <= 1) {
                throw new BusinessException("至少保留一个启用的管理员");
            }
        }

        adminUser.setStatus(adminUser.getStatus() == 1 ? 0 : 1);
        adminUserRepository.save(adminUser);
    }

    public List<AdminUserResponse> listByStatus(Integer status) {
        return adminUserRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AdminUserResponse> searchAdmins(String keyword) {
        Page<AdminUser> page = adminUserRepository.findByRealNameContainingOrUsernameContaining(
                keyword, keyword, org.springframework.data.domain.PageRequest.of(0, 1000));
        return page.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public AdminUserResponse getAdmin(Long id) {
        return getAdminById(id);
    }

    public Page<AdminUserResponse> listAdmins(Pageable pageable) {
        return getAdminList(null, pageable);
    }

    @Transactional
    public void toggleStatus(Long id, Integer status) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        if (status == 0 && adminUser.getStatus() == 1) {
            long activeCount = adminUserRepository.countByStatus(1);
            if (activeCount <= 1) {
                throw new BusinessException("至少保留一个启用的管理员");
            }
        }

        adminUser.setStatus(status);
        adminUserRepository.save(adminUser);
    }

    private AdminUserResponse convertToResponse(AdminUser adminUser) {
        AdminRole role = adminRoleRepository.findById(adminUser.getRoleId()).orElse(null);

        return AdminUserResponse.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .realName(adminUser.getRealName())
                .email(adminUser.getEmail())
                .phone(adminUser.getPhone())
                .avatarUrl(adminUser.getAvatarUrl())
                .roleId(adminUser.getRoleId())
                .roleName(role != null ? role.getRoleName() : null)
                .status(adminUser.getStatus())
                .lastLoginAt(adminUser.getLastLoginAt())
                .lastLoginIp(adminUser.getLastLoginIp())
                .createdAt(adminUser.getCreatedAt())
                .build();
    }
}
