-- 管理后台初始化脚本

-- 插入默认角色
INSERT INTO admin_roles (role_name, role_code, description, is_system, status, created_at, updated_at)
VALUES
    ('超级管理员', 'SUPER_ADMIN', '拥有所有权限', true, 1, NOW(), NOW()),
    ('运营管理员', 'OPERATOR', '负责日常运营管理', true, 1, NOW(), NOW()),
    ('客服', 'CUSTOMER_SERVICE', '负责用户支持', true, 1, NOW(), NOW());

-- 插入默认权限
INSERT INTO admin_permissions (permission_name, permission_code, module, action, description, parent_id, sort_order, created_at)
VALUES
    -- 仪表盘
    ('仪表盘', 'dashboard:view', 'dashboard', 'view', '查看仪表盘', NULL, 1, NOW()),

    -- 工作流管理
    ('工作流管理', 'workflow:manage', 'workflow', 'manage', '工作流管理', NULL, 2, NOW()),
    ('查看工作流', 'workflow:view', 'workflow', 'view', '查看工作流列表', 2, 1, NOW()),
    ('创建工作流', 'workflow:create', 'workflow', 'create', '创建新工作流', 2, 2, NOW()),
    ('编辑工作流', 'workflow:edit', 'workflow', 'edit', '编辑工作流', 2, 3, NOW()),
    ('删除工作流', 'workflow:delete', 'workflow', 'delete', '删除工作流', 2, 4, NOW()),
    ('发布工作流', 'workflow:publish', 'workflow', 'publish', '发布/下架工作流', 2, 5, NOW()),

    -- 用户管理
    ('用户管理', 'user:manage', 'user', 'manage', '用户管理', NULL, 3, NOW()),
    ('查看用户', 'user:view', 'user', 'view', '查看用户列表', 8, 1, NOW()),
    ('编辑用户', 'user:edit', 'user', 'edit', '编辑用户信息', 8, 2, NOW()),
    ('禁用用户', 'user:disable', 'user', 'disable', '禁用/启用用户', 8, 3, NOW()),
    ('调整资源点', 'user:adjust_points', 'user', 'adjust_points', '调整用户资源点', 8, 4, NOW()),

    -- 任务管理
    ('任务管理', 'task:manage', 'task', 'manage', '任务管理', NULL, 4, NOW()),
    ('查看任务', 'task:view', 'task', 'view', '查看任务列表', 13, 1, NOW()),
    ('取消任务', 'task:cancel', 'task', 'cancel', '取消任务', 13, 2, NOW()),

    -- 系统管理
    ('系统管理', 'system:manage', 'system', 'manage', '系统管理', NULL, 5, NOW()),
    ('管理员管理', 'admin:manage', 'system', 'admin', '管理员管理', 16, 1, NOW()),
    ('角色管理', 'role:manage', 'system', 'role', '角色管理', 16, 2, NOW()),
    ('系统配置', 'config:manage', 'system', 'config', '系统配置管理', 16, 3, NOW()),
    ('操作日志', 'log:view', 'system', 'log', '查看操作日志', 16, 4, NOW());

-- 为超级管理员角色分配所有权限
INSERT INTO admin_role_permissions (role_id, permission_id, created_at)
SELECT 1, id, NOW() FROM admin_permissions;

-- 为运营管理员分配部分权限
INSERT INTO admin_role_permissions (role_id, permission_id, created_at)
SELECT 2, id, NOW() FROM admin_permissions
WHERE permission_code IN (
    'dashboard:view',
    'workflow:view', 'workflow:create', 'workflow:edit', 'workflow:publish',
    'user:view', 'user:edit', 'user:disable',
    'task:view', 'task:cancel'
);

-- 为客服分配基础权限
INSERT INTO admin_role_permissions (role_id, permission_id, created_at)
SELECT 3, id, NOW() FROM admin_permissions
WHERE permission_code IN (
    'dashboard:view',
    'user:view',
    'task:view'
);

-- 创建默认超级管理员账号 (用户名: admin, 密码: admin123)
-- 密码使用BCrypt加密，这里是 "admin123" 的BCrypt哈希值
INSERT INTO admin_users (username, password_hash, real_name, email, role_id, status, login_fail_count, created_at, updated_at)
VALUES ('admin', '$2a$10$Mj4G/gON3Z/6TX1MmUqDS.ZqXOtm0US/p9IybBq4ZPjzg.nkwbaFa', '系统管理员', 'admin@example.com', 1, 1, 0, NOW(), NOW());
