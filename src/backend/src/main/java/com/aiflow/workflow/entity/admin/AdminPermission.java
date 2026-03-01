package com.aiflow.workflow.entity.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 管理员权限实体类
 */
@Entity
@Table(name = "admin_permissions", indexes = {
        @Index(name = "uk_permission_code", columnList = "permission_code", unique = true),
        @Index(name = "idx_parent_id", columnList = "parent_id"),
        @Index(name = "idx_module", columnList = "module")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    @Column(name = "permission_code", nullable = false, unique = true, length = 100)
    private String permissionCode;

    @Column(name = "module", nullable = false, length = 50)
    private String module;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
