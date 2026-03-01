package com.aiflow.workflow.controller;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流控制器
 */
@Tag(name = "工作流接口")
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @Operation(summary = "获取工作流列表")
    @GetMapping
    public Result<PageResponse<Workflow>> getWorkflowList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<Workflow> workflows = workflowService.getWorkflowList(category, keyword, page, size);
        return Result.success(workflows);
    }

    @Operation(summary = "获取工作流详情")
    @GetMapping("/{id}")
    public Result<Workflow> getWorkflowDetail(@PathVariable String id) {
        Workflow workflow = workflowService.getWorkflowDetail(id);
        return Result.success(workflow);
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = workflowService.getCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取热门排行")
    @GetMapping("/ranking")
    public Result<List<Workflow>> getRanking(@RequestParam(defaultValue = "10") int limit) {
        List<Workflow> ranking = workflowService.getRanking(limit);
        return Result.success(ranking);
    }
}
