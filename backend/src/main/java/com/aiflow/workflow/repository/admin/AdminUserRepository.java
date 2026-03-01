package com.aiflow.workflow.repository.admin;

import com.aiflow.workflow.entity.admin.AdminUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByUsername(String username);

    Optional<AdminUser> findByUsernameAndStatus(String username, Integer status);

    List<AdminUser> findByRoleId(Long roleId);

    List<AdminUser> findByStatus(Integer status);

    Page<AdminUser> findByRealNameContainingOrUsernameContaining(
            String realName, String username, Pageable pageable);

    long countByStatus(Integer status);

    long countByRoleId(Long roleId);

    @Modifying
    @Query("UPDATE AdminUser a SET a.lastLoginAt = :loginTime, a.lastLoginIp = :loginIp WHERE a.id = :userId")
    void updateLastLogin(@Param("userId") Long userId,
                        @Param("loginTime") LocalDateTime loginTime,
                        @Param("loginIp") String loginIp);

    @Modifying
    @Query("UPDATE AdminUser a SET a.loginFailCount = 0 WHERE a.id = :userId")
    void resetLoginFailCount(@Param("userId") Long userId);
}
