@echo off
rem Cancel SSH proxy in %USERPROFILE%\.ssh\config (comments out local ProxyCommand lines)
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0ssh_proxy_off.ps1" %*
echo.
echo %cmdcmdline% | find /i "%~f0" >nul && pause
endlocal
