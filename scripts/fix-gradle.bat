@echo off
echo.
echo ==============================================
echo  Corrigindo Gradle Wrapper
echo ==============================================
echo.

cd /d "%~dp0\.."

echo [INFO] Baixando Gradle Wrapper...
echo Isso pode demorar alguns minutos...

REM Baixar gradle-wrapper.jar
curl -L -o "gradle\wrapper\gradle-wrapper.jar" "https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar"

if %errorlevel% neq 0 (
    echo [ERRO] Falha ao baixar gradle-wrapper.jar
    echo Tentando metodo alternativo...
    
    REM Tentar com PowerShell
    powershell -Command "Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.4.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
    
    if %errorlevel% neq 0 (
        echo [ERRO] Nao foi possivel baixar o Gradle Wrapper
        echo.
        echo Opcoes manuais:
        echo 1. Baixe manualmente de: https://services.gradle.org/distributions/gradle-8.4-wrapper.jar
        echo 2. Salve como: gradle\wrapper\gradle-wrapper.jar
        echo 3. Ou instale o Android Studio que ja vem com Gradle
        pause
        exit /b 1
    )
)

echo [SUCESSO] Gradle Wrapper corrigido!
echo.
echo Agora configure o ANDROID_HOME:
echo.
echo 1. Localize sua instalacao do Android SDK
echo    Exemplos comuns:
echo    - C:\Users\%USERNAME%\AppData\Local\Android\Sdk
echo    - C:\Android\Sdk
echo    - C:\Program Files\Android\Sdk
echo.
echo 2. Adicione as variaveis de ambiente:
echo    ANDROID_HOME = caminho_do_seu_sdk
echo    Path += %%ANDROID_HOME%%\tools
echo    Path += %%ANDROID_HOME%%\platform-tools
echo.
echo 3. Reinicie o prompt de comando
echo.
echo 4. Execute novamente: scripts\setup-dev.bat
echo.
pause
