package com.aiflow.workflow.entity.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 管理员用户实体类
 */
@Entity
@Table(name = "admin_users", indexes = {
        @Index(name = "uk_username", columnList = "username", unique = true),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_role_id", columnList = "role_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    @Column(name = "login_fail_count", nullable = false)
    @Builder.Default
    private Integer loginFailCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isDisabled() {
        return status == 0;
    }

    public void incrementLoginFailCount() {
        this.loginFailCount++;
    }

    public void resetLoginFailCount() {
        this.loginFailCount = 0;
    }
}
