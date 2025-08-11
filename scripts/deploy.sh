#!/bin/bash

set -e

APP_NAME="airline-order"
APP_DIR="/opt/$APP_NAME"
JAR_FILE="airline-order-backend-0.0.1-SNAPSHOT.jar"
SERVICE_FILE="/etc/systemd/system/$APP_NAME.service"

echo "Starting deployment of $APP_NAME..."

# Create application directory if it doesn't exist
sudo mkdir -p $APP_DIR
sudo chown $USER:$USER $APP_DIR

# Copy JAR file
cp deploy/*.jar $APP_DIR/

# Deploy frontend files
sudo mkdir -p /var/www/airline-order
sudo cp -r deploy/frontend/* /var/www/airline-order/
sudo chown -R www-data:www-data /var/www/airline-order

# Create systemd service file
sudo tee $SERVICE_FILE > /dev/null <<EOF
[Unit]
Description=Airline Order Backend Service
After=network.target

[Service]
Type=simple
User=ubuntu
Group=ubuntu
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/$JAR_FILE
Restart=always
RestartSec=10

# Environment variables
Environment=SPRING_PROFILES_ACTIVE=prod
Environment=SPRING_DATASOURCE_URL=\${DB_URL}
Environment=SPRING_DATASOURCE_USERNAME=\${DB_USERNAME}
Environment=SPRING_DATASOURCE_PASSWORD=\${DB_PASSWORD}
Environment=JWT_SECRET=63ffbc2b8d13ad5180ed7ae7c67f18c85d86046732fc9ced6a02a9d50abb1a03
Environment=SERVER_PORT=8080

# JVM Options
Environment=JAVA_OPTS="-Xmx512m -Xms256m"

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd
sudo systemctl daemon-reload

# Configure nginx for separate frontend/backend
sudo tee /etc/nginx/sites-available/airline-order > /dev/null <<EOF
server {
    listen 80;
    server_name _;

    # Serve frontend static files
    location / {
        root /var/www/airline-order;
        try_files \$uri \$uri/ /index.html;
    }

    # Proxy API requests to backend
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Enable site and restart nginx
sudo ln -sf /etc/nginx/sites-available/airline-order /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl restart nginx

echo "Deployment completed successfully!"