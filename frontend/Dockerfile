# 开发环境Dockerfile
FROM node:20-alpine

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci

# 复制源代码
COPY . .

# 确保Angular CLI有执行权限
RUN chmod +x node_modules/.bin/ng

# 创建启动脚本，动态设置API地址
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "=== 前端服务启动脚本 ==="' >> /app/start.sh && \
    echo 'echo "当前工作目录: $(pwd)"' >> /app/start.sh && \
    echo 'echo "环境变量 API_BASE_URL: $API_BASE_URL"' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo 'if [ -n "$API_BASE_URL" ]; then' >> /app/start.sh && \
    echo '  echo "正在设置API地址..."' >> /app/start.sh && \
    echo '  sed -i "s|apiUrl: \"\"|apiUrl: \"$API_BASE_URL\"|g" /app/src/environments/environment.ts' >> /app/start.sh && \
    echo '  echo "配置文件已更新:"' >> /app/start.sh && \
    echo '  cat /app/src/environments/environment.ts | grep apiUrl' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '  echo "使用默认配置（相对路径）"' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo 'echo "启动Angular开发服务器..."' >> /app/start.sh && \
    echo 'npm start -- --host 0.0.0.0 --port 4200' >> /app/start.sh && \
    chmod +x /app/start.sh

# 安装curl用于健康检查
RUN apk add --no-cache curl

# 暴露开发端口
EXPOSE 4200

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:4200 || exit 1

# 启动开发服务器
CMD ["/app/start.sh"]
