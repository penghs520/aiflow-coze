package com.aiflow.workflow.repository;

import com.aiflow.workflow.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    /**
     * 根据用户ID查找记录
     */
    List<PointRecord> findByUser_Id(Long userId);

    /**
     * 根据用户ID分页查找记录
     */
    Page<PointRecord> findByUser_Id(Long userId, Pageable pageable);

    /**
     * 根据用户ID和变动类型查找记录
     */
    List<PointRecord> findByUser_IdAndChangeType(Long userId, Integer changeType);

    /**
     * 根据用户ID和变动类型分页查找记录
     */
    Page<PointRecord> findByUser_IdAndChangeType(Long userId, Integer changeType, Pageable pageable);

    /**
     * 根据关联ID查找记录
     */
    List<PointRecord> findByRelatedId(String relatedId);

    /**
     * 根据用户ID和创建时间范围查找记录
     */
    List<PointRecord> findByUser_IdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计用户资源点变动总量
     */
    @Query("SELECT SUM(pr.changeAmount) FROM PointRecord pr WHERE pr.user.id = :userId")
    Optional<Integer> sumChangeAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计用户充值获得的总点数
     */
    @Query("SELECT SUM(pr.changeAmount) FROM PointRecord pr WHERE pr.user.id = :userId AND pr.changeType = 2")
    Optional<Integer> sumRechargePointsByUserId(@Param("userId") Long userId);

    /**
     * 统计用户任务消耗的总点数
     */
    @Query("SELECT SUM(pr.changeAmount) FROM PointRecord pr WHERE pr.user.id = :userId AND pr.changeType = 3")
    Optional<Integer> sumConsumptionPointsByUserId(@Param("userId") Long userId);
}