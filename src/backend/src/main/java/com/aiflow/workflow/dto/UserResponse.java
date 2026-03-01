package com.aiflow.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
public class UserResponse {

    private Long id;
    private String phone;
    private String nickname;
    private String avatarUrl;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
