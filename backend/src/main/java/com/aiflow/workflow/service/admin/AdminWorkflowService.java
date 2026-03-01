package com.aiflow.workflow.service.admin;

import com.aiflow.workflow.dto.admin.WorkflowAdminRequest;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.repository.WorkflowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 管理端工作流服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWorkflowService {

    private final WorkflowRepository workflowRepository;
    private final ObjectMapper objectMapper;

    /**
     * 分页查询工作流
     */
    public Page<Workflow> listWorkflows(Pageable pageable) {
        return workflowRepository.findAll(pageable);
    }

    /**
     * 根据分类查询工作流
     */
    public List<Workflow> listByCategory(String category) {
        return workflowRepository.findByCategoryAndStatus(category, 1);
    }

    /**
     * 搜索工作流
     */
    public Page<Workflow> searchWorkflows(String keyword, Pageable pageable) {
        return workflowRepository.search(keyword, pageable);
    }

    /**
     * 获取工作流详情
     */
    public Workflow getWorkflow(String id) {
        return workflowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));
    }

    /**
     * 创建工作流
     */
    @Transactional
    public Workflow createWorkflow(WorkflowAdminRequest request) {
        // 生成工作流ID
        String workflowId = UUID.randomUUID().toString().replace("-", "");

        Workflow workflow = Workflow.builder()
                .id(workflowId)
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .coverUrl(request.getCoverUrl() != null ? request.getCoverUrl() : request.getIconUrl())
                .previewVideoUrl(request.getPreviewVideoUrl())
                .basePoints(request.getPointsCost() != null ? request.getPointsCost().intValue() : 0)
                .cozeWorkflowId(request.getCozeWorkflowId() != null ? request.getCozeWorkflowId() : "")
                .status(request.getStatus() != null ? request.getStatus() : 0)
                .sortOrder(0)
                .build();

        // 设置参数定义
        try {
            if (request.getInputSchema() != null) {
                workflow.setParameterDefinition(objectMapper.writeValueAsString(request.getInputSchema()));
            } else {
                workflow.setParameterDefinition("{}");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("参数定义格式错误", e);
        }

        return workflowRepository.save(workflow);
    }

    /**
     * 更新工作流
     */
    @Transactional
    public Workflow updateWorkflow(String id, WorkflowAdminRequest request) {
        Workflow workflow = getWorkflow(id);

        workflow.setName(request.getName());
        workflow.setDescription(request.getDescription());
        workflow.setCategory(request.getCategory());
        workflow.setCoverUrl(request.getCoverUrl() != null ? request.getCoverUrl() : request.getIconUrl());
        workflow.setPreviewVideoUrl(request.getPreviewVideoUrl());
        workflow.setCozeWorkflowId(request.getCozeWorkflowId());

        if (request.getPointsCost() != null) {
            workflow.setBasePoints(request.getPointsCost().intValue());
        }

        if (request.getStatus() != null) {
            workflow.setStatus(request.getStatus());
        }

        // 更新参数定义
        try {
            if (request.getInputSchema() != null) {
                workflow.setParameterDefinition(objectMapper.writeValueAsString(request.getInputSchema()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("参数定义格式错误", e);
        }

        return workflowRepository.save(workflow);
    }

    /**
     * 删除工作流
     */
    @Transactional
    public void deleteWorkflow(String id) {
        Workflow workflow = getWorkflow(id);
        workflowRepository.delete(workflow);
    }

    /**
     * 上架工作流
     */
    @Transactional
    public void publishWorkflow(String id) {
        Workflow workflow = getWorkflow(id);
        workflow.setStatus(1);
        workflowRepository.save(workflow);
    }

    /**
     * 下架工作流
     */
    @Transactional
    public void unpublishWorkflow(String id) {
        Workflow workflow = getWorkflow(id);
        workflow.setStatus(0);
        workflowRepository.save(workflow);
    }

    /**
     * 更新工作流排序
     */
    @Transactional
    public void updateSortOrder(String id, Integer sortOrder) {
        Workflow workflow = getWorkflow(id);
        workflow.setSortOrder(sortOrder);
        workflowRepository.save(workflow);
    }

    /**
     * 批量上架
     */
    @Transactional
    public void batchPublish(List<String> ids) {
        ids.forEach(this::publishWorkflow);
    }

    /**
     * 批量下架
     */
    @Transactional
    public void batchUnpublish(List<String> ids) {
        ids.forEach(this::unpublishWorkflow);
    }
}
