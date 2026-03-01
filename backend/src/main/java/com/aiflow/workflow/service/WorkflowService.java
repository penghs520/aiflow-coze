package com.aiflow.workflow.service;

import com.aiflow.workflow.dto.PageResponse;
import com.aiflow.workflow.entity.Workflow;
import com.aiflow.workflow.exception.WorkflowNotFoundException;
import com.aiflow.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 工作流服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    /**
     * 获取工作流列表
     */
    public PageResponse<Workflow> getWorkflowList(String category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sortOrder", "usageCount"));

        Page<Workflow> workflowPage;

        if (StringUtils.hasText(keyword)) {
            workflowPage = workflowRepository.search(keyword, pageable);
        } else if (StringUtils.hasText(category)) {
            workflowPage = workflowRepository.findByCategoryAndStatus(category, 1, pageable);
        } else {
            workflowPage = workflowRepository.findByStatus(1).stream()
                    .collect(java.util.stream.Collectors.collectingAndThen(
                            java.util.stream.Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        }

        return buildPageResponse(workflowPage);
    }

    /**
     * 获取工作流详情
     */
    public Workflow getWorkflowDetail(String workflowId) {
        return workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));
    }

    /**
     * 获取分类列表
     */
    public List<String> getCategories() {
        return Arrays.asList("video", "image", "text", "audio", "other");
    }

    /**
     * 获取热门排行
     */
    public List<Workflow> getRanking(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Workflow> page = workflowRepository.findByStatusOrderByUsageCountDesc(1, pageable);
        return page.getContent();
    }

    /**
     * 增加使用次数
     */
    @Transactional
    public void incrementUsageCount(String workflowId) {
        workflowRepository.incrementUsageCount(workflowId);
    }

    /**
     * 构建分页响应
     */
    private PageResponse<Workflow> buildPageResponse(Page<Workflow> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}

