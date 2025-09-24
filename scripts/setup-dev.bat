@echo off
echo.
echo =====================================================
echo  Assistente R$/km - Configuracao de Desenvolvimento
echo =====================================================
echo.

REM Verificar se estamos no diretorio correto
if not exist "settings.gradle.kts" (
    echo [ERRO] Nao estamos no diretorio raiz do projeto!
    echo Execute este script a partir da pasta raiz do projeto.
    pause
    exit /b 1
)

REM Verificar Java
echo [INFO] Verificando instalacao do Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Java nao encontrado!
    echo Instale o JDK 11+ antes de continuar.
    pause
    exit /b 1
)

REM Verificar Android SDK
echo [INFO] Verificando Android SDK...
if "%ANDROID_HOME%"=="" (
    echo [AVISO] ANDROID_HOME nao configurado!
    echo Configure a variavel de ambiente ANDROID_HOME.
    echo Exemplo: C:\Users\%USERNAME%\AppData\Local\Android\Sdk
    pause
)

REM Verificar Git
echo [INFO] Verificando Git...
git --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Git nao encontrado!
    echo Instale o Git antes de continuar.
    pause
    exit /b 1
)

REM Limpar builds anteriores
echo [INFO] Limpando builds anteriores...
if exist "build" rmdir /s /q build
if exist "app\build" rmdir /s /q app\build
if exist "core\build" rmdir /s /q core\build
if exist "ml\build" rmdir /s /q ml\build

REM Executar build inicial
echo [INFO] Executando build inicial...
echo Isso pode demorar alguns minutos na primeira vez...
call gradlew.bat build --stacktrace
if %errorlevel% neq 0 (
    echo [ERRO] Build inicial falhou!
    echo Verifique os logs acima para mais detalhes.
    pause
    exit /b 1
)

REM Executar testes
echo [INFO] Executando testes...
call gradlew.bat test
if %errorlevel% neq 0 (
    echo [AVISO] Alguns testes falharam!
    echo Verifique os relatorios em build/reports/tests/
)

REM Verificar lint
echo [INFO] Executando verificacoes de codigo...
call gradlew.bat ktlintCheck
if %errorlevel% neq 0 (
    echo [AVISO] Problemas de formatacao encontrados!
    echo Execute: gradlew ktlintFormat
)

echo.
echo =====================================================
echo  Configuracao concluida!
echo =====================================================
echo.
echo Proximos passos:
echo 1. Abra o projeto no Android Studio
echo 2. Conecte um dispositivo Android ou inicie um emulador
echo 3. Execute o app em modo debug
echo 4. Siga o onboarding para configurar permissoes
echo.
echo Scripts uteis:
echo - scripts\run-tests.bat        : Executar todos os testes
echo - scripts\build-release.bat    : Build de release
echo - gradlew ktlintFormat         : Corrigir formatacao
echo - gradlew assembleDebug        : Build debug rapido
echo.
echo Para suporte, visite: https://github.com/[repo]/issues
echo.
pause
