package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.admin.SystemConfig;
import com.aiflow.workflow.service.admin.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.aiflow.workflow.dto.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端系统配置控制器
 */
@RestController
@RequestMapping("/admin/v1/configs")
@RequiredArgsConstructor
public class AdminSystemConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * 分页查询配置
     */
    @GetMapping
    public Result<Page<SystemConfig>> listConfigs(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(systemConfigService.listConfigs(pageable));
    }

    /**
     * 根据类型查询配置
     */
    @GetMapping("/type/{configType}")
    public Result<List<SystemConfig>> listByType(@PathVariable String configType) {
        return Result.success(systemConfigService.listByType(configType));
    }

    /**
     * 获取系统配置列表
     */
    @GetMapping("/system")
    public Result<List<SystemConfig>> listSystemConfigs() {
        return Result.success(systemConfigService.listSystemConfigs());
    }

    /**
     * 获取自定义配置列表
     */
    @GetMapping("/custom")
    public Result<List<SystemConfig>> listCustomConfigs() {
        return Result.success(systemConfigService.listCustomConfigs());
    }

    /**
     * 根据配置键获取配置
     */
    @GetMapping("/{configKey}")
    public Result<SystemConfig> getConfig(@PathVariable String configKey) {
        return Result.success(systemConfigService.getByKey(configKey));
    }

    /**
     * 创建配置
     */
    @PostMapping
    public Result<SystemConfig> createConfig(@RequestBody Map<String, String> request) {
        SystemConfig config = systemConfigService.createConfig(
                request.get("configKey"),
                request.get("configValue"),
                request.get("configType"),
                request.get("description")
        );
        return Result.success(config);
    }

    /**
     * 更新配置
     */
    @PutMapping("/{configKey}")
    public Result<SystemConfig> updateConfig(
            @PathVariable String configKey,
            @RequestBody Map<String, String> request) {
        SystemConfig config = systemConfigService.updateConfig(
                configKey,
                request.get("configValue"),
                request.get("configType"),
                request.get("description")
        );
        return Result.success(config);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{configKey}")
    public Result<Void> deleteConfig(@PathVariable String configKey) {
        systemConfigService.deleteConfig(configKey);
        return Result.success();
    }

    /**
     * 批量删除配置
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<String> configKeys) {
        systemConfigService.batchDelete(configKeys);
        return Result.success();
    }
}
