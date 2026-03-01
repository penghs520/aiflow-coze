# AI工作流平台 - 后台管理系统

## 项目结构

```
├── src/backend/                    # 后端Spring Boot项目
│   ├── src/main/java/
│   │   └── com/aiflow/workflow/
│   │       ├── entity/admin/       # 管理员实体
│   │       ├── repository/admin/   # 管理员Repository
│   │       ├── dto/admin/          # 管理员DTO
│   │       ├── service/admin/      # 管理员Service
│   │       ├── controller/admin/   # 管理员Controller
│   │       ├── security/           # 安全配置
│   │       └── util/               # 工具类
│   └── src/main/resources/
│       ├── application.yml         # 配置文件
│       └── db/admin-init.sql       # 数据库初始化脚本
│
└── src/admin-frontend/             # 前端Vue 3项目
    ├── src/
    │   ├── api/                    # API接口
    │   ├── router/                 # 路由配置
    │   ├── store/                  # 状态管理
    │   ├── utils/                  # 工具函数
    │   ├── components/layout/      # 布局组件
    │   └── views/                  # 页面组件
    ├── package.json
    └── vite.config.js
```

## 快速启动（推荐使用Docker）

### 1. 启动基础设施

使用Docker Compose启动MySQL和Redis：

```bash
./start-infra.sh
```

或手动启动：

```bash
docker-compose up -d
```

**端口说明（避免冲突）：**
- MySQL: `localhost:3307` (而非默认的3306)
- Redis: `localhost:6380` (而非默认的6379)

### 2. 启动后端

```bash
cd src/backend
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

后端将在 http://localhost:8001 启动

### 3. 启动前端

```bash
cd src/admin-frontend
pnpm dev
```

前端将在 http://localhost:8081 启动

### 4. 停止基础设施

```bash
./stop-infra.sh
```

或手动停止：

```bash
docker-compose down
```

## 传统方式启动（使用本地MySQL/Redis）

### 1. 数据库准备

确保MySQL已启动，执行初始化脚本：

```bash
mysql -u root -p ai_workflow < src/backend/src/main/resources/db/admin-init.sql
```

### 2. 配置文件

使用默认配置文件 `application.yml`（连接本地MySQL:3306和Redis:6379）

### 3. 编译运行

```bash
cd src/backend
mvn clean compile
mvn spring-boot:run
```

后端将在 http://localhost:8001 启动

## 前端启动

### 1. 安装依赖

```bash
cd src/admin-frontend
pnpm install
```

### 2. 启动开发服务器

```bash
pnpm dev
```

前端将在 http://localhost:8081 启动

## 默认管理员账号

- 用户名: `admin`
- 密码: `admin123`

## 功能模块

### 已实现

1. **认证模块**
   - 管理员登录
   - JWT Token认证
   - 权限校验

2. **仪表盘**
   - 用户统计
   - 任务统计
   - 工作流统计

3. **基础架构**
   - 实体层（6个实体）
   - Repository层
   - Service层
   - Controller层
   - 前端路由和布局

### 待完善

- 工作流管理（CRUD、上下架）
- 用户管理（查询、禁用、资源点调整）
- 任务监控（查询、取消）
- 管理员管理（CRUD）
- 角色权限管理
- 操作日志查询
- 系统配置管理

## API接口

所有管理端接口前缀：`/api/admin/v1/`

### 认证接口

- `POST /api/admin/v1/auth/login` - 登录
- `GET /api/admin/v1/auth/me` - 获取当前管理员信息
- `PUT /api/admin/v1/auth/password` - 修改密码

### 仪表盘接口

- `GET /api/admin/v1/dashboard` - 获取仪表盘数据

## 技术栈

### 后端

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT
- Lombok

### 前端

- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios

## 开发说明

1. 后端编译通过，核心管理功能已实现
2. 前端基础框架搭建完成，登录和仪表盘可用
3. 其他页面为占位页面，需要继续开发
4. 数据库表会通过JPA自动创建，需手动执行初始化脚本插入初始数据
