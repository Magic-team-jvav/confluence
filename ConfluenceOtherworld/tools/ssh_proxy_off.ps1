<#
  取消 SSH 代理：把 ~/.ssh/config 里指向本地代理的 ProxyCommand 注释掉。
  用法：
    ssh_proxy_off.ps1              # 只处理指向 127.0.0.1/localhost 的 ProxyCommand
    ssh_proxy_off.ps1 -All         # 注释掉所有 ProxyCommand 行
  特性：改前自动备份（config.bak），只注释不删除，可随时用 ssh_proxy_on 还原。
#>
#Requires -Version 5.1
param([switch]$All)

$ErrorActionPreference = 'Stop'
function Write-Ok  ([string]$m) { Write-Host "[ OK ] $m" -ForegroundColor Green }
function Write-Warn([string]$m) { Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Info([string]$m) { Write-Host "[ .. ] $m" -ForegroundColor Cyan }

$sshDir  = Join-Path $env:USERPROFILE '.ssh'
$cfgPath = Join-Path $sshDir 'config'

Write-Host '============ 取消 SSH 代理 ============' -ForegroundColor Cyan
if (-not (Test-Path $cfgPath)) {
    Write-Warn "没有找到 $cfgPath，无需处理。"
    exit 0
}

$raw = [System.IO.File]::ReadAllText($cfgPath)
$lines = $raw -split "`r?`n"
$backup = "$cfgPath.bak"
Copy-Item $cfgPath $backup -Force
Write-Info "已备份到 $backup"

$changed = New-Object System.Collections.Generic.List[string]
$outLines = New-Object System.Collections.Generic.List[string]
foreach ($l in $lines) {
    $isProxy = ($l -match '^\s*ProxyCommand\b' -and $l -notmatch '^\s*#')
    $isLocal = ($l -match '127\.0\.0\.1|localhost')
    if ($isProxy -and ($All -or $isLocal)) {
        $outLines.Add('#[proxy-off] ' + $l.Trim())
        $changed.Add($l.Trim())
    } else {
        $outLines.Add($l)
    }
}

if ($changed.Count -eq 0) {
    Write-Warn '没有找到需要处理的 ProxyCommand 行（可能已经是取消状态）。'
} else {
    [System.IO.File]::WriteAllText($cfgPath, ($outLines -join "`r`n"), (New-Object System.Text.UTF8Encoding($false)))
    foreach ($c in $changed) { Write-Ok "已注释: $c" }
}

Write-Host ''
Write-Host '---------------- 生效后的 config ----------------' -ForegroundColor Cyan
Get-Content $cfgPath | ForEach-Object { Write-Host ('  ' + $_) }

Write-Host ''
Write-Info '验证直连（8 秒超时）: ssh -T git@github.com'
$env:GIT_TERMINAL_PROMPT = '0'
$out = & ssh -o ConnectTimeout=8 -o BatchMode=yes -T git@github.com 2>&1 | Out-String
if ($out -match 'successfully authenticated') {
    Write-Ok '直连 SSH 正常，可以 git pull。'
} else {
    Write-Warn '直连未成功，输出如下：'
    Write-Host ($out.Trim())
    Write-Warn '若提示 Connection timed out：说明当前网络直连 SSH 被拦（github.com:22 与 ssh.github.com:443 都可能被拦）。'
    Write-Warn '可选方案：① 用 ssh_proxy_on.bat 还原代理；② 改用 HTTPS 远程：'
    Write-Host '          git remote set-url origin https://github.com/Magic-team-jvav/confluence.git'
}
Write-Host ''
Write-Host '还原命令: tools\ssh_proxy_on.bat' -ForegroundColor Green
