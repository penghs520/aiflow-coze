# 后台管理系统 API 接口设计文档

## 1. API 基础规范

### 1.1 通用约定
- **协议**：HTTPS 1.1/2.0
- **数据格式**：JSON（Content-Type: application/json）
- **字符编码**：UTF-8
- **认证方式**：JWT Token（Bearer方案）
- **API前缀**：`/api/admin/v1/`
- **响应格式**：统一包装结构

### 1.2 统一响应结构
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1739750400000
}
```

### 1.3 错误码定义（后台管理专用）
| 错误码 | 含义 | 说明 |
|--------|------|------|
| 200 | 成功 | 请求成功 |
| 400 | 请求参数错误 | 参数格式、类型、范围错误 |
| 401 | 未认证 | Token无效或已过期 |
| 403 | 禁止访问 | 权限不足，无操作权限 |
| 404 | 资源不存在 | 请求的管理员、角色等不存在 |
| 409 | 资源冲突 | 用户名、邮箱等已存在 |
| 422 | 业务校验失败 | 数据状态不允许操作 |
| 429 | 请求过多 | 接口限流触发 |
| 500 | 服务器内部错误 | 系统异常 |

### 1.4 分页参数
所有列表接口支持统一分页参数：
```json
{
  "page": 1,      // 页码，从1开始
  "size": 20,     // 每页数量，默认20，最大100
  "sort": "createdAt,desc"  // 排序字段和方向
}
```

分页响应格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 150,
      "pages": 8
    }
  }
}
```

## 2. 认证授权模块

### 2.1 管理员登录
- **路径**：`/api/admin/v1/auth/login`
- **方法**：POST
- **描述**：管理员账户登录，返回JWT Token
- **认证要求**：否

**请求参数：**
```json
{
  "username": "admin",
  "password": "admin123",
  "captcha": "abcd"  // 图片验证码（可选，失败多次后需要）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "email": "admin@example.com",
      "avatarUrl": "https://oss.example.com/avatars/admin.jpg",
      "roleId": 1,
      "roleName": "超级管理员",
      "permissions": ["*:*"]  // 所有权限
    }
  }
}
```

### 2.2 刷新Token
- **路径**：`/api/admin/v1/auth/refresh`
- **方法**：POST
- **描述**：使用Refresh Token刷新Access Token
- **认证要求**：Refresh Token

**请求参数：**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2.3 修改密码
- **路径**：`/api/admin/v1/auth/password`
- **方法**：PUT
- **描述**：修改当前管理员密码
- **认证要求**：是

**请求参数：**
```json
{
  "oldPassword": "admin123",
  "newPassword": "Admin@2026",
  "confirmPassword": "Admin@2026"
}
```

### 2.4 退出登录
- **路径**：`/api/admin/v1/auth/logout`
- **方法**：POST
- **描述**：使当前Token失效
- **认证要求**：是

## 3. 管理员管理模块

### 3.1 管理员列表
- **路径**：`/api/admin/v1/admins`
- **方法**：GET
- **描述**：分页获取管理员列表
- **认证要求**：是（权限：`admin:list`）

**查询参数：**
| 参数名 | 类型 | 必需 | 说明 |
|--------|------|------|------|
| username | string | 否 | 用户名模糊搜索 |
| realName | string | 否 | 真实姓名模糊搜索 |
| email | string | 否 | 邮箱精确匹配 |
| roleId | integer | 否 | 角色ID筛选 |
| status | integer | 否 | 状态筛选：0-禁用，1-正常，2-锁定 |
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认20 |
| sort | string | 否 | 排序字段，如：createdAt,desc |

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "avatarUrl": "https://oss.example.com/avatars/admin.jpg",
        "roleId": 1,
        "roleName": "超级管理员",
        "status": 1,
        "lastLoginAt": "2026-03-01T10:00:00Z",
        "lastLoginIp": "192.168.1.100",
        "loginFailCount": 0,
        "createdAt": "2026-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 1,
      "pages": 1
    }
  }
}
```

### 3.2 创建管理员
- **路径**：`/api/admin/v1/admins`
- **方法**：POST
- **描述**：创建新的管理员账户
- **认证要求**：是（权限：`admin:create`）

**请求参数：**
```json
{
  "username": "workflow_manager",
  "password": "Manager@2026",
  "realName": "工作流管理员",
  "email": "workflow@example.com",
  "phone": "13800138001",
  "roleId": 2,
  "status": 1
}
```

### 3.3 管理员详情
- **路径**：`/api/admin/v1/admins/{id}`
- **方法**：GET
- **描述**：获取管理员详细信息
- **认证要求**：是（权限：`admin:detail`）

### 3.4 更新管理员
- **路径**：`/api/admin/v1/admins/{id}`
- **方法**：PUT
- **描述**：更新管理员信息
- **认证要求**：是（权限：`admin:update`）

**请求参数：**
```json
{
  "realName": "更新后的姓名",
  "email": "updated@example.com",
  "phone": "13800138002",
  "roleId": 3,
  "status": 1
}
```

### 3.5 删除管理员
- **路径**：`/api/admin/v1/admins/{id}`
- **方法**：DELETE
- **描述**：删除管理员账户（逻辑删除）
- **认证要求**：是（权限：`admin:delete`）

### 3.6 重置管理员密码
- **路径**：`/api/admin/v1/admins/{id}/reset-password`
- **方法**：POST
- **描述**：重置管理员密码为默认密码（需邮件通知）
- **认证要求**：是（权限：`admin:password:reset`）

## 4. 角色管理模块

### 4.1 角色列表
- **路径**：`/api/admin/v1/roles`
- **方法**：GET
- **描述**：获取角色列表（支持分页）
- **认证要求**：是（权限：`role:list`）

### 4.2 创建角色
- **路径**：`/api/admin/v1/roles`
- **方法**：POST
- **描述**：创建新的角色
- **认证要求**：是（权限：`role:create`）

**请求参数：**
```json
{
  "roleName": "数据管理员",
  "roleCode": "data_manager",
  "description": "负责数据统计和报表管理",
  "permissionIds": [1, 3, 5, 7, 9]  // 权限ID数组
}
```

### 4.3 角色详情
- **路径**：`/api/admin/v1/roles/{id}`
- **方法**：GET
- **描述**：获取角色详细信息及关联的权限
- **认证要求**：是（权限：`role:detail`）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "roleName": "工作流管理员",
    "roleCode": "workflow_manager",
    "description": "负责工作流上下架、参数配置、定价策略管理",
    "isSystem": 1,
    "status": 1,
    "createdAt": "2026-01-01T00:00:00Z",
    "permissions": [
      {
        "id": 1,
        "permissionName": "工作流列表查看",
        "permissionCode": "workflow:list",
        "module": "workflow",
        "action": "read"
      }
    ]
  }
}
```

### 4.4 更新角色
- **路径**：`/api/admin/v1/roles/{id}`
- **方法**：PUT
- **描述**：更新角色信息及权限关联
- **认证要求**：是（权限：`role:update`）

### 4.5 删除角色
- **路径**：`/api/admin/v1/roles/{id}`
- **方法**：DELETE
- **描述**：删除角色（系统内置角色不可删除）
- **认证要求**：是（权限：`role:delete`）

## 5. 工作流管理模块

### 5.1 工作流列表（后台）
- **路径**：`/api/admin/v1/workflows`
- **方法**：GET
- **描述**：后台管理工作流列表，支持更多筛选条件
- **认证要求**：是（权限：`workflow:list`）

**查询参数（扩展）：**
| 参数名 | 类型 | 说明 |
|--------|------|------|
| status | integer | 工作流状态：0-下架，1-上架，空-全部 |
| creatorId | integer | 创建者ID（管理员ID） |
| startTime | datetime | 创建时间范围开始 |
| endTime | datetime | 创建时间范围结束 |
| minUsageCount | integer | 最小使用次数 |
| hasError | boolean | 是否有错误任务 |

### 5.2 创建工作流
- **路径**：`/api/admin/v1/workflows`
- **方法**：POST
- **描述**：创建新的工作流配置
- **认证要求**：是（权限：`workflow:create`）

**请求参数：**
```json
{
  "name": "AI视频风格转换",
  "description": "将视频转换为指定艺术风格",
  "category": "video_creation",
  "tags": ["风格转换", "艺术滤镜", "AI视频"],
  "coverUrl": "https://oss.example.com/covers/wf_002.jpg",
  "previewVideoUrl": "https://oss.example.com/previews/wf_002.mp4",
  "basePoints": 800,
  "parameterDefinition": [
    {
      "key": "source_video",
      "name": "源视频",
      "type": "video_file",
      "required": true,
      "constraints": {
        "maxSize": "200MB",
        "formats": ["mp4", "mov"]
      }
    },
    {
      "key": "style_type",
      "name": "艺术风格",
      "type": "select",
      "required": true,
      "default": "oil_painting",
      "options": [
        {"value": "oil_painting", "label": "油画风格"},
        {"value": "watercolor", "label": "水彩风格"},
        {"value": "sketch", "label": "素描风格"}
      ]
    }
  ],
  "cozeWorkflowId": "coze_workflow_002",
  "status": 0,  // 默认下架状态，需要手动上架
  "sortOrder": 10
}
```

### 5.3 工作流详情（后台）
- **路径**：`/api/admin/v1/workflows/{id}`
- **方法**：GET
- **描述**：获取工作流详细信息，包含更多管理字段
- **认证要求**：是（权限：`workflow:detail`）

**响应字段（扩展）：**
```json
{
  "id": "wf_002",
  "name": "AI视频风格转换",
  // ... 其他字段
  "creatorId": 1,
  "creatorName": "系统管理员",
  "lastEditorId": 2,
  "lastEditorName": "工作流管理员",
  "auditStatus": 1,  // 审核状态：0-待审核，1-已审核，2-驳回
  "auditRemark": "审核通过",
  "configVersion": "1.0",
  "isRecommended": false,
  "recommendWeight": 0
}
```

### 5.4 更新工作流
- **路径**：`/api/admin/v1/workflows/{id}`
- **方法**：PUT
- **描述**：更新工作流配置信息
- **认证要求**：是（权限：`workflow:update`）

### 5.5 上架/下架工作流
- **路径**：`/api/admin/v1/workflows/{id}/publish`
- **方法**：POST
- **描述**：切换工作流的上架状态
- **认证要求**：是（权限：`workflow:publish`）

**请求参数：**
```json
{
  "status": 1,  // 1-上架，0-下架
  "remark": "符合上线标准，准予上架"
}
```

### 5.6 批量操作工作流
- **路径**：`/api/admin/v1/workflows/batch`
- **方法**：POST
- **描述**：批量上架、下架、删除工作流
- **认证要求**：是（权限：`workflow:batch`）

**请求参数：**
```json
{
  "ids": ["wf_001", "wf_002", "wf_003"],
  "action": "publish",  // publish-上架，unpublish-下架，delete-删除
  "remark": "批量上架操作"
}
```

### 5.7 工作流数据统计
- **路径**：`/api/admin/v1/workflows/{id}/statistics`
- **方法**：GET
- **描述**：获取工作流的详细统计数据
- **认证要求**：是（权限：`workflow:statistics`）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalTasks": 12543,
    "successTasks": 12021,
    "failedTasks": 522,
    "successRate": 95.8,
    "avgExecutionTime": "00:03:25",
    "totalPointsConsumed": 15432100,
    "dailyUsage": [
      {"date": "2026-02-28", "count": 342},
      {"date": "2026-03-01", "count": 287}
    ],
    "userDistribution": [
      {"region": "华东", "percentage": 35.2},
      {"region": "华南", "percentage": 28.7},
      {"region": "华北", "percentage": 22.1}
    ]
  }
}
```

## 6. 用户管理模块

### 6.1 用户列表（后台）
- **路径**：`/api/admin/v1/users`
- **方法**：GET
- **描述**：后台管理用户列表，支持高级筛选
- **认证要求**：是（权限：`user:list`）

**查询参数：**
| 参数名 | 类型 | 说明 |
|--------|------|------|
| phone | string | 手机号精确匹配 |
| nickname | string | 昵称模糊搜索 |
| status | integer | 状态：0-禁用，1-正常，2-锁定 |
| minPoints | integer | 最小资源点余额 |
| maxPoints | integer | 最大资源点余额 |
| registerStart | datetime | 注册时间范围开始 |
| registerEnd | datetime | 注册时间范围结束 |

### 6.2 用户详情（后台）
- **路径**：`/api/admin/v1/users/{id}`
- **方法**：GET
- **描述**：获取用户详细信息，包含敏感数据
- **认证要求**：是（权限：`user:detail`）

**响应字段（扩展）：**
```json
{
  "id": 1001,
  "phone": "13800138000",
  "nickname": "AI创作者",
  "avatarUrl": "https://oss.example.com/avatars/user_1001.jpg",
  "pointsBalance": 28500,
  "status": 1,
  "lastLoginAt": "2026-03-01T09:30:00Z",
  "lastLoginIp": "112.64.125.78",
  "registerIp": "112.64.125.76",
  "totalTasks": 42,
  "totalConsumption": 12600,
  "totalRecharge": 50000,
  "createdAt": "2026-01-15T14:20:00Z"
}
```

### 6.3 更新用户状态
- **路径**：`/api/admin/v1/users/{id}/status`
- **方法**：PUT
- **描述**：启用/禁用用户账户
- **认证要求**：是（权限：`user:status`）

**请求参数：**
```json
{
  "status": 0,  // 0-禁用，1-正常
  "reason": "发布违规内容，禁言7天"
}
```

### 6.4 调整用户资源点
- **路径**：`/api/admin/v1/users/{id}/points`
- **方法**：POST
- **描述**：手动调整用户资源点余额（需记录操作日志）
- **认证要求**：是（权限：`user:points`）

**请求参数：**
```json
{
  "changeType": "manual_adjustment",  // manual_adjustment-手动调整
  "changeAmount": 10000,              // 正数为增加，负数为减少
  "remark": "补偿系统故障导致的资源点损失"
}
```

### 6.5 用户行为分析
- **路径**：`/api/admin/v1/users/{id}/behavior`
- **方法**：GET
- **描述**：分析用户的使用行为模式
- **认证要求**：是（权限：`user:behavior`）

## 7. 任务管理模块

### 7.1 任务列表（后台）
- **路径**：`/api/admin/v1/tasks`
- **方法**：GET
- **描述**：查看所有用户的任务列表
- **认证要求**：是（权限：`task:list`）

**查询参数：**
| 参数名 | 类型 | 说明 |
|--------|------|------|
| userId | integer | 用户ID筛选 |
| workflowId | string | 工作流ID筛选 |
| status | integer | 状态筛选：0-待提交，1-排队中，2-处理中，3-已完成，4-失败，5-已取消 |
| startTime | datetime | 创建时间范围开始 |
| endTime | datetime | 创建时间范围结束 |
| hasError | boolean | 是否有错误信息 |
| priority | string | 优先级：low, normal, high, urgent |

### 7.2 任务详情（后台）
- **路径**：`/api/admin/v1/tasks/{id}`
- **方法**：GET
- **描述**：获取任务完整信息，包含执行日志
- **认证要求**：是（权限：`task:detail`）

### 7.3 重新执行任务
- **路径**：`/api/admin/v1/tasks/{id}/retry`
- **方法**：POST
- **描述**：重新执行失败的任务
- **认证要求**：是（权限：`task:retry`）

### 7.4 取消任务
- **路径**：`/api/admin/v1/tasks/{id}/cancel`
- **方法**：POST
- **描述**：取消进行中的任务
- **认证要求**：是（权限：`task:cancel`）

### 7.5 任务统计报表
- **路径**：`/api/admin/v1/tasks/statistics`
- **方法**：GET
- **描述**：生成任务相关统计报表
- **认证要求**：是（权限：`task:statistics`）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "timeRange": "2026-02-01 至 2026-02-28",
    "totalTasks": 125432,
    "successTasks": 120125,
    "failedTasks": 5307,
    "overallSuccessRate": 95.8,
    "avgExecutionTime": "00:03:42",
    "peakHours": [
      {"hour": 20, "taskCount": 15642},
      {"hour": 21, "taskCount": 14238},
      {"hour": 14, "taskCount": 12876}
    ],
    "workflowRanking": [
      {"workflowId": "wf_001", "name": "AI视频人物换脸", "taskCount": 45210},
      {"workflowId": "wf_003", "name": "图文生成", "taskCount": 38765},
      {"workflowId": "wf_002", "name": "AI视频风格转换", "taskCount": 21457}
    ]
  }
}
```

## 8. 系统管理模块

### 8.1 操作日志列表
- **路径**：`/api/admin/v1/operation-logs`
- **方法**：GET
- **描述**：查询管理员操作日志
- **认证要求**：是（权限：`system:log:list`）

**查询参数：**
| 参数名 | 类型 | 说明 |
|--------|------|------|
| adminId | integer | 管理员ID筛选 |
| module | string | 操作模块筛选 |
| action | string | 操作类型筛选 |
| status | integer | 操作状态：0-失败，1-成功 |
| startTime | datetime | 操作时间范围开始 |
| endTime | datetime | 操作时间范围结束 |
| keyword | string | 操作名称或目标ID模糊搜索 |

### 8.2 系统配置列表
- **路径**：`/api/admin/v1/configs`
- **方法**：GET
- **描述**：获取系统配置项列表
- **认证要求**：是（权限：`system:config:list`）

### 8.3 更新系统配置
- **路径**：`/api/admin/v1/configs/{key}`
- **方法**：PUT
- **描述**：更新系统配置值
- **认证要求**：是（权限：`system:config:update`）

**请求参数：**
```json
{
  "configValue": "new_value",
  "remark": "调整资源点定价策略"
}
```

### 8.4 通知公告管理
- **路径**：`/api/admin/v1/notices`
- **方法**：CRUD
- **描述**：通知公告的增删改查
- **认证要求**：是（权限：`system:notice:crud`）

## 9. 数据导出模块

### 9.1 导出工作流数据
- **路径**：`/api/admin/v1/export/workflows`
- **方法**：POST
- **描述**：导出工作流数据为Excel文件
- **认证要求**：是（权限：`workflow:export`）

**请求参数：**
```json
{
  "format": "excel",  // excel, csv
  "columns": ["id", "name", "category", "status", "usageCount", "createdAt"],
  "filters": {
    "status": 1,
    "category": "video_creation"
  }
}
```

### 9.2 导出用户数据
- **路径**：`/api/admin/v1/export/users`
- **方法**：POST
- **描述**：导出用户数据为Excel文件
- **认证要求**：是（权限：`user:export`）

### 9.3 导出任务数据
- **路径**：`/api/admin/v1/export/tasks`
- **方法**：POST
- **描述**：导出任务数据为Excel文件
- **认证要求**：是（权限：`task:export`）

### 9.4 导出订单数据
- **路径**：`/api/admin/v1/export/orders`
- **方法**：POST
- **描述**：导出订单数据为Excel文件
- **认证要求**：是（权限：`order:export`）

## 10. 接口安全设计

### 10.1 权限验证流程
1. **Token验证**：拦截器校验JWT Token有效性
2. **权限校验**：根据用户角色查询权限列表，验证当前接口需要的权限码
3. **数据权限**：对于用户数据，验证操作权限（如只能管理自己创建的工作流）
4. **操作审计**：记录完整操作日志，包括请求参数、响应结果、执行时间

### 10.2 防刷机制
1. **接口限流**：基于IP和用户ID的令牌桶限流
2. **验证码**：登录失败多次后需要图片验证码
3. **敏感操作二次确认**：删除、状态变更等操作需要二次确认
4. **操作频率限制**：同一用户短时间内不能重复执行相同操作

### 10.3 数据脱敏
1. **接口返回**：根据权限返回不同数据字段（如普通管理员看不到用户手机号）
2. **日志记录**：敏感数据（密码、支付信息）在日志中脱敏
3. **导出数据**：导出时对敏感字段进行脱敏处理

---

**文档版本记录**
| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|---------|-------|
| v1.0 | 2026-03-01 | 初始版本创建 | 扣子 |