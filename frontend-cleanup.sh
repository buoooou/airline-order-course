#!/bin/bash

# 前端清理脚本
# 用于清理前端构建产物、缓存和临时文件

set -e

echo "🧹 开始清理前端项目..."

# 进入前端目录
cd "$(dirname "$0")/frontend"

echo "📁 当前目录: $(pwd)"

# 1. 清理 node_modules
if [ -d "node_modules" ]; then
    echo "🗑️  删除 node_modules..."
    rm -rf node_modules
    echo "✅ node_modules 已删除"
else
    echo "ℹ️  node_modules 不存在，跳过"
fi

# 2. 清理 package-lock.json
if [ -f "package-lock.json" ]; then
    echo "🗑️  删除 package-lock.json..."
    rm -f package-lock.json
    echo "✅ package-lock.json 已删除"
else
    echo "ℹ️  package-lock.json 不存在，跳过"
fi

# 3. 清理构建输出目录
if [ -d "dist" ]; then
    echo "🗑️  删除 dist 目录..."
    rm -rf dist
    echo "✅ dist 目录已删除"
else
    echo "ℹ️  dist 目录不存在，跳过"
fi

# 4. 清理 Angular 缓存
if [ -d ".angular" ]; then
    echo "🗑️  删除 .angular 缓存..."
    rm -rf .angular
    echo "✅ .angular 缓存已删除"
else
    echo "ℹ️  .angular 缓存不存在，跳过"
fi

# 5. 清理临时文件
echo "🗑️  清理临时文件..."
find . -name "*.tmp" -type f -delete 2>/dev/null || true
find . -name "*.log" -type f -delete 2>/dev/null || true
find . -name ".DS_Store" -type f -delete 2>/dev/null || true

# 6. 清理测试覆盖率报告
if [ -d "coverage" ]; then
    echo "🗑️  删除 coverage 目录..."
    rm -rf coverage
    echo "✅ coverage 目录已删除"
else
    echo "ℹ️  coverage 目录不存在，跳过"
fi

# 7. 清理 npm 缓存 (可选)
echo "🧹 清理 npm 缓存..."
npm cache clean --force 2>/dev/null || echo "⚠️  npm 缓存清理失败，可能 npm 未安装"

# 8. 显示清理后的目录大小
echo ""
echo "📊 清理完成！当前目录大小:"
du -sh . 2>/dev/null || echo "无法计算目录大小"

echo ""
echo "🎉 前端项目清理完成！"
echo ""
echo "💡 下次构建时请运行:"
echo "   npm install --legacy-peer-deps"
echo "   npm run build --configuration=production"
