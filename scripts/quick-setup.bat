@echo off
echo.
echo ==============================================
echo  Setup Rapido - Assistente R$/km
echo ==============================================
echo.

cd /d "%~dp0\.."

echo [1/3] Corrigindo Gradle Wrapper...
call scripts\fix-gradle.bat
if %errorlevel% neq 0 (
    echo [ERRO] Falha na configuracao do Gradle
    pause
    exit /b 1
)

echo.
echo [2/3] Configurando ANDROID_HOME...
call scripts\setup-android-home.bat

echo.
echo [3/3] Testando configuracao...
if "%ANDROID_HOME%"=="" (
    echo [AVISO] ANDROID_HOME ainda nao configurado
    echo Configure manualmente e execute: scripts\setup-dev.bat
) else (
    echo [INFO] Tentando build de teste...
    call gradlew.bat tasks --console=plain
    if %errorlevel% equ 0 (
        echo.
        echo [SUCESSO] Configuracao completa!
        echo Agora execute: scripts\setup-dev.bat
    ) else (
        echo.
        echo [INFO] Configuracao basica ok, mas pode precisar de ajustes
        echo Execute: scripts\setup-dev.bat para continuar
    )
)

echo.
pause
