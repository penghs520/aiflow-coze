package com.aiflow.workflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.upload")
public class UploadConfig {

    private String basePath = "./uploads";
    private String baseUrl = "/uploads";
}
