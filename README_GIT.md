# 1. Fork 原项目
打开原项目的 GitHub 页面（如https://github.com/原作者/项目名）。
点击右上角的Fork按钮，选择自己的账号，完成后你的账号下会出现一个同名仓库（即 fork 后的仓库）。

# 2. 将 fork 的仓库拉取到本地
进入你 fork 后的仓库页面（https://github.com/你的账号/项目名），点击Code按钮，复制仓库 URL（HTTPS 或 SSH）。
本地打开终端，执行克隆命令：
bash
git clone https://github.com/你的账号/项目名.git  # 替换为你的仓库URL
cd 项目名  # 进入项目目录

# 3. 创建新分支并编辑代码
不建议直接在main分支修改（保持主分支干净），创建并切换到新分支：
bash
git checkout -b feature/new-function  # 分支名建议清晰（如功能名、bug修复等）

用编辑器修改代码（如添加新功能、修复 bug）。

# 4. 提交修改并推送到自己的 fork 仓库
查看修改内容：
bash
git status  # 确认修改的文件

暂存并提交：
bash
git add .  # 暂存所有修改（或指定文件：git add 文件名）
git commit -m "添加了XX功能：具体修改说明"  # 提交时写清楚修改目的

推送到自己 fork 仓库的新分支：
bash
git push origin feature/new-function  # origin默认指向你的fork仓库

查看远程分支：
git branch -r

# 5. 将新分支合并到自己 fork 仓库的 main 分支
有两种方式：

本地合并（推荐，更可控）：
bash
git checkout main  # 切换到main分支
git merge feature/new-function  # 合并新分支到main

# 若有冲突，编辑文件解决冲突后，执行：git add 冲突文件；git commit -m "解决合并冲突"
git push origin main  # 推送到自己fork仓库的main分支

GitHub 网页合并：在你的 fork 仓库页面，切换到feature/new-function分支，点击Compare & pull request，目标分支选择自己仓库的main，按提示完成合并。

# 6. 向原项目发起 Pull Request（贡献代码）
进入你 fork 仓库的 GitHub 页面，切换到main分支（或刚才的新分支）。
点击Pull request按钮，选择原项目的仓库（原作者/项目名）和目标分支（通常是main或master）。
填写 PR 标题和描述（说明你的修改内容、目的），点击Create pull request。
原项目维护者会收到通知，审核通过后可能合并到原项目，你的修改就会出现在原项目中。

# 7. （可选）同步原项目的最新更新到自己的 fork 仓库
如果原项目有新更新，想同步到自己的 fork 仓库：

关联原项目为上游仓库（仅需执行一次）：
git remote add upstream https://github.com/原作者/项目名.git

拉取原项目的最新代码：
git fetch upstream

合并到自己的main分支：
git checkout main
git merge upstream/main
git push origin main  # 推送到自己的fork仓库