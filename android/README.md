# AI工作流平台 Android App

基于已完成的产品需求文档、高保真原型和技术方案文档实现的Android原生应用。

## 项目结构

```
app/src/main/java/com/aiflow/workflow/
├── WorkflowApp.kt              # Application类
├── MainActivity.kt             # 主Activity
├── di/                         # 依赖注入模块
├── ui/                         # UI层
│   ├── WorkflowApp.kt          # 主导航组件
│   ├── auth/                   # 认证相关屏幕
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   └── AuthViewModel.kt
│   ├── home/                   # 首页
│   ├── task/                   # 任务中心
│   ├── profile/                # 个人中心
│   ├── workflow/               # 工作流模块（新增）
│   │   ├── WorkflowListScreen.kt
│   │   ├── WorkflowDetailScreen.kt
│   │   ├── ParameterInputScreen.kt
│   │   └── WorkflowViewModel.kt
│   └── theme/                  # 主题定义
├── data/                       # 数据层
│   ├── network/               # 网络相关
│   ├── repository/            # 仓库
│   ├── model/                 # 数据模型
│   └── local/                 # 本地存储
└── utils/                     # 工具类
```

## 技术栈

- **开发语言**: Kotlin + Java
- **UI框架**: Jetpack Compose + Material Design 3
- **架构模式**: MVVM + Repository
- **网络请求**: Retrofit2 + OkHttp3
- **依赖注入**: Hilt
- **本地存储**: DataStore + Room
- **图片加载**: Coil

## 核心功能

### 已实现
1. **项目基础框架**
   - Kotlin + Compose项目配置
   - Hilt依赖注入配置
   - 深色主题定义（背景色 #121212，主色 #3366CC）

2. **用户认证模块**
   - 登录界面：邮箱/用户名 + 密码
   - 注册界面：用户名、邮箱、密码、确认密码
   - 忘记密码界面：邮箱接收重置链接
   - 基本表单验证

3. **导航结构**
   - 底部导航栏：首页、任务中心、我的
   - 页面间导航逻辑
   - 认证状态导航（登录成功跳转首页）

4. **工作流展示模块（事项8）**
   - **工作流列表页面**：
     - 网格布局显示工作流卡片
     - 工作流名称、描述、资源点消耗、分类标签
     - 下拉刷新与分页加载
     - 搜索栏（支持工作流名称、标签搜索）
     - 分类标签过滤（顶部分类栏）
     - 热门排行区域（横向滚动）
   - **工作流详情页面**：
     - 点击列表项跳转至详情页
     - 展示详细说明、使用步骤、功能特点
     - 参数配置说明（类型、必填、约束条件）
     - 使用统计数据（成功率、总任务数、收藏数）
     - 封面图预览
   - **参数输入界面**：
     - 根据工作流参数配置动态生成输入字段
     - 支持文本、数字、选择器、多选、布尔值、滑块、文件上传
     - 表单验证逻辑（必填、范围、格式、文件大小）
     - 实时错误提示
     - 提交任务按钮

### 技术实现细节

#### 数据模型
- `WorkflowModels.kt`: 工作流相关数据结构
  - `Workflow`: 工作流列表项
  - `WorkflowDetail`: 工作流详情
  - `ParameterDefinition`: 参数定义
  - `ParameterType`: 参数类型枚举
  - `PaginationResponse`: 分页响应

- `TaskModels.kt`: 任务相关数据结构
  - `Task`: 任务详情
  - `TaskStatus`: 任务状态枚举
  - `TaskSubmitRequest`: 任务提交请求

#### 网络层扩展
- `ApiService.kt`: 新增工作流和任务相关接口
  - 工作流列表分页查询
  - 工作流详情获取
  - 任务提交与状态查询
  - 分类与排行接口

#### 仓库层
- `WorkflowRepository.kt`: 工作流数据仓库
  - 封装网络请求
  - 统一错误处理
  - 数据转换与缓存

#### ViewModel
- `WorkflowViewModel.kt`: 工作流业务逻辑
  - 状态管理（列表、详情、提交）
  - 数据加载与分页
  - 表单验证逻辑
  - 收藏状态管理

#### UI组件
- `WorkflowListScreen.kt`: 工作流列表（267行）
  - 搜索栏、分类标签、热门排行
  - 网格布局、卡片设计
  - 下拉刷新、加载更多

- `WorkflowDetailScreen.kt`: 工作流详情（228行）
  - 封面图区域
  - 基本信息、参数列表、统计数据
  - 开始使用按钮

- `ParameterInputScreen.kt`: 参数输入（367行）
  - 动态表单生成
  - 多种输入组件
  - 实时验证与错误提示
  - 任务提交

5. **任务管理与充值模块（事项9）**
   - **任务提交扩展**：
     - 在参数输入界面基础上，完善任务提交逻辑
     - 调用后台任务提交API，传递用户输入的所有参数
     - 处理提交成功/失败状态，显示加载动画
     - 成功后自动跳转到任务中心页面
   - **任务中心页面**：
     - 展示用户所有已提交的任务列表
     - 每个任务卡片显示：工作流名称、提交时间、当前状态、进度百分比、资源点消耗
     - 支持下拉刷新、分页加载
     - 支持任务取消（cancelTask）、失败重试（retryTask）操作
     - 点击任务项可跳转到任务详情页（待实现）
   - **资源点充值模块**：
     - 在个人中心页面添加资源点余额展示与充值入口
     - 提供至少3档充值选项（5000点、10000点、20000点）
     - 模拟支付流程，充值成功后更新本地余额并刷新显示
     - 集成用户信息获取与更新机制

### 待实现（后续事项）
5. **高级功能**
   - 用户作品展示
   - 工作流评价系统
   - 智能推荐

## 设计规范

严格遵循即梦App的深色原生风格：

- **背景色**: `#121212`
- **卡片背景**: `#1E1E1E`
- **主色调**: `#3366CC`
- **文字颜色**: 主要 `#FFFFFF`, 次要 `#B0B0B0`
- **字体**: Roboto（Android系统默认）
- **间距**: 8dp倍数系统

## 构建与运行

### 前提条件
- Android Studio Giraffe (2022.3.1) 或更高版本
- Android SDK 34
- Java 17

### 构建步骤
1. 使用Android Studio打开 `src/android/` 目录
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器（Android 8.0+）
4. 点击运行按钮或使用 `Shift+F10`

### 直接构建（命令行）
```bash
cd src/android
./gradlew assembleDebug
# APK位置: app/build/outputs/apk/debug/app-debug.apk
```

## API集成

项目已配置网络层，需要后端API服务支持：

- 认证API: `/api/v1/auth/login`, `/api/v1/auth/register`
- 用户API: `/api/v1/users/profile`
- 工作流API: `/api/v1/workflows`（分页查询）
- 工作流详情: `/api/v1/workflows/{id}`
- 任务提交: `/api/v1/tasks`
- 任务查询: `/api/v1/tasks/{id}`

默认使用 `http://localhost:8080`，可在 `NetworkModule.kt` 中修改。

## 界面交互流程

```
登录 → 首页（工作流列表） → 工作流详情 → 参数输入 → 任务提交 → 任务中心
     ↓                     ↓
  注册 ← 忘记密码      收藏/取消收藏
```

### 关键交互
1. **工作流列表**：
   - 支持下拉刷新与滚动加载
   - 分类标签快速过滤
   - 搜索框实时搜索
   - 热门排行展示

2. **工作流详情**：
   - 参数配置说明（展开/收起）
   - 统计数据可视化
   - 封面图预览

3. **参数输入**：
   - 动态表单生成（基于JSON配置）
   - 实时验证与错误提示
   - 文件上传支持
   - 提交确认

## 下一步开发

基于当前工作流模块，后续开发任务：

1. **事项9**: 实现任务提交后的状态跟踪
   - 任务中心页面（我的任务列表）
   - 任务详情与进度显示
   - 任务结果查看与下载
   - 资源点充值模块

2. **事项10**: 前后端联调与端到端测试
   - API接口对接
   - 数据格式验证
   - 错误处理完善
   - 性能优化

3. **事项11**: 部署上线与运维
   - 生产环境配置
   - 监控与告警
   - 用户反馈收集

## 许可证

本项目为AI工作流平台App的Android客户端实现。