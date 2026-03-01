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
 * 管理员角色实体类
 */
@Entity
@Table(name = "admin_roles", indexes = {
        @Index(name = "uk_role_code", columnList = "role_code", unique = true),
        @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "description", length = 200)
    private String description;

    /**
     * 是否系统内置角色（不可删除）
     */
    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private Boolean isSystem = false;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isDisabled() {
        return status == 0;
    }
}
