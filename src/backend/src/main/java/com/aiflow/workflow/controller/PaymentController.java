package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.CreateOrderRequest;
import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.entity.RechargeOrder;
import com.aiflow.workflow.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@Slf4j
@Tag(name = "支付接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "创建充值订单")
    @PostMapping
    public Result<RechargeOrder> createOrder(Authentication authentication,
                                              @Valid @RequestBody CreateOrderRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        RechargeOrder order = paymentService.createOrder(userId, request.getAmount(), request.getPaymentChannel());
        return Result.success(order);
    }

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageResponse<RechargeOrder>> getOrderList(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        PageResponse<RechargeOrder> orders = paymentService.getOrderList(userId, page, size);
        return Result.success(orders);
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/payment/callback/wechat")
    public Result<Void> wechatCallback(@RequestParam String orderId,
                                        @RequestParam String transactionId) {
        log.info("收到微信支付回调: orderId={}, transactionId={}", orderId, transactionId);
        paymentService.handlePaymentCallback(orderId, transactionId);
        return Result.success();
    }

    @Operation(summary = "支付宝支付回调")
    @PostMapping("/payment/callback/alipay")
    public Result<Void> alipayCallback(@RequestParam String orderId,
                                        @RequestParam String transactionId) {
        log.info("收到支付宝支付回调: orderId={}, transactionId={}", orderId, transactionId);
        paymentService.handlePaymentCallback(orderId, transactionId);
        return Result.success();
    }
}
