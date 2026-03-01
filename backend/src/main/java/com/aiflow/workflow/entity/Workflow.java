package com.aiflow.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工作流实体类
 * 对应数据库表 workflows
 */
@Entity
@Table(name = "workflows", indexes = {
        @Index(name = "idx_category_status", columnList = "category, status"),
        @Index(name = "idx_sort_order", columnList = "sort_order")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /**
     * 标签数组，存储为JSON
     */
    @Column(name = "tags", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String tags;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "preview_video_url", length = 500)
    private String previewVideoUrl;

    @Column(name = "base_points", nullable = false)
    private Integer basePoints;

    /**
     * 参数定义，存储为JSON
     */
    @Column(name = "parameter_definition", nullable = false, columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String parameterDefinition;

    @Column(name = "coze_workflow_id", nullable = false, length = 100)
    private String cozeWorkflowId;

    /**
     * 状态：0-下架，1-上架
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 业务方法
    public boolean isActive() {
        return status == 1;
    }

    public void incrementUsageCount() {
        usageCount++;
    }

    public void updateRating(BigDecimal newRating) {
        // 简化计算：实际可能需要更复杂的加权平均
        if (averageRating.equals(BigDecimal.ZERO)) {
            averageRating = newRating;
        } else {
            averageRating = averageRating.add(newRating).divide(BigDecimal.valueOf(2));
        }
    }
}