#!/bin/bash

# 后端清理脚本
# 用于清理后端构建产物、缓存和临时文件

set -e

echo "🧹 开始清理后端项目..."

# 进入后端目录
cd "$(dirname "$0")/backend"

echo "📁 当前目录: $(pwd)"

# 1. 清理 Maven target 目录
if [ -d "target" ]; then
    echo "🗑️  删除 target 目录..."
    rm -rf target
    echo "✅ target 目录已删除"
else
    echo "ℹ️  target 目录不存在，跳过"
fi

# 2. 清理 Maven 本地仓库缓存 (可选，谨慎使用)
# if [ -d "$HOME/.m2/repository" ]; then
#     echo "🗑️  清理 Maven 本地仓库缓存..."
#     rm -rf "$HOME/.m2/repository"
#     echo "✅ Maven 本地仓库缓存已清理"
# fi

# 3. 清理 IDE 相关文件
echo "🗑️  清理 IDE 相关文件..."

# IntelliJ IDEA
if [ -d ".idea" ]; then
    rm -rf .idea
    echo "✅ .idea 目录已删除"
fi

if [ -f "*.iml" ]; then
    rm -f *.iml
    echo "✅ .iml 文件已删除"
fi

# Eclipse
if [ -d ".metadata" ]; then
    rm -rf .metadata
    echo "✅ .metadata 目录已删除"
fi

if [ -f ".project" ]; then
    rm -f .project
    echo "✅ .project 文件已删除"
fi

if [ -f ".classpath" ]; then
    rm -f .classpath
    echo "✅ .classpath 文件已删除"
fi

# VS Code
if [ -d ".vscode" ]; then
    rm -rf .vscode
    echo "✅ .vscode 目录已删除"
fi

# 4. 清理日志文件
echo "🗑️  清理日志文件..."
find . -name "*.log" -type f -delete 2>/dev/null || true
find . -name "*.log.*" -type f -delete 2>/dev/null || true

# 5. 清理临时文件
echo "🗑️  清理临时文件..."
find . -name "*.tmp" -type f -delete 2>/dev/null || true
find . -name "*.temp" -type f -delete 2>/dev/null || true
find . -name ".DS_Store" -type f -delete 2>/dev/null || true
find . -name "Thumbs.db" -type f -delete 2>/dev/null || true

# 6. 清理测试报告
if [ -d "test-results" ]; then
    echo "🗑️  删除 test-results 目录..."
    rm -rf test-results
    echo "✅ test-results 目录已删除"
fi

if [ -d "surefire-reports" ]; then
    echo "🗑️  删除 surefire-reports 目录..."
    rm -rf surefire-reports
    echo "✅ surefire-reports 目录已删除"
fi

# 7. 清理 Spring Boot 相关临时文件
find . -name "spring-*.log" -type f -delete 2>/dev/null || true

# 8. 清理编译产物
find . -name "*.class" -type f -delete 2>/dev/null || true

# 9. 显示清理后的目录大小
echo ""
echo "📊 清理完成！当前目录大小:"
du -sh . 2>/dev/null || echo "无法计算目录大小"

echo ""
echo "🎉 后端项目清理完成！"
echo ""
echo "💡 下次构建时请运行:"
echo "   mvn clean compile"
echo "   mvn clean package -DskipTests"
echo "   或者"
echo "   ./mvnw clean package -DskipTests"
