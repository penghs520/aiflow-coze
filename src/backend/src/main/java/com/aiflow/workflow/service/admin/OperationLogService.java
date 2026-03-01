package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.admin.OperationLog;
import com.aiflow.workflow.repository.admin.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogRepository operationLogRepository;

    /**
     * 记录操作日志
     */
    @Transactional
    public void log(Long adminId, String module, String action, String targetType, Long targetId,
                    String description, String requestMethod, String requestUrl, String requestParams,
                    Integer responseCode, String ipAddress, Long executionTime) {
        OperationLog log = OperationLog.builder()
                .adminId(adminId)
                .module(module)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .requestMethod(requestMethod)
                .requestUrl(requestUrl)
                .requestParams(requestParams)
                .responseCode(responseCode)
                .ipAddress(ipAddress)
                .executionTime(executionTime)
                .build();

        operationLogRepository.save(log);
    }

    /**
     * 简化的日志记录方法
     */
    @Transactional
    public void log(Long adminId, String module, String action, String description) {
        log(adminId, module, action, null, null, description, null, null, null, null, null, null);
    }

    /**
     * 分页查询操作日志
     */
    public Page<OperationLog> listLogs(Pageable pageable) {
        return operationLogRepository.findAll(pageable);
    }

    /**
     * 根据管理员ID查询日志
     */
    public Page<OperationLog> listByAdmin(Long adminId, Pageable pageable) {
        return operationLogRepository.findByAdminId(adminId, pageable);
    }

    /**
     * 根据模块查询日志
     */
    public Page<OperationLog> listByModule(String module, Pageable pageable) {
        return operationLogRepository.findByModule(module, pageable);
    }

    /**
     * 根据时间范围查询日志
     */
    public Page<OperationLog> listByDateRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return operationLogRepository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    /**
     * 根据管理员和模块查询日志
     */
    public Page<OperationLog> listByAdminAndModule(Long adminId, String module, Pageable pageable) {
        return operationLogRepository.findByConditions(adminId, module, null, null, pageable);
    }

    /**
     * 获取日志详情
     */
    public OperationLog getLog(Long id) {
        return operationLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日志不存在"));
    }

    /**
     * 清理过期日志（保留最近N天）
     */
    @Transactional
    public void cleanOldLogs(int daysToKeep) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        operationLogRepository.findByConditions(null, null, null, cutoffTime,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                .forEach(operationLogRepository::delete);
        log.info("清理了过期日志，保留最近{}天", daysToKeep);
    }
}
