package com.aiflow.workflow.service;

import com.aiflow.workflow.config.OssConfig;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OssConfig ossConfig;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        log.info("OSS 配置信息: endpoint={}, bucket={}, simulate={}",
                 ossConfig.getEndpoint(), ossConfig.getBucket(), ossConfig.getSimulate());

        if (!ossConfig.getSimulate()) {
            try {
                ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
                );
                log.info("阿里云 OSS 客户端初始化成功");
            } catch (Exception e) {
                log.error("阿里云 OSS 客户端初始化失败", e);
                throw new RuntimeException("OSS 客户端初始化失败: " + e.getMessage());
            }
        } else {
            log.info("OSS 模拟模式已启用");
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("阿里云 OSS 客户端已关闭");
        }
    }

    /**
     * 上传文件到 OSS
     * @param file 文件
     * @param folder 文件夹 (images/videos)
     * @return 文件访问 URL
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (ossConfig.getSimulate()) {
            log.info("OSS 模拟模式: 文件上传 - {}", file.getOriginalFilename());
            return generateMockUrl(file.getOriginalFilename(), folder);
        }

        try (InputStream inputStream = file.getInputStream()) {
            // 生成文件路径: folder/yyyy-MM-dd/uuid.ext
            String objectKey = generateObjectKey(file.getOriginalFilename(), folder);

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucket(),
                objectKey,
                inputStream
            );

            ossClient.putObject(putObjectRequest);

            // 返回文件访问 URL
            String url = generateFileUrl(objectKey);
            log.info("文件上传成功: {}", url);
            return url;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成对象键 (OSS 中的文件路径)
     */
    private String generateObjectKey(String originalFilename, String folder) {
        // 获取文件扩展名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成日期路径
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 生成唯一文件名
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;

        return folder + "/" + datePath + "/" + filename;
    }

    /**
     * 生成文件访问 URL
     */
    private String generateFileUrl(String objectKey) {
        if (ossConfig.getUrlPrefix() != null && !ossConfig.getUrlPrefix().isEmpty()) {
            // 使用自定义域名
            return ossConfig.getUrlPrefix() + "/" + objectKey;
        } else {
            // 使用默认 OSS 域名
            return "https://" + ossConfig.getBucket() + "." +
                   ossConfig.getEndpoint().replace("https://", "").replace("http://", "") +
                   "/" + objectKey;
        }
    }

    /**
     * 生成模拟 URL
     */
    private String generateMockUrl(String originalFilename, String folder) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        return "https://mock-oss.example.com/" + folder + "/" + filename;
    }
}
