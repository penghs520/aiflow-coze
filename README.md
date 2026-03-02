# AI工作流平台

基于扣子（Coze）工作流的AI应用平台，提供工作流管理、任务执行、资源点计费等功能。

## 项目结构

```
.
├── src/
│   ├── backend/              # Spring Boot后端
│   ├── admin-frontend/       # Vue 3管理后台
│   └── android/              # Android客户端
├── outputs/                  # 设计文档
├── docker-compose.yml        # Docker基础设施配置
├── start-infra.sh           # 启动脚本
└── stop-infra.sh            # 停止脚本
```

## 快速开始

### 1. 启动基础设施

```bash
./start-infra.sh
```

这将启动：
- MySQL (端口 3307)
- Redis (端口 6380)

### 2. 配置环境变量 (可选)

如需使用阿里云 OSS 或 Coze API,配置环境变量:

```bash
cd backend
cp .env.example .env
# 编辑 .env 文件,填入真实配置
vim .env
```

详见 [环境变量配置指南](docs/ENVIRONMENT_VARIABLES.md)

### 3. 启动后端

```bash
cd backend
# 如果配置了 .env 文件
source .env && mvn spring-boot:run
# 或直接启动 (使用默认配置)
mvn spring-boot:run
```

后端运行在 http://localhost:8001

### 4. 启动管理前端

```bash
cd admin-frontend
pnpm install
pnpm dev
```

前端运行在 http://localhost:8081

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

## 功能特性

- ✅ 用户认证与授权 (JWT)
- ✅ 工作流管理 (集成 Coze API)
- ✅ 任务执行与监控
- ✅ 文件上传 (支持阿里云 OSS)
- ✅ 资源点计费系统
- ✅ 管理后台
- 🚧 Android 客户端 (开发中)

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Redis
- Swagger/OpenAPI

### 前端
- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios

### Android
- Kotlin
- Jetpack Compose
- Retrofit
- Hilt

## 文档

- [管理后台使用说明](ADMIN_README.md)
- [部署文档](DEPLOYMENT.md)
- [环境变量配置指南](docs/ENVIRONMENT_VARIABLES.md)
- [阿里云 OSS 集成](docs/OSS_INTEGRATION.md)
- [OSS 快速开始](docs/OSS_QUICKSTART.md)
- [设计文档](docs/)

## Git提交规范

使用 Conventional Commits 规范：

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具链相关

示例：
```bash
git commit -m "feat: 添加用户管理功能"
git commit -m "fix: 修复登录token过期问题"
```

## License

MIT
