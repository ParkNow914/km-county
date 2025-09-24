@echo off
echo.
echo ==============================================
echo  Instalacao do Android Studio
echo ==============================================
echo.

echo [INFO] Este script ira abrir o navegador para download do Android Studio
echo.
echo O que sera instalado:
echo - Android Studio (IDE completa)
echo - Android SDK (ferramentas de desenvolvimento)
echo - Gradle (sistema de build)
echo - Emulador Android (opcional)
echo.

choice /C SN /M "Deseja continuar com a instalacao"
if errorlevel 2 goto :end
if errorlevel 1 goto :install

:install
echo [INFO] Abrindo pagina de download...
start "" "https://developer.android.com/studio"

echo.
echo ==========================================
echo  INSTRUCOES DE INSTALACAO
echo ==========================================
echo.
echo 1. DOWNLOAD:
echo    - Clique em "Download Android Studio"
echo    - Aceite os termos de servico
echo    - Salve o arquivo (android-studio-xxx-windows.exe)
echo.
echo 2. INSTALACAO:
echo    - Execute o arquivo baixado como Administrador
echo    - Siga o wizard de instalacao
echo    - IMPORTANTE: Mantenha marcado "Android SDK"
echo    - Escolha pasta de instalacao (padrao ok)
echo.
echo 3. PRIMEIRA EXECUCAO:
echo    - Abra Android Studio
echo    - Siga o "Setup Wizard"
echo    - Escolha "Standard" installation
echo    - Aguarde download dos componentes (pode demorar)
echo.
echo 4. VERIFICAR INSTALACAO:
echo    - No Android Studio, va em File ^> Settings
echo    - Appearance ^& Behavior ^> System Settings ^> Android SDK
echo    - Anote o caminho do "Android SDK Location"
echo    - Exemplo: C:\Users\%USERNAME%\AppData\Local\Android\Sdk
echo.
echo 5. CONFIGURAR VARIAVEIS:
echo    - Pressione Win+R, digite "sysdm.cpl"
echo    - Aba "Avancado" ^> "Variaveis de Ambiente"
echo    - Em "Variaveis do sistema", clique "Nova"
echo    - Nome: ANDROID_HOME
echo    - Valor: caminho_do_SDK_anotado_acima
echo    - Edite a variavel "Path" e adicione:
echo      %%ANDROID_HOME%%\tools
echo      %%ANDROID_HOME%%\platform-tools
echo.
echo 6. TESTAR:
echo    - Reinicie o prompt de comando
echo    - Execute: scripts\setup-dev.bat
echo.

echo =========================================
echo  ALTERNATIVA RAPIDA (SEM ANDROID STUDIO)
echo =========================================
echo.
echo Se voce NAO quer instalar o Android Studio completo:
echo.
echo 1. Baixe apenas o SDK:
echo    https://developer.android.com/studio#command-tools
echo.
echo 2. Extraia para: C:\Android\Sdk
echo.
echo 3. Configure ANDROID_HOME = C:\Android\Sdk
echo.
echo Porem, recomendamos o Android Studio para desenvolvimento completo.
echo.

:end
pause
