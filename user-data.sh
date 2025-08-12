#!/bin/bash
yum update -y
yum install -y docker jq
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# 安装Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 安装AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install

# 创建应用目录
mkdir -p /opt/airline-order
chown ec2-user:ec2-user /opt/airline-order

# 设置AWS区域
echo "export AWS_DEFAULT_REGION=us-east-2" >> /home/ec2-user/.bashrc
