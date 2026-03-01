package com.aiflow.workflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequest {

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付渠道(wechat/alipay)
     */
    @NotBlank(message = "支付渠道不能为空")
    private String paymentChannel;
}
