package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.entity.RechargeOrder;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.RechargeOrderRepository;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RechargeOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final IdGenerator idGenerator;

    private static final int POINTS_PER_YUAN = 1000; // 1元=1000资源点

    /**
     * 创建充值订单
     */
    @Transactional
    public RechargeOrder createOrder(Long userId, BigDecimal amount, String paymentChannel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 计算资源点
        int points = amount.multiply(BigDecimal.valueOf(POINTS_PER_YUAN)).intValue();

        // 创建订单
        RechargeOrder order = new RechargeOrder();
        order.setId(String.valueOf(idGenerator.nextId()));
        order.setUser(user);
        order.setOrderType(RechargeOrder.TYPE_RECHARGE);
        order.setAmount(amount);
        order.setPoints(points);
        order.setPaymentChannel(paymentChannel);
        order.setStatus(0); // 待支付
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    /**
     * 处理支付回调
     */
    @Transactional
    public void handlePaymentCallback(String orderId, String transactionId) {
        RechargeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() != 0) {
            log.warn("订单状态异常: {}", order.getStatus());
            return;
        }

        // 更新订单状态
        order.setStatus(1); // 已支付
        order.setPaymentTransactionId(transactionId);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // 增加资源点
        pointService.addPoints(order.getUser().getId(), order.getPoints(), 2, order.getId());

        log.info("订单 {} 支付成功，用户 {} 获得 {} 资源点", orderId, order.getUser().getId(), order.getPoints());
    }

    /**
     * 获取订单列表
     */
    public PageResponse<RechargeOrder> getOrderList(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RechargeOrder> orderPage = orderRepository.findByUser_Id(userId, pageable);

        return new PageResponse<>(
                orderPage.getContent(),
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.hasNext(),
                orderPage.hasPrevious()
        );
    }
}
