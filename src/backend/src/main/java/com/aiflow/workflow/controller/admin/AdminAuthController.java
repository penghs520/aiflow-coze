package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.dto.admin.AdminLoginRequest;
import com.aiflow.workflow.dto.admin.AdminUserResponse;
import com.aiflow.workflow.service.admin.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理端认证接口")
@RestController
@RequestMapping("/admin/v1/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(
            @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String token = adminAuthService.login(request, ipAddress);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");

        return Result.success(data);
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/me")
    public Result<AdminUserResponse> getAdminInfo(Authentication authentication) {
        Long adminId = (Long) authentication.getPrincipal();
        AdminUserResponse response = adminAuthService.getAdminInfo(adminId);
        return Result.success(response);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication) {
        Long adminId = (Long) authentication.getPrincipal();
        adminAuthService.changePassword(adminId, oldPassword, newPassword);
        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
