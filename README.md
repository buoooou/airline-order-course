在本地生成新密钥对: 打开终端，运行以下命令。这把钥匙没有密码，便于自动化脚本使用。
ssh-keygen -t rsa -b 4096 -f github_actions_deploy_key -N ""

安装“新门锁”: 将新生成的公钥 (github_actions_deploy_key.pub) 的内容，追加到你 EC2 服务器的 ~/.ssh/authorized_keys 文件中。
~/.ssh/authorized_keys 来确保 SSH 服务的安全要求。

登录 Docker Hub，在“Account Settings” -> “Security”中创建一个新的 Access Token。
立即复制并保管好这个令牌，因为它只会显示一次。

将我们刚刚准备好的所有敏感信息，安全地存放到 GitHub 仓库的 "Settings" -> "Secrets and variables" -> "Actions" 中。
Secret 名称存放的值
DOCKERHUB_USERNAME你的 Docker Hub 用户名
DOCKERHUB_TOKEN上一步生成的 Docker Hub 访问令牌
EC2_HOST你 EC2 服务器的公网 IP 地址
EC2_USERNAME你登录 EC2 的用户名 (如 ubuntu 或 ec2-user)
SSH_PRIVATE_KEY新生成的私钥 github_actions_deploy_key 的全部内容

详细看：https://full-stack.postions.app/github-cicd
