package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理端用户管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserManageService {

    private final UserRepository userRepository;

    /**
     * 分页查询用户
     */
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 根据状态查询用户
     */
    public List<User> listByStatus(Integer status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 搜索用户（根据昵称）
     */
    public List<User> searchUsers(String keyword) {
        return userRepository.findByNicknameContaining(keyword);
    }

    /**
     * 获取用户详情
     */
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 禁用用户
     */
    @Transactional
    public void disableUser(Long id) {
        User user = getUser(id);
        user.setStatus(0);
        userRepository.save(user);
        log.info("管理员禁用用户: {}", id);
    }

    /**
     * 启用用户
     */
    @Transactional
    public void enableUser(Long id) {
        User user = getUser(id);
        user.setStatus(1);
        userRepository.save(user);
        log.info("管理员启用用户: {}", id);
    }

    /**
     * 调整用户资源点
     */
    @Transactional
    public void adjustPoints(Long id, Long points, String reason) {
        User user = getUser(id);
        Long newBalance = user.getPointsBalance() + points;

        if (newBalance < 0) {
            throw new RuntimeException("资源点余额不足");
        }

        user.setPointsBalance(newBalance);
        userRepository.save(user);

        log.info("管理员调整用户资源点: userId={}, points={}, reason={}", id, points, reason);
    }

    /**
     * 批量禁用用户
     */
    @Transactional
    public void batchDisable(List<Long> ids) {
        ids.forEach(this::disableUser);
    }

    /**
     * 批量启用用户
     */
    @Transactional
    public void batchEnable(List<Long> ids) {
        ids.forEach(this::enableUser);
    }

    /**
     * 统计用户数量
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * 统计活跃用户数量
     */
    public long countActiveUsers() {
        return userRepository.countByStatus(1);
    }
}
