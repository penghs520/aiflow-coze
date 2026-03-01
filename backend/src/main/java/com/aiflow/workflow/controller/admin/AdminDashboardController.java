package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.dto.admin.DashboardResponse;
import com.aiflow.workflow.service.admin.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理端仪表盘接口")
@RestController
@RequestMapping("/admin/v1/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(summary = "获取仪表盘数据")
    @GetMapping
    public Result<DashboardResponse> getDashboardData() {
        DashboardResponse response = adminDashboardService.getDashboardData();
        return Result.success(response);
    }
}
