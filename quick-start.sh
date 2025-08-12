#!/bin/bash

# 🛫 航空订单系统 - 快速启动脚本
# 适用于 AWS us-east-2 区域，账户 381492153714

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_header() {
    echo -e "${PURPLE}========================================${NC}"
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}========================================${NC}"
    echo ""
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查必要工具
check_prerequisites() {
    print_info "检查必要工具..."
    
    local missing_tools=()
    
    if ! command -v git &> /dev/null; then
        missing_tools+=("git")
    fi
    
    if ! command -v docker &> /dev/null && ! command -v podman &> /dev/null; then
        missing_tools+=("docker 或 podman")
    fi
    
    if ! command -v aws &> /dev/null; then
        missing_tools+=("aws-cli")
    fi
    
    if [ ${#missing_tools[@]} -ne 0 ]; then
        print_error "缺少必要工具: ${missing_tools[*]}"
        echo ""
        echo "请安装缺少的工具："
        echo "• Git: https://git-scm.com/downloads"
        echo "• Docker: https://docs.docker.com/get-docker/"
        echo "• AWS CLI: https://aws.amazon.com/cli/"
        exit 1
    fi
    
    print_success "所有必要工具已安装"
}

# 显示欢迎信息
show_welcome() {
    clear
    print_header "🛫 航空订单系统 - 快速部署向导"
    
    echo -e "${BLUE}欢迎使用航空订单系统快速部署脚本！${NC}"
    echo ""
    echo "这个脚本将帮助你："
    echo "✅ 配置AWS环境"
    echo "✅ 构建Docker镜像"
    echo "✅ 部署到AWS EC2"
    echo "✅ 配置数据库连接"
    echo "✅ 设置CI/CD流水线"
    echo ""
    echo -e "${YELLOW}目标环境信息：${NC}"
    echo "• AWS区域: us-east-2 (俄亥俄州)"
    echo "• AWS账户: 381492153714"
    echo "• 用户: FUser23"
    echo ""
    
    read -p "是否继续？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "部署已取消"
        exit 0
    fi
}

# 检查AWS配置
check_aws_config() {
    print_info "检查AWS配置..."
    
    if ! aws sts get-caller-identity &> /dev/null; then
        print_warning "AWS CLI未配置或凭证无效"
        echo ""
        echo "请按照以下步骤配置AWS CLI："
        echo "1. 登录 https://shida-awscloud3.signin.aws.amazon.com/console"
        echo "2. 用户名: FUser23，密码: p8Bd41^["
        echo "3. 进入 IAM > 用户 > FUser23 > 安全凭证"
        echo "4. 创建访问密钥"
        echo "5. 运行 'aws configure' 并输入密钥信息"
        echo ""
        read -p "配置完成后按回车继续..." -r
        
        if ! aws sts get-caller-identity &> /dev/null; then
            print_error "AWS配置验证失败"
            exit 1
        fi
    fi
    
    local current_region=$(aws configure get region)
    if [ "$current_region" != "us-east-2" ]; then
        print_warning "当前AWS区域是 $current_region，建议使用 us-east-2"
        read -p "是否切换到 us-east-2 区域？(y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            aws configure set region us-east-2
            print_success "已切换到 us-east-2 区域"
        fi
    fi
    
    print_success "AWS配置验证通过"
}

# 构建Docker镜像
build_docker_image() {
    print_info "构建Docker镜像..."
    
    # 检查是否使用podman
    if command -v podman &> /dev/null; then
        DOCKER_CMD="podman"
    else
        DOCKER_CMD="docker"
    fi
    
    print_info "使用 $DOCKER_CMD 构建镜像..."
    
    if $DOCKER_CMD build -t airline-order-app:latest .; then
        print_success "Docker镜像构建成功"
    else
        print_error "Docker镜像构建失败"
        exit 1
    fi
}

# 部署选择菜单
show_deployment_menu() {
    print_header "选择部署方式"
    
    echo "请选择部署方式："
    echo "1) 🚀 完整AWS部署 (推荐)"
    echo "2) 🐳 本地Docker部署 (测试用)"
    echo "3) ⚙️  仅配置GitHub Actions"
    echo "4) 📚 查看部署文档"
    echo "5) 🔧 故障排除"
    echo "0) 退出"
    echo ""
    
    read -p "请选择 (0-5): " choice
    
    case $choice in
        1) deploy_to_aws ;;
        2) deploy_locally ;;
        3) setup_github_actions ;;
        4) show_documentation ;;
        5) troubleshooting ;;
        0) exit 0 ;;
        *) 
            print_error "无效选择"
            show_deployment_menu
            ;;
    esac
}

# AWS完整部署
deploy_to_aws() {
    print_header "🚀 AWS完整部署"
    
    print_info "开始AWS基础设施部署..."
    
    if [ -f "aws-infrastructure/deploy-infrastructure.sh" ]; then
        chmod +x aws-infrastructure/deploy-infrastructure.sh
        cd aws-infrastructure
        ./deploy-infrastructure.sh
        cd ..
        print_success "AWS基础设施部署完成"
    else
        print_error "找不到AWS部署脚本"
        return 1
    fi
    
    print_info "部署完成！请查看 deployment-info.txt 获取访问信息"
}

# 本地Docker部署
deploy_locally() {
    print_header "🐳 本地Docker部署"
    
    print_info "启动本地Docker服务..."
    
    # 检查docker-compose文件
    if [ ! -f "docker-compose.yml" ]; then
        print_error "找不到 docker-compose.yml 文件"
        return 1
    fi
    
    # 启动服务
    if command -v docker-compose &> /dev/null; then
        docker-compose up -d
    elif command -v podman-compose &> /dev/null; then
        podman-compose up -d
    else
        print_error "找不到 docker-compose 或 podman-compose"
        return 1
    fi
    
    print_success "本地服务启动成功"
    echo ""
    echo "访问地址："
    echo "• 应用主页: http://localhost:8080"
    echo "• API文档: http://localhost:8080/swagger-ui/index.html"
    echo "• 健康检查: http://localhost:8080/actuator/health"
}

# 配置GitHub Actions
setup_github_actions() {
    print_header "⚙️ GitHub Actions配置"
    
    echo "GitHub Actions Secrets配置指南："
    echo ""
    echo "请在GitHub仓库的 Settings > Secrets and variables > Actions 中添加："
    echo ""
    echo "🔑 AWS相关："
    echo "• AWS_ACCESS_KEY_ID: <你的AWS访问密钥ID>"
    echo "• AWS_SECRET_ACCESS_KEY: <你的AWS秘密访问密钥>"
    echo ""
    echo "🖥️ EC2相关："
    echo "• EC2_HOST: <EC2实例公网IP>"
    echo "• EC2_USERNAME: ec2-user"
    echo "• EC2_PRIVATE_KEY: <EC2密钥对私钥内容>"
    echo ""
    echo "🗄️ 数据库相关："
    echo "• DB_HOST: <RDS数据库端点>"
    echo "• DB_PASSWORD: <数据库密码>"
    echo ""
    echo "🔐 应用相关："
    echo "• JWT_SECRET: <JWT密钥>"
    echo ""
    
    read -p "配置完成后按回车继续..." -r
    print_success "GitHub Actions配置完成"
}

# 显示文档
show_documentation() {
    print_header "📚 部署文档"
    
    echo "可用文档："
    echo ""
    echo "📋 主要文档："
    echo "• COMPLETE_DEPLOYMENT_GUIDE.md - 完整部署指南"
    echo "• DEPLOYMENT_PLAN.md - 部署计划详解"
    echo ""
    echo "🔧 技术文档："
    echo "• CORS_AND_PROXY_GUIDE.md - 跨域配置指南"
    echo "• DATABASE_MIGRATION_GUIDE.md - 数据库迁移指南"
    echo ""
    echo "🛠️ 配置文件："
    echo "• .github/workflows/deploy.yml - CI/CD配置"
    echo "• aws-infrastructure/ - AWS基础设施脚本"
    echo "• nginx/nginx.conf - Nginx配置"
    echo "• proxy.conf.json - 开发环境代理配置"
    echo ""
    
    read -p "按回车返回主菜单..." -r
    show_deployment_menu
}

# 故障排除
troubleshooting() {
    print_header "🔧 故障排除"
    
    echo "常见问题和解决方案："
    echo ""
    echo "❌ Docker构建失败："
    echo "• 检查网络连接"
    echo "• 清理Docker缓存: docker system prune -a"
    echo "• 使用podman替代docker"
    echo ""
    echo "❌ AWS连接失败："
    echo "• 检查AWS凭证配置"
    echo "• 验证区域设置 (us-east-2)"
    echo "• 检查IAM权限"
    echo ""
    echo "❌ 数据库连接失败："
    echo "• 检查安全组配置"
    echo "• 验证数据库端点"
    echo "• 测试网络连通性"
    echo ""
    echo "❌ 应用无法访问："
    echo "• 检查EC2实例状态"
    echo "• 验证安全组端口开放"
    echo "• 查看应用日志"
    echo ""
    
    echo "🔍 调试命令："
    echo "• 查看容器状态: docker ps"
    echo "• 查看容器日志: docker logs <container-id>"
    echo "• 测试端口: telnet <host> <port>"
    echo "• 检查AWS资源: aws ec2 describe-instances"
    echo ""
    
    read -p "按回车返回主菜单..." -r
    show_deployment_menu
}

# 主函数
main() {
    # 显示欢迎信息
    show_welcome
    
    # 检查前置条件
    check_prerequisites
    
    # 检查AWS配置
    check_aws_config
    
    # 构建Docker镜像
    build_docker_image
    
    # 显示部署菜单
    show_deployment_menu
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
