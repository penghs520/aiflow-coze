package com.aiflow.workflow.repository;

import com.aiflow.workflow.entity.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String> {

    /**
     * 根据状态查找工作流
     */
    List<Workflow> findByStatus(Integer status);

    /**
     * 根据分类和状态查找工作流
     */
    List<Workflow> findByCategoryAndStatus(String category, Integer status);

    /**
     * 根据分类和状态分页查找工作流
     */
    Page<Workflow> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    /**
     * 根据分类查找活跃的工作流
     */
    @Query("SELECT w FROM Workflow w WHERE w.category = :category AND w.status = 1 ORDER BY w.sortOrder DESC, w.usageCount DESC")
    List<Workflow> findActiveByCategory(@Param("category") String category);

    /**
     * 根据标签查找工作流
     */
    @Query(value = "SELECT * FROM workflows w WHERE JSON_CONTAINS(w.tags, :tag, '$')", nativeQuery = true)
    List<Workflow> findByTag(@Param("tag") String tag);

    /**
     * 搜索名称或描述中包含关键字的工作流
     */
    @Query("SELECT w FROM Workflow w WHERE w.status = 1 AND (w.name LIKE %:keyword% OR w.description LIKE %:keyword%)")
    Page<Workflow> search(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据使用次数排序查找热门工作流
     */
    Page<Workflow> findByStatusOrderByUsageCountDesc(Integer status, Pageable pageable);

    /**
     * 根据评分排序查找优质工作流
     */
    Page<Workflow> findByStatusOrderByAverageRatingDesc(Integer status, Pageable pageable);

    /**
     * 增加工作流使用次数
     */
    @Modifying
    @Query("UPDATE Workflow w SET w.usageCount = w.usageCount + 1 WHERE w.id = :workflowId")
    void incrementUsageCount(@Param("workflowId") String workflowId);
}