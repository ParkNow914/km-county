@echo off
echo.
echo ==============================================
echo  Instalacao SEM Privilegios de Administrador
echo ==============================================
echo.

set USER_DIR=%USERPROFILE%
set ANDROID_DIR=%USER_DIR%\Android
set SDK_DIR=%ANDROID_DIR%\Sdk

echo [INFO] Instalando Android SDK Tools sem privilegios de admin...
echo.
echo Opcoes disponiveis:
echo.
echo 1. SDK Tools apenas (143 MB) - RAPIDO
echo 2. Android Studio ZIP (1.4 GB) - COMPLETO
echo 3. Tentar winget sem admin
echo.

choice /C 123 /M "Escolha uma opcao"
if errorlevel 3 goto :winget_user
if errorlevel 2 goto :studio_zip
if errorlevel 1 goto :sdk_tools

:sdk_tools
echo.
echo [1/4] Criando estrutura de pastas...
mkdir "%SDK_DIR%\cmdline-tools\latest" 2>nul
mkdir "%SDK_DIR%\platforms" 2>nul
mkdir "%SDK_DIR%\build-tools" 2>nul
mkdir "%SDK_DIR%\platform-tools" 2>nul

echo [2/4] Baixando SDK Tools...
echo URL: https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip
echo.
echo Opcoes de download:
echo A. Automatico (PowerShell)
echo B. Manual (abrir navegador)
echo.

choice /C AB /M "Como deseja baixar"
if errorlevel 2 goto :manual_sdk
if errorlevel 1 goto :auto_sdk

:auto_sdk
echo [INFO] Baixando automaticamente...
powershell -Command "Invoke-WebRequest -Uri 'https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip' -OutFile '%TEMP%\cmdline-tools.zip'"
if %errorlevel% neq 0 goto :manual_sdk

echo [3/4] Extraindo arquivos...
powershell -Command "Expand-Archive -Path '%TEMP%\cmdline-tools.zip' -DestinationPath '%SDK_DIR%\cmdline-tools\latest' -Force"
goto :configure_sdk

:manual_sdk
echo [INFO] Abrindo pagina de download...
start "" "https://developer.android.com/studio#command-tools"
echo.
echo INSTRUCOES MANUAIS:
echo 1. Baixe: commandlinetools-win-13114758_latest.zip
echo 2. Extraia para: %SDK_DIR%\cmdline-tools\latest
echo 3. Pressione qualquer tecla quando terminar
pause
goto :configure_sdk

:studio_zip
echo.
echo [INFO] Android Studio ZIP (versao completa)
echo.
echo [1/3] Criando pasta do Android Studio...
mkdir "%USER_DIR%\AndroidStudio" 2>nul

echo [2/3] Baixando Android Studio ZIP...
start "" "https://developer.android.com/studio"
echo.
echo INSTRUCOES MANUAIS:
echo 1. Na pagina que abriu, baixe: android-studio-xxx-windows.ZIP
echo 2. Extraia para: %USER_DIR%\AndroidStudio
echo 3. Execute: %USER_DIR%\AndroidStudio\bin\studio64.exe
echo 4. Siga o setup wizard
echo 5. Pressione qualquer tecla quando terminar
pause

set ANDROID_HOME=%USER_DIR%\AndroidStudio\sdk
goto :set_variables

:winget_user
echo.
echo [INFO] Tentando winget sem privilegios de admin...
winget install --id Google.AndroidStudio --scope user --accept-package-agreements --accept-source-agreements
if %errorlevel% equ 0 (
    echo [SUCESSO] Instalacao via winget bem-sucedida!
    goto :find_installation
) else (
    echo [ERRO] Winget falhou. Tentando opcoes alternativas...
    goto :sdk_tools
)

:configure_sdk
echo [4/4] Configurando variaveis de ambiente...
set ANDROID_HOME=%SDK_DIR%
goto :set_variables

:find_installation
echo [INFO] Procurando instalacao do Android Studio...
for /d %%i in ("%LOCALAPPDATA%\Programs\Android\Android Studio*") do (
    set ANDROID_HOME=%%i\sdk
    goto :set_variables
)
set ANDROID_HOME=%USER_DIR%\AppData\Local\Android\Sdk

:set_variables
echo.
echo [INFO] Configurando ANDROID_HOME para esta sessao...
echo ANDROID_HOME=%ANDROID_HOME%

REM Configurar para sessao atual
set PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin

echo.
echo [INFO] Testando configuracao...
if exist "%ANDROID_HOME%" (
    echo [OK] Pasta ANDROID_HOME encontrada: %ANDROID_HOME%
) else (
    echo [AVISO] Pasta ANDROID_HOME nao encontrada
)

echo.
echo [INFO] Para tornar permanente, adicione ao seu PATH:
echo.
echo 1. Pressione Win+R, digite: rundll32.exe sysdm.cpl,EditEnvironmentVariables
echo 2. Em "Variaveis do usuario", clique "Nova"
echo 3. Nome: ANDROID_HOME
echo 4. Valor: %ANDROID_HOME%
echo 5. Edite "Path" e adicione:
echo    %%ANDROID_HOME%%\tools
echo    %%ANDROID_HOME%%\platform-tools
echo    %%ANDROID_HOME%%\cmdline-tools\latest\bin
echo.

echo [INFO] Testando nosso projeto...
echo.
cd /d "%~dp0\.."
call scripts\setup-dev.bat

echo.
echo ==============================================
echo  INSTALACAO CONCLUIDA!
echo ==============================================
echo.
echo ANDROID_HOME configurado para: %ANDROID_HOME%
echo.
echo Para usar em outras sessoes:
echo 1. Configure as variaveis de ambiente (instrucoes acima)
echo 2. Ou execute este script novamente
echo.
pause
