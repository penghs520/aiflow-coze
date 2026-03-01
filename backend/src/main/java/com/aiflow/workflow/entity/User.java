package com.aiflow.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 users
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "uk_phone", columnList = "phone", unique = true),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "points_balance", nullable = false)
    @Builder.Default
    private Long pointsBalance = 30000L;

    /**
     * 状态：0-禁用，1-正常，2-锁定
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 业务方法
    public boolean isDisabled() {
        return status == 0;
    }

    public boolean isLocked() {
        return status == 2;
    }

    public boolean hasSufficientPoints(Long required) {
        return pointsBalance >= required;
    }

    public void deductPoints(Long points) {
        if (pointsBalance < points) {
            throw new IllegalArgumentException("余额不足");
        }
        pointsBalance -= points;
    }

    public void addPoints(Long points) {
        pointsBalance += points;
    }
}