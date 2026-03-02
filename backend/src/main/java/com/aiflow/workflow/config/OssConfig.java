package com.aiflow.workflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.oss")
public class OssConfig {

    private String endpoint;
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private Boolean simulate = false;
    private String urlPrefix;
}
