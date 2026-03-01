package com.aiflow.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 资源点记录实体类
 * 对应数据库表 point_records
 */
@Entity
@Table(name = "point_records", indexes = {
        @Index(name = "idx_user_created", columnList = "user_id, created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    /**
     * 变动类型：1-注册赠送，2-充值获得，3-任务消耗，4-退款返还
     */
    @Column(name = "change_type", nullable = false)
    private Integer changeType;

    /**
     * 变动数量：正数为增加，负数为消耗
     */
    @Column(name = "change_amount", nullable = false)
    private Integer changeAmount;

    /**
     * 变动后余额
     */
    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @Column(name = "related_id", length = 50)
    private String relatedId;

    @Column(name = "description", length = 200)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 常量定义
    public static final int TYPE_REGISTER_GIFT = 1;
    public static final int TYPE_RECHARGE_GAIN = 2;
    public static final int TYPE_TASK_CONSUMPTION = 3;
    public static final int TYPE_REFUND_RETURN = 4;

    // 业务方法
    public boolean isIncrease() {
        return changeAmount > 0;
    }

    public boolean isDecrease() {
        return changeAmount < 0;
    }

    public String getChangeTypeText() {
        switch (changeType) {
            case TYPE_REGISTER_GIFT:
                return "注册赠送";
            case TYPE_RECHARGE_GAIN:
                return "充值获得";
            case TYPE_TASK_CONSUMPTION:
                return "任务消耗";
            case TYPE_REFUND_RETURN:
                return "退款返还";
            default:
                return "未知";
        }
    }

    /**
     * 创建资源点变动记录
     */
    public static PointRecord createRecord(User user, Integer changeType, Integer changeAmount,
                                           Long balanceAfter, String relatedId, String description) {
        return PointRecord.builder()
                .user(user)
                .changeType(changeType)
                .changeAmount(changeAmount)
                .balanceAfter(balanceAfter)
                .relatedId(relatedId)
                .description(description)
                .build();
    }
}