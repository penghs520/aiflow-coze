package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.Result;
import com.aiflow.workflow.dto.admin.WorkflowAdminRequest;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.service.CozeService;
import com.aiflow.workflow.service.admin.AdminWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端工作流控制器
 */
@RestController
@RequestMapping("/admin/v1/workflows")
@RequiredArgsConstructor
public class AdminWorkflowController {

    private final AdminWorkflowService adminWorkflowService;
    private final CozeService cozeService;

    /**
     * 分页查询工作流
     */
    @GetMapping
    public Result<Page<Workflow>> listWorkflows(@PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminWorkflowService.listWorkflows(pageable));
    }

    /**
     * 获取工作流参数定义（从Coze平台查询）
     */
    @GetMapping("/{id}/parameters")
    public Result<List<Map<String, Object>>> getWorkflowParameters(@PathVariable String id) {
        Workflow workflow = adminWorkflowService.getWorkflow(id);
        List<Map<String, Object>> parameters = cozeService.getWorkflowParameters(workflow.getCozeWorkflowId());
        return Result.success(parameters);
    }

    /**
     * 搜索工作流
     */
    @GetMapping("/search")
    public Result<Page<Workflow>> searchWorkflows(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return Result.success(adminWorkflowService.searchWorkflows(keyword, pageable));
    }

    /**
     * 根据分类查询工作流
     */
    @GetMapping("/category/{category}")
    public Result<List<Workflow>> listByCategory(@PathVariable String category) {
        return Result.success(adminWorkflowService.listByCategory(category));
    }

    /**
     * 获取工作流详情
     */
    @GetMapping("/{id}")
    public Result<Workflow> getWorkflow(@PathVariable String id) {
        return Result.success(adminWorkflowService.getWorkflow(id));
    }

    /**
     * 创建工作流
     */
    @PostMapping
    public Result<Workflow> createWorkflow(@Valid @RequestBody WorkflowAdminRequest request) {
        return Result.success(adminWorkflowService.createWorkflow(request));
    }

    /**
     * 更新工作流
     */
    @PutMapping("/{id}")
    public Result<Workflow> updateWorkflow(
            @PathVariable String id,
            @Valid @RequestBody WorkflowAdminRequest request) {
        return Result.success(adminWorkflowService.updateWorkflow(id, request));
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorkflow(@PathVariable String id) {
        adminWorkflowService.deleteWorkflow(id);
        return Result.success();
    }

    /**
     * 上架工作流
     */
    @PutMapping("/{id}/publish")
    public Result<Void> publishWorkflow(@PathVariable String id) {
        adminWorkflowService.publishWorkflow(id);
        return Result.success();
    }

    /**
     * 下架工作流
     */
    @PutMapping("/{id}/unpublish")
    public Result<Void> unpublishWorkflow(@PathVariable String id) {
        adminWorkflowService.unpublishWorkflow(id);
        return Result.success();
    }

    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public Result<Void> updateSortOrder(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request) {
        adminWorkflowService.updateSortOrder(id, request.get("sortOrder"));
        return Result.success();
    }

    /**
     * 批量上架
     */
    @PutMapping("/batch/publish")
    public Result<Void> batchPublish(@RequestBody List<String> ids) {
        adminWorkflowService.batchPublish(ids);
        return Result.success();
    }

    /**
     * 批量下架
     */
    @PutMapping("/batch/unpublish")
    public Result<Void> batchUnpublish(@RequestBody List<String> ids) {
        adminWorkflowService.batchUnpublish(ids);
        return Result.success();
    }
}
