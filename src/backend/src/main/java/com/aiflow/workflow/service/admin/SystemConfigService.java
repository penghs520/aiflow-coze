package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.entity.admin.SystemConfig;
import com.aiflow.workflow.repository.admin.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;

    /**
     * 分页查询配置
     */
    public Page<SystemConfig> listConfigs(Pageable pageable) {
        return systemConfigRepository.findAll(pageable);
    }

    /**
     * 根据类型查询配置
     */
    public List<SystemConfig> listByType(String configType) {
        return systemConfigRepository.findByConfigType(configType);
    }

    /**
     * 根据配置键获取配置
     */
    public SystemConfig getByKey(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new RuntimeException("配置不存在: " + configKey));
    }

    /**
     * 获取配置值
     */
    public String getConfigValue(String configKey) {
        return getByKey(configKey).getConfigValue();
    }

    /**
     * 获取配置值（带默认值）
     */
    public String getConfigValue(String configKey, String defaultValue) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * 创建配置
     */
    @Transactional
    public SystemConfig createConfig(String configKey, String configValue, String configType, String description) {
        // 检查配置键是否已存在
        if (systemConfigRepository.findByConfigKey(configKey).isPresent()) {
            throw new RuntimeException("配置键已存在: " + configKey);
        }

        SystemConfig config = SystemConfig.builder()
                .configKey(configKey)
                .configValue(configValue)
                .configType(configType)
                .description(description)
                .isSystem(false)
                .build();

        return systemConfigRepository.save(config);
    }

    /**
     * 更新配置
     */
    @Transactional
    public SystemConfig updateConfig(String configKey, String configValue) {
        SystemConfig config = getByKey(configKey);
        config.setConfigValue(configValue);
        return systemConfigRepository.save(config);
    }

    /**
     * 更新配置（完整）
     */
    @Transactional
    public SystemConfig updateConfig(String configKey, String configValue, String configType, String description) {
        SystemConfig config = getByKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType);
        config.setDescription(description);
        return systemConfigRepository.save(config);
    }

    /**
     * 删除配置
     */
    @Transactional
    public void deleteConfig(String configKey) {
        SystemConfig config = getByKey(configKey);

        // 系统内置配置不可删除
        if (config.getIsSystem()) {
            throw new RuntimeException("系统内置配置不可删除");
        }

        systemConfigRepository.delete(config);
        log.info("删除配置: {}", configKey);
    }

    /**
     * 批量删除配置
     */
    @Transactional
    public void batchDelete(List<String> configKeys) {
        configKeys.forEach(this::deleteConfig);
    }

    /**
     * 获取所有系统配置
     */
    public List<SystemConfig> listSystemConfigs() {
        return systemConfigRepository.findByIsSystem(true);
    }

    /**
     * 获取所有自定义配置
     */
    public List<SystemConfig> listCustomConfigs() {
        return systemConfigRepository.findByIsSystem(false);
    }
}
