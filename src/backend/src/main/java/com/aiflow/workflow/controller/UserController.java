package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.dto.UserResponse;
import com.aiflow.workflow.service.PointService;
import com.aiflow.workflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PointService pointService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/me")
    public Result<Void> updateUser(Authentication authentication,
                                    @RequestParam(required = false) String nickname,
                                    @RequestParam(required = false) String avatarUrl) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateUserInfo(userId, nickname, avatarUrl);
        return Result.success();
    }

    @Operation(summary = "获取资源点余额")
    @GetMapping("/me/points")
    public Result<Map<String, Long>> getPoints(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long balance = pointService.getBalance(userId);

        Map<String, Long> data = new HashMap<>();
        data.put("balance", balance);

        return Result.success(data);
    }
}
