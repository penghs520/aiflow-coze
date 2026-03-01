package com.aiflow.workflow.controller.admin;

import com.aiflow.workflow.dto.admin.WorkflowAdminRequest;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.service.admin.AdminWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

    /**
     * 分页查询工作流
     */
    @GetMapping
    public ResponseEntity<Page<Workflow>> listWorkflows(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminWorkflowService.listWorkflows(pageable));
    }

    /**
     * 搜索工作流
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Workflow>> searchWorkflows(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminWorkflowService.searchWorkflows(keyword, pageable));
    }

    /**
     * 根据分类查询工作流
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Workflow>> listByCategory(@PathVariable String category) {
        return ResponseEntity.ok(adminWorkflowService.listByCategory(category));
    }

    /**
     * 获取工作流详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflow(@PathVariable String id) {
        return ResponseEntity.ok(adminWorkflowService.getWorkflow(id));
    }

    /**
     * 创建工作流
     */
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowAdminRequest request) {
        return ResponseEntity.ok(adminWorkflowService.createWorkflow(request));
    }

    /**
     * 更新工作流
     */
    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(
            @PathVariable String id,
            @Valid @RequestBody WorkflowAdminRequest request) {
        return ResponseEntity.ok(adminWorkflowService.updateWorkflow(id, request));
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        adminWorkflowService.deleteWorkflow(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 上架工作流
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> publishWorkflow(@PathVariable String id) {
        adminWorkflowService.publishWorkflow(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 下架工作流
     */
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublishWorkflow(@PathVariable String id) {
        adminWorkflowService.unpublishWorkflow(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新排序
     */
    @PutMapping("/{id}/sort")
    public ResponseEntity<Void> updateSortOrder(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request) {
        adminWorkflowService.updateSortOrder(id, request.get("sortOrder"));
        return ResponseEntity.ok().build();
    }

    /**
     * 批量上架
     */
    @PutMapping("/batch/publish")
    public ResponseEntity<Void> batchPublish(@RequestBody List<String> ids) {
        adminWorkflowService.batchPublish(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量下架
     */
    @PutMapping("/batch/unpublish")
    public ResponseEntity<Void> batchUnpublish(@RequestBody List<String> ids) {
        adminWorkflowService.batchUnpublish(ids);
        return ResponseEntity.ok().build();
    }
}
