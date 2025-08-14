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

# 确保启动脚本有执行权限
RUN chmod +x start.sh

# 安装curl用于健康检查
RUN apk add --no-cache curl

# 暴露开发端口
EXPOSE 4200

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:4200 || exit 1

# 启动开发服务器
CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]
