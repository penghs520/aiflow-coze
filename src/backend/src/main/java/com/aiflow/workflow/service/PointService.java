package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.entity.PointRecord;
import com.aiflow.workflow.entity.User;
import com.aiflow.workflow.exception.BusinessException;
import com.aiflow.workflow.exception.InsufficientPointsException;
import com.aiflow.workflow.repository.PointRecordRepository;
import com.aiflow.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 资源点服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRecordRepository pointRecordRepository;
    private final UserRepository userRepository;

    /**
     * 扣除资源点
     */
    @Transactional
    public void deductPoints(Long userId, Integer amount, String taskId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (user.getPointsBalance() < amount) {
            throw new InsufficientPointsException("资源点不足，当前余额: " + user.getPointsBalance());
        }

        // 扣除资源点
        user.setPointsBalance(user.getPointsBalance() - amount);
        userRepository.save(user);

        // 记录变动
        PointRecord record = new PointRecord();
        record.setUser(user);
        record.setChangeAmount(-amount);
        record.setChangeType(3); // 任务消耗
        record.setBalanceAfter(user.getPointsBalance());
        record.setRelatedId(taskId);
        record.setDescription("任务消耗资源点");
        record.setCreatedAt(LocalDateTime.now());
        pointRecordRepository.save(record);

        log.info("用户 {} 扣除资源点 {}, 剩余 {}", userId, amount, user.getPointsBalance());
    }

    /**
     * 增加资源点
     */
    @Transactional
    public void addPoints(Long userId, Integer amount, Integer type, String relatedId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 增加资源点
        user.setPointsBalance(user.getPointsBalance() + amount);
        userRepository.save(user);

        // 记录变动
        PointRecord record = new PointRecord();
        record.setUser(user);
        record.setChangeAmount(amount);
        record.setChangeType(type);
        record.setBalanceAfter(user.getPointsBalance());
        record.setRelatedId(relatedId);
        record.setDescription(getTypeDescription(type));
        record.setCreatedAt(LocalDateTime.now());
        pointRecordRepository.save(record);

        log.info("用户 {} 增加资源点 {}, 当前余额 {}", userId, amount, user.getPointsBalance());
    }

    /**
     * 获取资源点记录
     */
    public PageResponse<PointRecord> getPointRecords(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PointRecord> recordPage = pointRecordRepository.findByUser_Id(userId, pageable);

        return new PageResponse<>(
                recordPage.getContent(),
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.hasNext(),
                recordPage.hasPrevious()
        );
    }

    /**
     * 获取余额
     */
    public Long getBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return user.getPointsBalance();
    }

    private String getTypeDescription(Integer type) {
        return switch (type) {
            case 1 -> "注册赠送";
            case 2 -> "充值获得";
            case 3 -> "任务消耗";
            case 4 -> "退款返还";
            default -> "其他";
        };
    }
}
