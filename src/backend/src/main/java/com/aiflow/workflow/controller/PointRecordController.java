package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.entity.PointRecord;
import com.aiflow.workflow.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 资源点记录控制器
 */
@Tag(name = "资源点记录接口")
@RestController
@RequestMapping("/point-records")
@RequiredArgsConstructor
public class PointRecordController {

    private final PointService pointService;

    @Operation(summary = "获取资源点记录")
    @GetMapping
    public Result<PageResponse<PointRecord>> getPointRecords(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = (Long) authentication.getPrincipal();
        PageResponse<PointRecord> records = pointService.getPointRecords(userId, page, size);
        return Result.success(records);
    }
}
