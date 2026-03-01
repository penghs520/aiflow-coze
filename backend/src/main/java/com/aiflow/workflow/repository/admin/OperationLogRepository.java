package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    Page<OperationLog> findByAdminId(Long adminId, Pageable pageable);

    Page<OperationLog> findByModule(String module, Pageable pageable);

    Page<OperationLog> findByCreatedAtBetween(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    @Query("SELECT ol FROM OperationLog ol WHERE " +
           "(:adminId IS NULL OR ol.adminId = :adminId) AND " +
           "(:module IS NULL OR ol.module = :module) AND " +
           "(:startTime IS NULL OR ol.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR ol.createdAt <= :endTime)")
    Page<OperationLog> findByConditions(
            @Param("adminId") Long adminId,
            @Param("module") String module,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);

    long countByAdminId(Long adminId);

    long countByModule(String module);
}
