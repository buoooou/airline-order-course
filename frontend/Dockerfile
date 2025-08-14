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

# 创建启动脚本，动态设置API地址并禁用代理
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'if [ -n "$API_BASE_URL" ]; then' >> /app/start.sh && \
    echo '  echo "设置API地址: $API_BASE_URL"' >> /app/start.sh && \
    echo '  # 直接修改environment.ts文件' >> /app/start.sh && \
    echo '  sed -i "s|apiUrl: \"\"|apiUrl: \"$API_BASE_URL\"|g" /app/src/environments/environment.ts' >> /app/start.sh && \
    echo '  echo "修改后的environment.ts:"' >> /app/start.sh && \
    echo '  grep "apiUrl:" /app/src/environments/environment.ts' >> /app/start.sh && \
    echo '  # 在Docker环境中禁用代理' >> /app/start.sh && \
    echo '  npm start -- --disable-host-check --host 0.0.0.0 --port 4200' >> /app/start.sh && \
    echo 'else' >> /app/start.sh && \
    echo '  echo "使用默认配置和代理"' >> /app/start.sh && \
    echo '  npm start' >> /app/start.sh && \
    echo 'fi' && \
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
