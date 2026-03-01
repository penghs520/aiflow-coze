package com.aiflow.workflow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 短信服务(模拟模式)
 */
@Slf4j
@Service
public class SmsService {

    @Value("${app.sms.simulate:true}")
    private boolean simulate;

    private final Random random = new Random();

    /**
     * 发送验证码
     */
    public boolean sendCode(String phone, String code) {
        if (simulate) {
            log.info("【模拟短信】发送验证码到 {}: {}", phone, code);
            return true;
        }

        // 真实短信发送逻辑
        // 集成阿里云短信服务等
        return false;
    }

    /**
     * 生成6位验证码
     */
    public String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
