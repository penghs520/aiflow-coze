package com.aiflow.workflow.entity.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Entity
@Table(name = "operation_logs", indexes = {
        @Index(name = "idx_admin_id", columnList = "admin_id"),
        @Index(name = "idx_module", columnList = "module"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "module", nullable = false, length = 50)
    private String module;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "execution_time")
    private Long executionTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
