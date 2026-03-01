package com.aiflow.workflow.security;

import com.aiflow.workflow.entity.admin.AdminPermission;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.admin.AdminPermissionRepository;
import com.aiflow.workflow.repository.admin.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 权限校验AOP切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final AdminUserRepository adminUserRepository;
    private final AdminPermissionRepository adminPermissionRepository;

    @Around("@annotation(com.aiflow.workflow.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("未登录");
        }

        Long adminId = (Long) authentication.getPrincipal();
        var adminUser = adminUserRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("管理员不存在"));

        if (adminUser.isDisabled()) {
            throw new BusinessException("账号已被禁用");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);

        String requiredPermission = annotation.value();
        List<AdminPermission> permissions = adminPermissionRepository.findByRoleId(adminUser.getRoleId());

        boolean hasPermission = permissions.stream()
                .anyMatch(p -> p.getPermissionCode().equals(requiredPermission));

        if (!hasPermission) {
            throw new BusinessException("无权限访问");
        }

        return joinPoint.proceed();
    }
}
