package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.entity.admin.SystemConfig;
import com.aiflow.workflow.service.admin.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Page<SystemConfig>> listConfigs(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(systemConfigService.listConfigs(pageable));
    }

    /**
     * 根据类型查询配置
     */
    @GetMapping("/type/{configType}")
    public ResponseEntity<List<SystemConfig>> listByType(@PathVariable String configType) {
        return ResponseEntity.ok(systemConfigService.listByType(configType));
    }

    /**
     * 获取系统配置列表
     */
    @GetMapping("/system")
    public ResponseEntity<List<SystemConfig>> listSystemConfigs() {
        return ResponseEntity.ok(systemConfigService.listSystemConfigs());
    }

    /**
     * 获取自定义配置列表
     */
    @GetMapping("/custom")
    public ResponseEntity<List<SystemConfig>> listCustomConfigs() {
        return ResponseEntity.ok(systemConfigService.listCustomConfigs());
    }

    /**
     * 根据配置键获取配置
     */
    @GetMapping("/{configKey}")
    public ResponseEntity<SystemConfig> getConfig(@PathVariable String configKey) {
        return ResponseEntity.ok(systemConfigService.getByKey(configKey));
    }

    /**
     * 创建配置
     */
    @PostMapping
    public ResponseEntity<SystemConfig> createConfig(@RequestBody Map<String, String> request) {
        SystemConfig config = systemConfigService.createConfig(
                request.get("configKey"),
                request.get("configValue"),
                request.get("configType"),
                request.get("description")
        );
        return ResponseEntity.ok(config);
    }

    /**
     * 更新配置
     */
    @PutMapping("/{configKey}")
    public ResponseEntity<SystemConfig> updateConfig(
            @PathVariable String configKey,
            @RequestBody Map<String, String> request) {
        SystemConfig config = systemConfigService.updateConfig(
                configKey,
                request.get("configValue"),
                request.get("configType"),
                request.get("description")
        );
        return ResponseEntity.ok(config);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{configKey}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String configKey) {
        systemConfigService.deleteConfig(configKey);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除配置
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<String> configKeys) {
        systemConfigService.batchDelete(configKeys);
        return ResponseEntity.ok().build();
    }
}
