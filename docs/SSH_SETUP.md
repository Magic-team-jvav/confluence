# SSH 设置指南

适用场景：中国大陆访问 GitHub，端口 22 被墙，通过 HTTP 代理走 443 端口。

## 1. 生成 SSH Key

```bash
ssh-keygen -t ed25519 -C "你的邮箱@example.com" -f ~/.ssh/id_ed25519 -N ""
```

## 2. 添加公钥到 GitHub

```bash
cat ~/.ssh/id_ed25519.pub
```

复制输出内容 → https://github.com/settings/ssh/new → 粘贴保存。

## 3. 配置 SSH 走代理

编辑 `~/.ssh/config`：

```
Host github.com
    Hostname ssh.github.com
    Port 443
    ProxyCommand connect -H 127.0.0.1:7897 %h %p
```

> 代理地址 `127.0.0.1:7897` 按你实际用的 clash/v2ray 端口调整。
> `connect` 工具 Git Bash 自带（`/mingw64/bin/connect`）。

## 4. 验证

```bash
ssh -T git@github.com
```

看到 `Hi <username>! You've successfully authenticated` 即成功。

## 5. 仓库切换 SSH 远程

```bash
git remote set-url origin git@github.com:<owner>/<repo>.git
```

子模块批量查看：

```bash
git submodule foreach 'git remote get-url origin'
```

## 6. 已有仓库快捷脚本

如果 clone 了新环境，一键切换所有子模块：

```bash
# 主仓库
git remote set-url origin git@github.com:Magic-team-jvav/confluence.git

# 子模块
git -C Confluence-Magic-Lib remote set-url origin git@github.com:westernat/Confluence-Magic-Lib.git
git -C PortLib remote set-url origin git@github.com:westernat/MesdagPortLib.git
git -C TerraCurio remote set-url origin git@github.com:westernat/TerraCurio.git
git -C TerraFurniture remote set-url origin git@github.com:MakerTechno/TerraFurniture.git
```

## 故障排查

| 现象 | 原因 | 解决 |
|------|------|------|
| `Connection timed out` (22) | 端口被封 | 改用 443 端口 |
| `Connection timed out` (443) | 代理未运行或没配 ProxyCommand | 启动代理 / 检查 SSH config |
| `Host key verification failed` | 首次连接未信任 host | `ssh -T -o StrictHostKeyChecking=accept-new git@github.com` |
| `Permission denied (publickey)` | 公钥未添加 | 去 GitHub 添加公钥 |
| IDE 里 push 报认证错 | IDE 覆盖了 credential.helper | 切 SSH 不受 IDE 影响 |
