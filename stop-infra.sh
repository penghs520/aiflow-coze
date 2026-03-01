#!/bin/bash

echo "🛑 停止AI工作流平台基础设施..."

docker-compose down

echo ""
echo "✅ 基础设施已停止"
echo ""
echo "💡 如需完全清理（包括数据卷）："
echo "  docker-compose down -v"
echo ""
