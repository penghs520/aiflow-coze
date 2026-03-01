package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {

    Optional<SystemConfig> findByConfigKey(String configKey);

    List<SystemConfig> findByConfigType(String configType);

    List<SystemConfig> findByIsSystem(Boolean isSystem);

    boolean existsByConfigKey(String configKey);
}
