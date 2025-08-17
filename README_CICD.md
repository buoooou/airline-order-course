# CI/CD

### 一、前期准备工作

### 1.1 配置 WSL 环境
确保 WSL 中的 Ubuntu 已正确配置：

# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y git curl wget unzip

# 确认 Docker 已正确安装并运行
docker --version
docker-compose --version

# 确保当前用户加入 docker 组（避免每次使用 sudo）
sudo usermod -aG docker $USER

### 1.2 配置 AWS 命令行工具
### 1.2.1 登录AWS控制台
URL: https://shida-awscloud3.signin.aws.amazon.com/console

### 1.3 配置 GitHub 仓库

# 克隆代码仓库（如果尚未克隆）
git clone https://github.com/fm-t7/airline-order-course.git
cd airline-order-course

# 确保本地分支与远程同步
git checkout main
git pull origin main

### 1.4 配置 EC2 信任密钥
使用 vs code 链接 Ubuntu, 自然切换为 ubuntu 账户(/home/ubuntu) 
(不需要使用 su - ubuntu，whoami, pwd)
# Copy EC2的密码文件到/home/ubuntu
copy /mnt/c/airline-order-course/airline-fuser26.pem /home/ubuntu
# 设置属性 (# -rw-------)
chmod 600 airline-fuser26.pem
# 确认属性
ls -ld airline-fuser26.pem         

# 创建 .ssh 目录并设置权限（仅当前用户可读写执行）
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

# 连接 EC2, 在authorized_keys下生成私钥
ssh -i airline-fuser26.pem ubuntu@3.25.139.89


### 1.5 注册DockerHub账号
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

AWS_ACCESS_KEY_ID: AWS 访问密钥 ID
AWS_SECRET_ACCESS_KEY: AWS 密钥
AWS_ACCOUNT_ID: AWS 账户 ID
AWS_REGION: AWS 区域 (as-southeast-2)
EC2_HOST: EC2 实例的公网 IP 或域名
EC2_USERNAME: EC2 登录用户名（通常是 ubuntu）
EC2_SSH_KEY: 用于登录 EC2 的 SSH 私钥

### 三、Docker 配置文件
生成多阶段构建的单镜像部署

### 四、本地开发与 CI/CD 衔接脚本


### 五、使用说明
准备工作:
在 AWS 控制台启动 EC2 实例

# 触发 CI/CD:
# 提交代码到main分支将自动触发CI/CD流程
git add .
git commit -m "描述你的更改"
git push origin main

监控 CI/CD 流程:
登录 GitHub 仓库
进入 Actions 标签页查看流水线运行状态


### 七、注意事项
文件格式: Windows 和 Linux 的换行符不同，确保脚本文件使用 LF 格式而非 CRLF

通过 .gitattributes 文件配置 Git 自动转换文件格式

<!-- # 在WSL中转换文件格式
dos2unix *.sh

权限问题: 确保脚本有执行权限
chmod +x *.sh -->

AWS 资源成本: 定期检查 AWS 资源使用情况，避免不必要的支出
安全: 不要将敏感信息硬编码在脚本中，始终使用环境变量或 Secrets
备份: 定期备份数据库和关键配置