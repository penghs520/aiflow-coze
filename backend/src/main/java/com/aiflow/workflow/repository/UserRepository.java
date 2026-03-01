package com.aiflow.workflow.repository;

import com.aiflow.workflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据手机号和状态查找用户
     */
    Optional<User> findByPhoneAndStatus(String phone, Integer status);

    /**
     * 根据状态查找用户列表
     */
    List<User> findByStatus(Integer status);

    /**
     * 查找资源点余额大于指定值的用户
     */
    List<User> findByPointsBalanceGreaterThan(Long points);

    /**
     * 查找昵称包含关键字的用户
     */
    List<User> findByNicknameContaining(String keyword);

    /**
     * 统计指定状态下的用户数量
     */
    long countByStatus(Integer status);

    /**
     * 更新用户最后登录时间
     */
    @Query("UPDATE User u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId);
}