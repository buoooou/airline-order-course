FROM node:20-alpine AS build

WORKDIR /frontend-app

# 复制依赖文件并安装
COPY package*.json ./
RUN npm install

# 复制源代码并构建
COPY . .
RUN npm run build

# 构建Nginx运行环境
FROM nginx:alpine

# 删除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 复制自定义配置
COPY nginx.conf /etc/nginx/conf.d

# 复制构建产物到Nginx
COPY --from=build /frontend-app/dist/airline-order-frontend/browser/ /usr/share/nginx/html/

# 暴露80端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
