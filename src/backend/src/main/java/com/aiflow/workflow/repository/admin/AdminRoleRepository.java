package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, Long> {

    Optional<AdminRole> findByRoleCode(String roleCode);

    List<AdminRole> findByStatus(Integer status);

    List<AdminRole> findByIsSystem(Boolean isSystem);

    long countByStatus(Integer status);

    boolean existsByRoleCode(String roleCode);
}
