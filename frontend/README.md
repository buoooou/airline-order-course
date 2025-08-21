# AirlineOrderFrontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.1.2.

## Development server

# install dependencies
npm install

# generate a new component （pages/login）
npx ng generate component pages/login

# Method 1: Using npm start
npm start

# Method 2: Using npx
npx ng serve

http://localhost:4200/

# help
npx ng generate --help

## Building
npx ng build

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests
npx ng test

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

## Running end-to-end tests
npx ng e2e

## Additional Resources
https://angular.dev/tools/cli

# 网络设置
1. 打开公司网络，Win11的公司网络代理：http://proxy.emea.ibm.com:8080

2. 设置 ubuntu 的代理
sudo nano /etc/systemd/system/docker.service.d/proxy.conf
[Service]
Environment="HTTP_PROXY=http://proxy.emea.ibm.com:8080"
Environment="HTTPS_PROXY=http://proxy.emea.ibm.com:8080"
Environment="NO_PROXY=localhost,127.0.0.1,.example.com"

# 运行 Dockerfile
cd frontend
docker build -t aws-airline-frontend .

# 确认镜像
docker image ls
# 运行容器 （避免和后端端口冲突，这里使用8081端口）
docker run -d -p 8081:80 --name aws-airline-frontend aws-airline-frontend

# 进入容器 （Alpine 系统默认没有 bash，用 sh 即可）
# docker exec -it aws-airline-frontend /bin/bash
docker exec -it aws-airline-frontend /bin/sh
docker exec -it aws-airline-frontend ls /usr/share/nginx/html

# 查看日志
docker logs -f aws-airline-frontend

# 停止运行的容器
docker stop aws-airline-frontend

# 删除容器
docker rm aws-airline-frontend

# 生成aws镜像
docker tag aws-airline-frontend:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/airline-order-frontend-sfm:V1

# 推送镜像到aws
docker push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/airline-order-frontend-sfm:V1 

# 前端页面
http://localhost:8081/


# 常见问题
1. 前端页面显示空白
注意前端打包后的结构dist，注意Dockerfile中COPY的路径
确认Image内容：
docker exec -it aws-airline-frontend ls /usr/share/nginx/html
修改Dockerfile文件：
COPY --from=build /frontend-app/dist/airline-order-frontend/browser/ /usr/share/nginx/html/

2. 前端页面显示 nginx welcome page
在前端追加nginx的配置文件，并修改Dockerfile文件（追加 COPY nginx.conf /etc/nginx/conf.d）。

3. 前端页面显示 502 Bad Gateway
修改nginx的配置文件的proxy_pass：
旧：
proxy_pass http://host.docker.internal:8080;
新：
proxy_pass http://192.168.2.109:8080;
