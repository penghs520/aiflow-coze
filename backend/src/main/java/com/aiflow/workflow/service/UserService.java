package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.UserResponse;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.exception.AuthException;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.repository.UserRepository;
import com.aiflow.workflow.util.JwtUtil;
import com.aiflow.workflow.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SmsService smsService;
    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;
    private static final int INITIAL_POINTS = 30000;

    /**
     * 发送验证码
     */
    public void sendVerificationCode(String phone) {
        // 生成验证码
        String code = smsService.generateCode();

        // 存入Redis
        String key = SMS_CODE_PREFIX + phone;
        redisUtil.setWithExpire(key, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 发送短信
        boolean success = smsService.sendCode(phone, code);
        if (!success) {
            throw new BusinessException("验证码发送失败");
        }

        log.info("验证码已发送到 {}", phone);
    }

    /**
     * 登录
     */
    @Transactional
    public String login(String phone, String code) {
        // 验证验证码
        String key = SMS_CODE_PREFIX + phone;
        Object storedCode = redisUtil.get(key);

        if (storedCode == null || !storedCode.toString().equals(code)) {
            throw new AuthException("验证码错误或已过期");
        }

        // 查找或创建用户
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> register(phone));

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 删除验证码
        redisUtil.delete(key);

        // 生成Token
        return jwtUtil.generateToken(user.getId());
    }

    /**
     * 注册
     */
    private User register(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + phone.substring(7));
        user.setPointsBalance((long) INITIAL_POINTS);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * 获取用户信息
     */
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUserInfo(Long userId, String nickname, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        userRepository.save(user);
    }
}

