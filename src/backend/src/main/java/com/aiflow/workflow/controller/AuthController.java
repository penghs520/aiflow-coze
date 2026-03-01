package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.LoginRequest;
import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.dto.SendCodeRequest;
import com.aiflow.workflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "发送验证码")
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        userService.sendVerificationCode(request.getPhone());
        return Result.success();
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request.getPhone(), request.getCode());

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenType", "Bearer");

        return Result.success(data);
    }
}
