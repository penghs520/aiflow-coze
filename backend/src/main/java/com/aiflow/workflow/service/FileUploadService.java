package com.aiflow.workflow.service;

import com.aiflow.workflow.config.UploadConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final UploadConfig uploadConfig;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        try {
            Path uploadPath = Paths.get(uploadConfig.getBasePath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path imagesPath = uploadPath.resolve("images");
            Path videosPath = uploadPath.resolve("videos");
            if (!Files.exists(imagesPath)) {
                Files.createDirectories(imagesPath);
            }
            if (!Files.exists(videosPath)) {
                Files.createDirectories(videosPath);
            }
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            throw new RuntimeException("创建上传目录失败: " + e.getMessage());
        }
    }

    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持上传图片文件");
        }
        return saveFile(file, "images");
    }

    /**
     * 上传视频
     */
    public String uploadVideo(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("只支持上传视频文件");
        }
        return saveFile(file, "videos");
    }

    /**
     * 保存文件到本地
     */
    private String saveFile(MultipartFile file, String folder) {
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 目标路径
            Path folderPath = Paths.get(uploadConfig.getBasePath(), folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            Path targetPath = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath);

            // 返回访问URL
            return uploadConfig.getBaseUrl() + "/" + folder + "/" + filename;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
