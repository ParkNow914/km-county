@echo off
echo.
echo ==============================================
echo  Configuracao do ANDROID_HOME
echo ==============================================
echo.

echo [INFO] Verificando localizacoes comuns do Android SDK...
echo.

set FOUND_SDK=

REM Verificar localizacoes comuns diretamente
call :check_path "%USERPROFILE%\AppData\Local\Android\Sdk"
if "%FOUND_SDK%" neq "" goto :sdk_found

call :check_path "%LOCALAPPDATA%\Android\Sdk"
if "%FOUND_SDK%" neq "" goto :sdk_found

call :check_path "C:\Android\Sdk"
if "%FOUND_SDK%" neq "" goto :sdk_found

call :check_path "C:\Program Files\Android\Sdk"
if "%FOUND_SDK%" neq "" goto :sdk_found

call :check_path "C:\Program Files (x86)\Android\Sdk"
if "%FOUND_SDK%" neq "" goto :sdk_found

:sdk_found

if "%FOUND_SDK%"=="" (
    echo [AVISO] Android SDK nao encontrado automaticamente
    echo.
    echo Opcoes:
    echo.
    echo 1. INSTALAR ANDROID STUDIO
    echo    - Baixe de: https://developer.android.com/studio
    echo    - Instale normalmente
    echo    - O SDK sera instalado automaticamente
    echo.
    echo 2. INSTALAR APENAS O SDK
    echo    - Baixe de: https://developer.android.com/studio#command-tools
    echo    - Extraia para: C:\Android\Sdk
    echo.
    echo 3. SE JA TEM O SDK
    echo    - Localize a pasta do SDK
    echo    - Execute: set ANDROID_HOME=caminho_completo
    echo    - Adicione ao PATH do sistema
    echo.
    goto :manual_config
)

echo.
echo [SUCESSO] Android SDK encontrado em: %FOUND_SDK%
echo.
echo Configurando ANDROID_HOME automaticamente...

REM Configurar ANDROID_HOME para a sessao atual
set ANDROID_HOME=%FOUND_SDK%
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%

echo [INFO] ANDROID_HOME configurado para esta sessao: %ANDROID_HOME%
echo.
echo Para tornar permanente, execute como Administrador:
echo.
echo setx ANDROID_HOME "%ANDROID_HOME%" /M
echo setx PATH "%%PATH%%;%%ANDROID_HOME%%\tools;%%ANDROID_HOME%%\platform-tools" /M
echo.

:test_config
echo [INFO] Testando configuracao...
echo.

if exist "%ANDROID_HOME%\tools" (
    echo [OK] Pasta tools encontrada
) else (
    echo [AVISO] Pasta tools nao encontrada
)

if exist "%ANDROID_HOME%\platform-tools" (
    echo [OK] Pasta platform-tools encontrada
) else (
    echo [AVISO] Pasta platform-tools nao encontrada
)

if exist "%ANDROID_HOME%\platforms" (
    echo [OK] Pasta platforms encontrada
) else (
    echo [AVISO] Pasta platforms nao encontrada - instale pelo menos uma versao do Android
)

echo.
echo Agora tente executar novamente: scripts\setup-dev.bat
echo.
goto :end

:manual_config
echo Para configurar manualmente:
echo.
echo 1. Abra "Variaveis de Ambiente" no Windows
echo 2. Adicione nova variavel de sistema:
echo    Nome: ANDROID_HOME
echo    Valor: caminho_completo_do_seu_SDK
echo.
echo 3. Edite a variavel PATH e adicione:
echo    %%ANDROID_HOME%%\tools
echo    %%ANDROID_HOME%%\platform-tools
echo.
echo 4. Reinicie o prompt de comando
echo.

:end
pause
exit /b 0

:check_path
if exist %1 (
    echo [ENCONTRADO] %1
    set FOUND_SDK=%~1
    exit /b 0
) else (
    echo [NAO ENCONTRADO] %1
    exit /b 1
)
