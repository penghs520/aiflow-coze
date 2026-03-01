package com.aiflow.workflow.repository;

import com.aiflow.workflow.entity.RechargeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RechargeOrderRepository extends JpaRepository<RechargeOrder, String> {

    /**
     * 根据用户ID查找订单
     */
    List<RechargeOrder> findByUser_Id(Long userId);

    /**
     * 根据用户ID和状态查找订单
     */
    List<RechargeOrder> findByUser_IdAndStatus(Long userId, Integer status);

    /**
     * 根据用户ID分页查找订单
     */
    Page<RechargeOrder> findByUser_Id(Long userId, Pageable pageable);

    /**
     * 根据状态查找订单
     */
    List<RechargeOrder> findByStatus(Integer status);

    /**
     * 根据支付渠道和状态查找订单
     */
    List<RechargeOrder> findByPaymentChannelAndStatus(String paymentChannel, Integer status);

    /**
     * 根据创建时间范围查找订单
     */
    List<RechargeOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据支付交易ID查找订单
     */
    Optional<RechargeOrder> findByPaymentTransactionId(String transactionId);

    /**
     * 统计用户订单总金额
     */
    @Query("SELECT SUM(o.amount) FROM RechargeOrder o WHERE o.user.id = :userId AND o.status = 1")
    Optional<BigDecimal> sumAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计用户充值总点数
     */
    @Query("SELECT SUM(o.points) FROM RechargeOrder o WHERE o.user.id = :userId AND o.status = 1")
    Optional<Integer> sumPointsByUserId(@Param("userId") Long userId);

    /**
     * 查找待支付的超时订单
     */
    @Query("SELECT o FROM RechargeOrder o WHERE o.status = 0 AND o.createdAt < :expireTime")
    List<RechargeOrder> findExpiredOrders(@Param("expireTime") LocalDateTime expireTime);
}