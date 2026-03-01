package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.AdminPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminPermissionRepository extends JpaRepository<AdminPermission, Long> {

    Optional<AdminPermission> findByPermissionCode(String permissionCode);

    List<AdminPermission> findByModule(String module);

    List<AdminPermission> findByParentId(Long parentId);

    List<AdminPermission> findByParentIdOrderBySortOrder(Long parentId);

    @Query("SELECT p FROM AdminPermission p WHERE p.parentId IS NULL ORDER BY p.sortOrder")
    List<AdminPermission> findRootPermissions();

    @Query("SELECT p FROM AdminPermission p JOIN AdminRolePermission rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId")
    List<AdminPermission> findByRoleId(@Param("roleId") Long roleId);

    boolean existsByPermissionCode(String permissionCode);
}
