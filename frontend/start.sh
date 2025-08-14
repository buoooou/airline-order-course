#!/bin/sh

echo "启动脚本开始执行..."

if [ -n "$API_BASE_URL" ]; then
  echo "设置API地址: $API_BASE_URL"
  sed -i "s|apiUrl: \"\"|apiUrl: \"$API_BASE_URL\"|g" /app/src/environments/environment.ts
fi

echo "启动Angular开发服务器..."
npm start -- --host 0.0.0.0 --port 4200
