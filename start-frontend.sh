#!/bin/bash

echo "启动在线机票系统前端..."

cd frontend

echo "检查Node.js版本..."
node --version
npm --version

echo "安装依赖..."
npm install

echo "启动Angular开发服务器..."
npm start

echo "前端应用已启动，访问地址: http://localhost:4200"