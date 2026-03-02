#!/bin/bash

# 启动后端服务 (自动加载环境变量)

cd "$(dirname "$0")"

echo "🚀 启动 AI 工作流平台后端..."
echo ""

# 检查 .env 文件是否存在
if [ -f .env ]; then
    echo "📝 加载环境变量从 .env 文件..."

    # 加载环境变量 (过滤注释和空行)
    export $(cat .env | grep -v '^#' | grep -v '^$' | xargs)

    echo "✅ 环境变量已加载"
    echo ""

    # 显示当前配置
    echo "📋 当前配置:"
    echo "  - OSS Endpoint: ${APP_OSS_ENDPOINT}"
    echo "  - OSS Bucket: ${APP_OSS_BUCKET}"
    echo "  - OSS 模拟模式: ${APP_OSS_SIMULATE}"
    echo "  - Coze 模拟模式: ${COZE_SIMULATE}"
else
    echo "⚠️  未找到 .env 文件,使用默认配置"
    echo ""
    echo "💡 提示:"
    echo "  1. 复制配置模板: cp .env.example .env"
    echo "  2. 编辑配置文件: vim .env"
    echo "  3. 重新运行此脚本: ./start.sh"
    echo ""
    echo "📋 默认配置:"
    echo "  - OSS 模拟模式: true (文件保存到本地)"
    echo "  - Coze 使用默认配置"
fi

echo ""
echo "🔧 启动 Spring Boot 应用..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# 启动应用
mvn spring-boot:run
