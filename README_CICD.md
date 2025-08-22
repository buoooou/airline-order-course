# CI/CD

参照：
https://full-stack.postions.app/github-cicd
https://java.postions.app/java-github-cicd
   

### 一、前期准备工作
### 1.1 确认网络设置 （参照 主目录下的 README.md 中的网络设置）

### 1.2 配置 WSL 环境
<!-- 确保 WSL 中的 Ubuntu 已正确配置：

# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget unzip

# 确认 Docker 已正确安装并运行（CICD过程中不需要）
docker --version
docker-compose --version -->

# 确保当前用户加入 docker 组（避免每次使用 sudo）
sudo usermod -aG docker $USER -->

### 1.3 配置 GitHub 仓库

# WIN11 中克隆代码仓库（如果尚未克隆）
git clone https://github.com/fm-t7/airline-order-course.git
cd airline-order-course

# 确保本地分支与远程同步
git checkout main
git pull origin main

### 1.4 登录AWS 建立EC2实例并下载 EC2 密钥
# 登录AWS控制台
URL: https://shida-awscloud3.signin.aws.amazon.com/console

# 选择 EC2 实例
选择 EC2 实例 -> 启动实例 -> 选择 Amazon Linux 2 AMI -> 选择实例类型 -> 选择 VPC 和子网 -> 选择密钥对 -> 选择启动模板 -> 启动实例

# 连接 EC2 实例
# 选择 EC2 实例 -> 连接 -> 选择实例 -> 选择实例
# 下载密钥文件并保存到本地（如 airline-fuser26.pem）

### 1.5 在 Ubuntu 上配置 AWS EC2 信任密钥
使用 vs code 链接 Ubuntu, 自然切换为 ubuntu 账户(/home/ubuntu) 
(不需要使用 su - ubuntu，whoami, pwd)

# Copy EC2的密码文件到/home/ubuntu
copy /mnt/c/airline-order-course/airline-fuser26.pem /home/ubuntu

# 设置属性 (# -rw-------)
chmod 600 airline-fuser26.pem
# 确认属性
ls -ld airline-fuser26.pem         

# 连接 EC2, 在authorized_keys下生成私钥
ssh -i airline-fuser26.pem ubuntu@3.25.139.89

# 在 EC2 上创建 .ssh 目录并设置权限（仅当前用户可读写执行）
mkdir -p ~/.ssh && chmod 700 ~/.ssh
# 创建 authorized_keys 文件并设置权限：
touch ~/.ssh/authorized_keys  # 创建文件
chmod 600 ~/.ssh/authorized_keys  # 权限必须为600，否则SSH会拒绝使用

# 生成无密码的SSH密钥对，用于GitHub Actions部署（github_actions_deploy_key 和 github_actions_deploy_key.pub）
ssh-keygen -t rsa -b 4096 -f github_actions_deploy_key -N ""
# 重新执行追加公钥的命令：
cat github_actions_deploy_key.pub >> ~/.ssh/authorized_keys
# 验证
ssh -i github_actions_deploy_key ubuntu@3.25.139.89

### 1.6 注册DockerHub账号
使用公司代理，登录 https://www.docker.com/, 使用github 账号登录dockerhub账号，获得Personal access tokens
docker login -u suifm -p <password>


### 二、CI/CD 流程设计

### 2.1 流程概览
代码提交触发 GitHub Actions
自动执行测试（前端 + 后端）
构建 Docker 镜像
推送镜像到 AWS ECR
部署到 AWS EC2
执行健康检查

### 2.2 GitHub Actions 配置
创建 / 修改 .github/workflows/main.yml 文件

### 2.3 配置 GitHub Secrets
在 GitHub 仓库页面添加以下 Secrets：

<!-- 
AWS_ACCESS_KEY_ID: AWS 访问密钥 ID
AWS_SECRET_ACCESS_KEY: AWS 密钥
AWS_ACCOUNT_ID: AWS 账户 ID 
-->
AWS_REGION: AWS 区域 (as-southeast-2)
EC2_HOST: EC2 实例的公网 IP 或域名
EC2_USERNAME: EC2 登录用户名（通常是 ubuntu）
SSH_PRIVATE_KEY: 用于登录 EC2 的 SSH 私钥
DOCKER_IMAGE_NAME: Docker 镜像名称
DOCKERHUB_TOKEN： DockerHub 账号的 Token
DOCKERHUB_USERNAME：DockerHub 账号的用户名


### 三、本地开发与 CI/CD 衔接脚本
# 本地开发
完成前后端开发和测试

# 测试前后端的打包（Dockerfile中会重新制作）
1. 测试打包后端, 生成jar包
cd backend
./mvnw clean package -DskipTests

2. 编译前端，生成dist目录
cd frontend
npm run build


### 四、Dockerfile 和 Docker Compose 配置文件
生成多阶段构建的单镜像部署, 然后使用 Docker Compose 部署到同一个 EC2 实例。

### 4.1 Dockerfile
从源代码开始多阶段构建，注意

### 4.2 Docker Compose
制作单一镜像，注意 Dist 目录结构


### 五、CICD

建立新分支:
git checkout -b dev

开发，测试，提交代码:
git add .
git commit -m "描述你的更改"
git push origin dev

合并到主分支
git checkout main
git merge dev
git push origin main
git push origin main --force

触发 CI/CD 流程:
在 GitHub 仓库页面，Actions 标签页，点击 Run workflow，选择你要运行的工作流，点击 Run workflow 按钮，等待工作流完成。


### 六、常见问题

### 6.1 Docker compose 部署失败
1. 确认docker-compose.yml部署位置
docker-compose.yml 要提前配置在EC2的/home/ubuntu目录下，并且要确保配置文件中，容器名称和镜像名称要保持一致。
2. 确认镜像名
docker-compose.yml 中的镜像名，和docerhub上的镜像名要保持一致。参数取不到，采取直接书写的方式。

### 6.2 部署成功后，前端无法访问
因为 Dockerfile 部署后前端目录结构变化，导致前端无法访问。
COPY --from=frontend-builder /app/dist/*/browser/ ./src/main/resources/static/

解决方法：
修改后端的放行规则
