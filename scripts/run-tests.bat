@echo off
echo.
echo ==========================================
echo  Assistente R$/km - Execucao de Testes
echo ==========================================
echo.

REM Verificar se estamos no diretorio correto
if not exist "settings.gradle.kts" (
    echo [ERRO] Execute este script da pasta raiz do projeto!
    pause
    exit /b 1
)

cd /d "%~dp0\.."

echo [INFO] Limpando builds anteriores...
call gradlew.bat clean

echo.
echo [INFO] Executando ktlint...
call gradlew.bat ktlintCheck
if %errorlevel% neq 0 (
    echo [ERRO] ktlint falhou! Corrija a formatacao primeiro.
    echo Execute: gradlew ktlintFormat
    pause
    exit /b 1
)

echo.
echo [INFO] Executando detekt...
call gradlew.bat detekt
if %errorlevel% neq 0 (
    echo [ERRO] detekt falhou! Corrija os problemas de qualidade.
    pause
    exit /b 1
)

echo.
echo [INFO] Executando testes unitarios...
call gradlew.bat test --stacktrace
if %errorlevel% neq 0 (
    echo [ERRO] Testes unitarios falharam!
    echo Verifique: app\build\reports\tests\testDebugUnitTest\index.html
    pause
    exit /b 1
)

echo.
echo [INFO] Verificando dispositivos conectados...
adb devices | find "device" >nul
if %errorlevel% equ 0 (
    echo [INFO] Dispositivo encontrado! Executando testes instrumentados...
    call gradlew.bat connectedAndroidTest --stacktrace
    if %errorlevel% neq 0 (
        echo [AVISO] Testes instrumentados falharam!
        echo Verifique: app\build\reports\androidTests\connected\index.html
    )
) else (
    echo [AVISO] Nenhum dispositivo Android conectado.
    echo Conecte um dispositivo ou inicie um emulador para testes instrumentados.
)

echo.
echo [INFO] Executando lint do Android...
call gradlew.bat lint
if %errorlevel% neq 0 (
    echo [AVISO] Android lint encontrou problemas!
    echo Verifique: app\build\reports\lint-results-debug.html
)

echo.
echo ==========================================
echo  Resumo dos Testes
echo ==========================================
echo.
echo Relatorios disponiveis:
echo - Testes unitarios: app\build\reports\tests\testDebugUnitTest\index.html
echo - Testes instrumentados: app\build\reports\androidTests\connected\index.html  
echo - ktlint: build\reports\ktlint\ktlintMainSourceSetCheck.html
echo - detekt: build\reports\detekt\detekt.html
echo - Android lint: app\build\reports\lint-results-debug.html
echo.
echo [SUCESSO] Todos os testes obrigatorios passaram!
echo O projeto esta pronto para build de release.
echo.
pause
