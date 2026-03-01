# AI工作流平台 - 部署指南

## 环境要求

### 后端
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### Android
- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 26+ (Android 8.0)
- Kotlin 1.9.20

## 后端部署

### 1. 安装依赖服务

#### MySQL
```bash
# 启动MySQL
mysql -u root -p

# 执行初始化脚本
source src/backend/src/main/resources/db/init.sql
```

#### Redis
```bash
# 启动Redis
redis-server
```

### 2. 配置应用

编辑 `src/backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_workflow
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379
    password: # 如果有密码

app:
  jwt:
    secret: your-secret-key-at-least-32-characters

  coze:
    api-key: ${COZE_API_KEY}  # 从环境变量读取
    simulate: false  # 生产环境设为false
```

### 3. 编译运行

```bash
cd src/backend

# 编译
mvn clean package

# 运行
java -jar target/backend-1.0.0.jar

# 或使用Maven运行
mvn spring-boot:run
```

### 4. 验证

访问 Swagger UI: http://localhost:8001/api/swagger-ui.html

健康检查: http://localhost:8001/api/actuator/health

## Android部署

### 1. 配置后端地址

编辑 `src/android/app/src/main/java/com/aiflow/workflow/di/NetworkModule.kt`：

```kotlin
private const val BASE_URL = "http://your-server-ip:8001/api/"
```

### 2. 编译安装

```bash
cd src/android

# 编译Debug版本
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 或在Android Studio中直接运行
```

## 测试流程

### 1. 后端API测试

使用Postman或curl测试：

```bash
# 发送验证码
curl -X POST http://localhost:8001/api/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000"}'

# 登录
curl -X POST http://localhost:8001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"123456"}'

# 获取工作流列表
curl -X GET http://localhost:8001/api/workflows \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. 完整业务流程测试

1. 启动后端服务
2. 启动MySQL和Redis
3. 安装Android应用
4. 在应用中注册/登录
5. 浏览工作流列表
6. 提交一个任务
7. 查看任务进度和结果

## 常见问题

### 后端启动失败

1. 检查MySQL是否启动
2. 检查Redis是否启动
3. 检查端口8001是否被占用
4. 查看日志: `logs/application.log`

### Android连接失败

1. 确认后端服务已启动
2. 检查BASE_URL配置是否正确
3. 如果使用模拟器，使用 `10.0.2.2` 代替 `localhost`
4. 检查防火墙设置

### Token认证失败

1. 确认登录成功并获取了token
2. 检查token是否正确保存到DataStore
3. 查看网络请求日志确认Authorization header

## 模拟模式

项目默认启用模拟模式，无需真实的扣子API Key即可测试：

- 短信验证码：任意6位数字均可
- 扣子API：返回模拟的执行结果
- 支付回调：可手动调用接口模拟

生产环境请在 `application.yml` 中关闭模拟模式。

## 下一步

- 集成真实的扣子API
- 集成真实的短信服务
- 集成真实的支付渠道
- 部署到生产环境
- 配置HTTPS
- 配置域名和CDN
