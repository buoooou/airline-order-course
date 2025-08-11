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
Environment=JWT_SECRET=\${JWT_SECRET}

# JVM Options
Environment=JAVA_OPTS="-Xmx512m -Xms256m"

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd
sudo systemctl daemon-reload

echo "Deployment completed successfully!"