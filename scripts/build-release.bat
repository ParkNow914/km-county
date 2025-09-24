@echo off
echo.
echo ============================================
echo  Assistente R$/km - Build de Release
echo ============================================
echo.

REM Verificar se estamos no diretorio correto
if not exist "settings.gradle.kts" (
    echo [ERRO] Execute este script da pasta raiz do projeto!
    pause
    exit /b 1
)

cd /d "%~dp0\.."

echo [INFO] Verificando pre-requisitos...

REM Verificar Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Java nao encontrado!
    pause
    exit /b 1
)

REM Verificar Android SDK
if "%ANDROID_HOME%"=="" (
    echo [ERRO] ANDROID_HOME nao configurado!
    pause
    exit /b 1
)

echo [INFO] Limpando builds anteriores...
call gradlew.bat clean

echo.
echo [INFO] Executando verificacoes de qualidade...
call gradlew.bat ktlintCheck detekt
if %errorlevel% neq 0 (
    echo [ERRO] Verificacoes de qualidade falharam!
    echo Execute scripts\run-tests.bat primeiro
    pause
    exit /b 1
)

echo.
echo [INFO] Executando testes...
call gradlew.bat test
if %errorlevel% neq 0 (
    echo [ERRO] Testes falharam!
    echo Corrija os testes antes do build de release
    pause
    exit /b 1
)

echo.
echo [INFO] Buildando APK de release...
if exist "keystore.jks" (
    echo [INFO] Keystore encontrado - build assinado
    call gradlew.bat assembleRelease
) else (
    echo [AVISO] Keystore nao encontrado - build com debug key
    call gradlew.bat assembleRelease
)

if %errorlevel% neq 0 (
    echo [ERRO] Build do APK falhou!
    pause
    exit /b 1
)

echo.
echo [INFO] Buildando AAB de release...
call gradlew.bat bundleRelease
if %errorlevel% neq 0 (
    echo [ERRO] Build do AAB falhou!
    pause
    exit /b 1
)

REM Extrair informacoes de versao
for /f "tokens=2 delims==" %%i in ('findstr "versionName" app\build.gradle.kts') do set VERSION_NAME=%%i
set VERSION_NAME=%VERSION_NAME:"=%
set VERSION_NAME=%VERSION_NAME: =%

echo.
echo [INFO] Criando diretorio de release...
set RELEASE_DIR=release\v%VERSION_NAME%
if not exist "%RELEASE_DIR%" mkdir "%RELEASE_DIR%"

echo.
echo [INFO] Copiando arquivos de release...
copy "app\build\outputs\apk\release\app-release.apk" "%RELEASE_DIR%\assistente-ridepricing-v%VERSION_NAME%.apk" >nul
copy "app\build\outputs\bundle\release\app-release.aab" "%RELEASE_DIR%\assistente-ridepricing-v%VERSION_NAME%.aab" >nul

echo.
echo [INFO] Gerando checksums...
cd "%RELEASE_DIR%"
certutil -hashfile "assistente-ridepricing-v%VERSION_NAME%.apk" SHA256 > checksums.txt
certutil -hashfile "assistente-ridepricing-v%VERSION_NAME%.aab" SHA256 >> checksums.txt
cd ..\..

echo.
echo [INFO] Gerando informacoes de build...
echo Assistente R$/km - Build Information > "%RELEASE_DIR%\build-info.txt"
echo ===================================== >> "%RELEASE_DIR%\build-info.txt"
echo. >> "%RELEASE_DIR%\build-info.txt"
echo Versao: %VERSION_NAME% >> "%RELEASE_DIR%\build-info.txt"
echo Data do Build: %date% %time% >> "%RELEASE_DIR%\build-info.txt"
echo Sistema: %OS% >> "%RELEASE_DIR%\build-info.txt"
echo. >> "%RELEASE_DIR%\build-info.txt"
echo Arquivos: >> "%RELEASE_DIR%\build-info.txt"
echo - assistente-ridepricing-v%VERSION_NAME%.apk >> "%RELEASE_DIR%\build-info.txt"
echo - assistente-ridepricing-v%VERSION_NAME%.aab >> "%RELEASE_DIR%\build-info.txt"
echo - checksums.txt (SHA256) >> "%RELEASE_DIR%\build-info.txt"
echo. >> "%RELEASE_DIR%\build-info.txt"
echo Instalacao: >> "%RELEASE_DIR%\build-info.txt"
echo 1. Ative "Origens desconhecidas" no Android >> "%RELEASE_DIR%\build-info.txt"
echo 2. Instale o arquivo APK >> "%RELEASE_DIR%\build-info.txt"
echo 3. Siga o onboarding para configurar permissoes >> "%RELEASE_DIR%\build-info.txt"
echo 4. Teste com um app de transporte >> "%RELEASE_DIR%\build-info.txt"

echo.
echo ============================================
echo  BUILD CONCLUIDO COM SUCESSO!
echo ============================================
echo.
echo Arquivos de release criados em: %RELEASE_DIR%
echo.
echo Arquivos:
echo  📱 APK: assistente-ridepricing-v%VERSION_NAME%.apk
echo  📦 AAB: assistente-ridepricing-v%VERSION_NAME%.aab  
echo  🔐 Checksums: checksums.txt
echo  📄 Info: build-info.txt
echo.
echo Proximos passos:
echo 1. Teste o APK em um dispositivo real
echo 2. Verifique se todas as funcionalidades funcionam
echo 3. Crie uma release no GitHub
echo 4. Faça upload dos arquivos para a release
echo.
echo Para suporte: https://github.com/[repo]/issues
echo.
pause
