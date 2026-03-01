package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/v1/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileUploadService fileUploadService;

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadImage(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadVideo(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }
}
