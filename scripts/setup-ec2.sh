#!/bin/bash

# EC2 服务器初始化脚本

set -e

echo "Starting EC2 setup for Airline Order application..."

# 更新系统
sudo apt-get update
sudo apt-get upgrade -y

# 安装 Java 8
sudo apt-get install -y openjdk-8-jdk

# 数据库使用外部服务，无需本地安装

# 创建应用目录
sudo mkdir -p /opt/airline-order
sudo chown ubuntu:ubuntu /opt/airline-order

# 安装 nginx (用于反向代理)
sudo apt-get install -y nginx

sudo tee /etc/nginx/sites-available/airline-order > /dev/null <<EOF
server {
    listen 80;
    server_name _;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# 启用站点
sudo ln -sf /etc/nginx/sites-available/airline-order /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# 测试并重启 nginx
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx

# 配置防火墙
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443
sudo ufw allow 4200
sudo ufw allow 8080
sudo ufw --force enable

echo "EC2 setup completed successfully!"
echo "Please make sure to:"
echo "1. Set the DB_URL environment variable (external database connection)"
echo "2. Set the DB_USERNAME environment variable"
echo "3. Set the DB_PASSWORD environment variable"
echo "4. Set the JWT_SECRET environment variable"
echo "5. Configure your GitHub repository secrets"