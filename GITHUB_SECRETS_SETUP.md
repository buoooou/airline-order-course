# GitHub Secrets 配置指南

为了让 CI/CD 流水线正常工作，需要在 GitHub 仓库中配置以下 Secrets。

## 🔐 必需的 Secrets

### AWS 相关
```
AWS_ACCESS_KEY_ID
- 描述: AWS 访问密钥 ID
- 值: 你的 AWS Access Key ID
- 用途: 用于访问 AWS 服务（ECR、EC2 等）

AWS_SECRET_ACCESS_KEY  
- 描述: AWS 秘密访问密钥
- 值: 你的 AWS Secret Access Key
- 用途: 用于访问 AWS 服务（ECR、EC2 等）
```

### EC2 部署相关
```
EC2_SSH_PRIVATE_KEY
- 描述: EC2 实例的 SSH 私钥
- 值: fuser23-airline-order-keypair.pem 文件的完整内容
- 用途: 用于 SSH 连接到 EC2 实例进行部署

EC2_HOST
- 描述: EC2 实例的公网 IP 地址
- 值: 18.116.240.81
- 用途: 指定部署目标服务器
```

### 通知相关（可选）
```
SLACK_WEBHOOK
- 描述: Slack Webhook URL
- 值: https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK
- 用途: 发送部署状态通知到 Slack
```

## 📝 配置步骤

### 1. 进入 GitHub 仓库设置
1. 打开你的 GitHub 仓库
2. 点击 `Settings` 选项卡
3. 在左侧菜单中选择 `Secrets and variables` > `Actions`

### 2. 添加 Repository Secrets
点击 `New repository secret` 按钮，逐个添加上述 secrets。

### 3. 验证配置
添加完成后，你应该看到以下 secrets：
- ✅ AWS_ACCESS_KEY_ID
- ✅ AWS_SECRET_ACCESS_KEY  
- ✅ EC2_SSH_PRIVATE_KEY
- ✅ EC2_HOST
- ✅ SLACK_WEBHOOK (可选)

## 🔧 Environment 配置（可选）

如果需要更严格的部署控制，可以配置 GitHub Environments：

### 1. 创建 Environments
1. 在仓库设置中选择 `Environments`
2. 创建两个环境：
   - `development` (用于 develop 分支)
   - `production` (用于 main 分支)

### 2. 配置保护规则
对于 `production` 环境，建议配置：
- ✅ Required reviewers (需要审核者批准)
- ✅ Wait timer (等待时间)
- ✅ Deployment branches (限制部署分支)

## 🚀 触发 CI/CD

配置完成后，CI/CD 将在以下情况自动触发：

### 代码推送触发
```bash
# 推送到 main 分支 -> 触发生产环境部署
git push origin main

# 推送到 develop 分支 -> 触发开发环境部署  
git push origin develop

# 创建 Pull Request -> 触发代码质量检查和构建
```

### 手动触发
1. 进入 GitHub 仓库的 `Actions` 选项卡
2. 选择 `CI/CD Pipeline for Airline Order Management System`
3. 点击 `Run workflow` 按钮

## 📊 监控流水线

### 查看执行状态
1. 进入 `Actions` 选项卡
2. 查看最新的 workflow 运行状态
3. 点击具体的运行记录查看详细日志

### 常见状态
- 🟢 **Success**: 所有步骤成功完成
- 🔴 **Failure**: 某个步骤失败，需要检查日志
- 🟡 **In Progress**: 正在执行中
- ⚪ **Queued**: 等待执行

## 🛠️ 故障排除

### 常见问题

#### 1. AWS 认证失败
```
Error: The security token included in the request is invalid
```
**解决方案**: 检查 `AWS_ACCESS_KEY_ID` 和 `AWS_SECRET_ACCESS_KEY` 是否正确

#### 2. SSH 连接失败
```
Error: Permission denied (publickey)
```
**解决方案**: 
- 检查 `EC2_SSH_PRIVATE_KEY` 是否包含完整的私钥内容
- 确保私钥格式正确（包含 `-----BEGIN RSA PRIVATE KEY-----` 等）

#### 3. ECR 推送失败
```
Error: denied: User is not authorized to perform: ecr:BatchCheckLayerAvailability
```
**解决方案**: 确保 AWS 用户有 ECR 相关权限

#### 4. 部署超时
```
Error: Timeout waiting for deployment
```
**解决方案**: 
- 检查 EC2 实例是否正常运行
- 确保安全组允许相应端口访问
- 检查 Docker 服务是否正常

### 调试技巧

#### 1. 启用调试日志
在 workflow 文件中添加：
```yaml
env:
  ACTIONS_STEP_DEBUG: true
  ACTIONS_RUNNER_DEBUG: true
```

#### 2. SSH 调试
```yaml
- name: Debug SSH connection
  run: |
    ssh -vvv ec2-user@${{ secrets.EC2_HOST }} 'echo "SSH connection successful"'
```

#### 3. 检查服务状态
```yaml
- name: Check service status
  run: |
    ssh ec2-user@${{ secrets.EC2_HOST }} '
      docker ps
      docker logs airline-backend --tail 50
      docker logs airline-frontend --tail 50
    '
```

## 📈 优化建议

### 1. 缓存优化
- Maven 依赖缓存
- Node.js 依赖缓存  
- Docker 层缓存

### 2. 并行执行
- 前后端测试并行运行
- 镜像构建并行执行

### 3. 条件执行
- 只在代码变更时执行相关步骤
- 跳过不必要的测试和构建

### 4. 通知优化
- 只在失败时发送通知
- 包含更多上下文信息

---

**注意**: 请妥善保管所有密钥和凭证，不要在代码中硬编码任何敏感信息。
