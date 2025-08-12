# 多阶段构建 - 构建阶段
FROM node:20-alpine AS build

WORKDIR /app

# 复制源代码
COPY frontend/ .

# 清理并重新安装依赖
RUN rm -f package-lock.json && \
    npm install --legacy-peer-deps && \
    npm run build --configuration=production

# 生产阶段 - Nginx
FROM nginx:alpine

# 删除nginx默认文件
RUN rm -rf /usr/share/nginx/html/*

# 复制构建产物到nginx（从browser子目录）
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# 复制nginx配置
COPY nginx/nginx.docker.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
