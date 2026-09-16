<#
  恢复/设置 SSH 代理：让 ~/.ssh/config 里的 github.com 走本地代理。
  用法：
    ssh_proxy_on.ps1                # 默认端口 7897
    ssh_proxy_on.ps1 -Port 7890     # 指定端口
    ssh_proxy_on.ps1 -HostName github.com -RealHost ssh.github.com -RealPort 443
  特性：改前自动备份（config.bak）；优先还原被 ssh_proxy_off 注释掉的行；否则在 Host 块内插入。
#>
#Requires -Version 5.1
param(
    [int]$Port = 7897,
    [string]$HostName = 'github.com',
    [string]$RealHost = 'ssh.github.com',
    [int]$RealPort = 443
)

$ErrorActionPreference = 'Stop'
function Write-Ok  ([string]$m) { Write-Host "[ OK ] $m" -ForegroundColor Green }
function Write-Warn([string]$m) { Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Write-Info([string]$m) { Write-Host "[ .. ] $m" -ForegroundColor Cyan }

$sshDir  = Join-Path $env:USERPROFILE '.ssh'
$cfgPath = Join-Path $sshDir 'config'
$proxyLine = ('    ProxyCommand connect -H 127.0.0.1:{0} %h %p' -f $Port)

Write-Host '============ 设置 SSH 代理 ============' -ForegroundColor Cyan
if (-not (Test-Path $sshDir)) { New-Item -ItemType Directory -Path $sshDir | Out-Null }

if (Test-Path $cfgPath) {
    Copy-Item $cfgPath "$cfgPath.bak" -Force
    Write-Info "已备份到 $cfgPath.bak"
    $lines = New-Object System.Collections.Generic.List[string]
    ([System.IO.File]::ReadAllText($cfgPath) -split "`r?`n") | ForEach-Object { $lines.Add($_) }

    # 1) 优先还原被 ssh_proxy_off 注释掉的行
    $restored = $false
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^#\[proxy-off\]\s*ProxyCommand\b') {
            $lines[$i] = ($lines[$i] -replace '^#\[proxy-off\]\s*', '')
            $restored = $true
            Write-Ok ('已还原: ' + $lines[$i].Trim())
        }
    }
    if (-not $restored) {
        # 2) 在 Host <HostName> 块内插入 / 追加
        $start = -1
        for ($i = 0; $i -lt $lines.Count; $i++) {
            if ($lines[$i] -match ('^\s*Host\s+.*\b' + [regex]::Escape($HostName) + '\b')) { $start = $i; break }
        }
        if ($start -ge 0) {
            $end = $lines.Count
            for ($i = $start + 1; $i -lt $lines.Count; $i++) { if ($lines[$i] -match '^\s*Host\s') { $end = $i; break } }
            $insertAt = $end
            for ($i = $start; $i -lt $end; $i++) { if ($lines[$i] -match '^\s*(HostName|Port)\b') { $insertAt = $i + 1 } }
            $lines.Insert($insertAt, $proxyLine)
            Write-Ok ('已在 Host ' + $HostName + ' 块内插入代理行')
        } else {
            $lines.Add('')
            $lines.Add('Host ' + $HostName)
            $lines.Add('    Hostname ' + $RealHost)
            $lines.Add('    Port ' + $RealPort)
            $lines.Add($proxyLine)
            Write-Ok ('已新建 Host ' + $HostName + ' 块')
        }
    }
    [System.IO.File]::WriteAllText($cfgPath, (($lines -join "`r`n").TrimEnd() + "`r`n"), (New-Object System.Text.UTF8Encoding($false)))
} else {
    $content = @(
        'Host ' + $HostName,
        '    Hostname ' + $RealHost,
        '    Port ' + $RealPort,
        $proxyLine
    ) -join "`r`n"
    [System.IO.File]::WriteAllText($cfgPath, $content + "`r`n", (New-Object System.Text.UTF8Encoding($false)))
    Write-Ok "已创建 $cfgPath"
}

Write-Host ''
Write-Host '---------------- 生效后的 config ----------------' -ForegroundColor Cyan
Get-Content $cfgPath | ForEach-Object { Write-Host ('  ' + $_) }

Write-Host ''
Write-Info ('检查代理端口 127.0.0.1:{0} 是否在监听（本工具不启动任何代理客户端）' -f $Port)
$c = New-Object System.Net.Sockets.TcpClient
$portOk = $false
try { $portOk = $c.ConnectAsync('127.0.0.1', $Port).Wait(600) } catch { } finally { $c.Close() }
if ($portOk) { Write-Ok ('端口 {0} 在监听，代理可用' -f $Port) }
else { Write-Warn ('端口 {0} 没有监听：请自行启动你的代理客户端，否则 git 会报 Connection closed by UNKNOWN port 65535' -f $Port) }

$connect = Get-Command connect -ErrorAction SilentlyContinue
if (-not $connect) {
    $g = 'D:\Git\mingw64\bin\connect.exe'
    if (Test-Path $g) { Write-Ok "connect 助手存在: $g" } else { Write-Warn '没找到 connect 助手，ProxyCommand 会失败（Git for Windows 通常自带）' }
}

if ($portOk) {
    Write-Host ''
    Write-Info '验证: ssh -T git@github.com'
    $env:GIT_TERMINAL_PROMPT = '0'
    $out = & ssh -o ConnectTimeout=10 -o BatchMode=yes -T git@github.com 2>&1 | Out-String
    if ($out -match 'successfully authenticated') { Write-Ok 'SSH 到 GitHub 正常' }
    else { Write-Warn 'SSH 未通过：'; Write-Host ($out.Trim()) }
}
Write-Host ''
Write-Host '取消命令: tools\ssh_proxy_off.bat' -ForegroundColor Green
