package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.AdminRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRolePermissionRepository extends JpaRepository<AdminRolePermission, Long> {

    List<AdminRolePermission> findByRoleId(Long roleId);

    List<AdminRolePermission> findByPermissionId(Long permissionId);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Modifying
    @Query("DELETE FROM AdminRolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM AdminRolePermission rp WHERE rp.permissionId = :permissionId")
    void deleteByPermissionId(@Param("permissionId") Long permissionId);
}
