#!/bin/sh

echo "启动脚本开始执行..."
echo "当前工作目录: $(pwd)"
echo "环境变量API_BASE_URL: $API_BASE_URL"

# 检查文件是否存在
if [ ! -f "/app/src/environments/environment.ts" ]; then
  echo "错误: environment.ts文件不存在!"
  ls -la /app/src/environments/
  exit 1
fi

# 显示修改前的文件内容
echo "修改前的environment.ts内容:"
cat /app/src/environments/environment.ts

if [ -n "$API_BASE_URL" ]; then
  echo "设置API地址: $API_BASE_URL"
  # 使用更安全的sed命令
  sed -i "s|apiUrl: \"\"|apiUrl: \"$API_BASE_URL\"|g" /app/src/environments/environment.ts
  
  # 验证修改是否成功
  echo "修改后的environment.ts内容:"
  cat /app/src/environments/environment.ts
else
  echo "警告: API_BASE_URL环境变量未设置，使用默认配置"
fi

echo "启动Angular开发服务器..."
npm start -- --host 0.0.0.0 --port 4200
