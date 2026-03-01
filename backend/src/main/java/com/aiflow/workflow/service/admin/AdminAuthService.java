package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.dto.admin.AdminLoginRequest;
import com.aiflow.workflow.dto.admin.AdminUserResponse;
import com.aiflow.workflow.entity.admin.AdminRole;
import com.aiflow.workflow.entity.admin.AdminUser;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.admin.AdminRoleRepository;
import com.aiflow.workflow.repository.admin.AdminUserRepository;
import com.aiflow.workflow.util.AdminJwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final AdminRoleRepository adminRoleRepository;
    private final AdminJwtUtil adminJwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String login(AdminLoginRequest request, String ipAddress) {
        AdminUser adminUser = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (adminUser.isDisabled()) {
            throw new BusinessException("账号已被禁用");
        }

        if (adminUser.getLoginFailCount() >= 5) {
            throw new BusinessException("登录失败次数过多，账号已锁定");
        }

        if (!passwordEncoder.matches(request.getPassword(), adminUser.getPasswordHash())) {
            adminUser.incrementLoginFailCount();
            adminUserRepository.save(adminUser);
            throw new BusinessException("用户名或密码错误");
        }

        adminUser.resetLoginFailCount();
        adminUser.setLastLoginAt(LocalDateTime.now());
        adminUser.setLastLoginIp(ipAddress);
        adminUserRepository.save(adminUser);

        return adminJwtUtil.generateToken(adminUser.getId());
    }

    public AdminUserResponse getAdminInfo(Long adminId) {
        AdminUser adminUser = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        AdminRole role = adminRoleRepository.findById(adminUser.getRoleId())
                .orElse(null);

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

    @Transactional
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        AdminUser adminUser = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        if (!passwordEncoder.matches(oldPassword, adminUser.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }

        adminUser.setPasswordHash(passwordEncoder.encode(newPassword));
        adminUserRepository.save(adminUser);
    }
}
