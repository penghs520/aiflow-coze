package com.aiflow.workflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单实体类
 * 对应数据库表 orders
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_user_status", columnList = "user_id, status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeOrder {

    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    /**
     * 订单类型：1-资源点充值
     */
    @Column(name = "order_type", nullable = false)
    private Integer orderType;

    @Column(name = "package_id", length = 50)
    private String packageId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "points", nullable = false)
    private Integer points;

    /**
     * 状态：0-待支付，1-支付成功，2-支付失败，3-已退款
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "payment_channel", length = 20)
    private String paymentChannel;

    @Column(name = "payment_transaction_id", length = 100)
    private String paymentTransactionId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 常量定义
    public static final int TYPE_RECHARGE = 1;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_REFUNDED = 3;

    public static final String CHANNEL_WECHAT = "wechat";
    public static final String CHANNEL_ALIPAY = "alipay";

    // 业务方法
    public boolean isPending() {
        return status == STATUS_PENDING;
    }

    public boolean isSuccess() {
        return status == STATUS_SUCCESS;
    }

    public boolean isFailed() {
        return status == STATUS_FAILED;
    }

    public boolean isRefunded() {
        return status == STATUS_REFUNDED;
    }

    public void markAsPaid(String channel, String transactionId) {
        if (!isPending()) {
            throw new IllegalStateException("只有待支付订单可以标记为已支付");
        }
        status = STATUS_SUCCESS;
        paymentChannel = channel;
        paymentTransactionId = transactionId;
        paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        if (!isPending()) {
            throw new IllegalStateException("只有待支付订单可以标记为支付失败");
        }
        status = STATUS_FAILED;
        paidAt = LocalDateTime.now();
    }

    public void refund() {
        if (!isSuccess()) {
            throw new IllegalStateException("只有支付成功的订单可以退款");
        }
        status = STATUS_REFUNDED;
    }

    public boolean isRechargeOrder() {
        return orderType == TYPE_RECHARGE;
    }
}