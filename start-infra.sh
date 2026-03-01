#!/bin/bash

echo "🚀 启动AI工作流平台基础设施..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker"
    exit 1
fi

# 启动基础设施
echo "📦 启动MySQL和Redis..."
docker-compose up -d

# 等待服务就绪
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "✅ 检查服务状态..."
docker-compose ps

echo ""
echo "🎉 基础设施启动完成！"
echo ""
echo "📊 服务信息："
echo "  MySQL: localhost:3307"
echo "    - 数据库: ai_workflow"
echo "    - 用户名: root"
echo "    - 密码: root"
echo ""
echo "  Redis: localhost:6380"
echo ""
echo "💡 使用以下命令："
echo "  查看日志: docker-compose logs -f"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart"
echo ""
echo "🔧 启动后端服务："
echo "  cd src/backend"
echo "  mvn spring-boot:run -Dspring-boot.run.profiles=docker"
echo ""
