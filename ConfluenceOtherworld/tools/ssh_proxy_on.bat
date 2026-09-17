@echo off
rem Set/restore SSH proxy in %USERPROFILE%\.ssh\config (default port 7897)
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0ssh_proxy_on.ps1" %*
echo.
echo %cmdcmdline% | find /i "%~f0" >nul && pause
endlocal
