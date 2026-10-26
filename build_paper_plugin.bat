@echo off
setlocal EnableExtensions
set "ROOT=%~dp0"
set "NO_PAUSE=0"
if /i "%~1"=="--no-pause" set "NO_PAUSE=1"
set "JAVA_HOME25="

rem Use validated JDK 25
if defined JAVA_HOME call :tryJdk "%JAVA_HOME%"
if defined JAVA_HOME_25_X64 call :tryJdk "%JAVA_HOME_25_X64%"
for /d %%J in ("%ProgramFiles%\Eclipse Adoptium\jdk-25*" "%ProgramFiles%\Microsoft\jdk-25*" "%ProgramFiles%\Java\jdk-25*" "%USERPROFILE%\.jdks\*25*") do call :tryJdk "%%~fJ"
if not defined JAVA_HOME25 (
    echo ERROR: JDK 25 was not found. Set JAVA_HOME to a JDK 25 installation.
    goto failed
)
set "JAVA_HOME=%JAVA_HOME25%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

where mvn >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven was not found on PATH. Install Maven to build CoroUtil Paper.
    goto failed
)

echo Using JDK: %JAVA_HOME%
echo.
echo Building CoroUtil Paper / Purpur Server Plugin and Common Library (Maven)...
call mvn -f "%ROOT%pom.xml" clean package
if errorlevel 1 goto failed

echo.
echo ==============================================================================
echo Build succeeded!
echo The shaded Paper/Purpur plugin JAR is located in:
echo %ROOT%jar
echo ==============================================================================
if "%NO_PAUSE%"=="0" pause
exit /b 0

:failed
echo.
echo Build failed.
if "%NO_PAUSE%"=="0" pause
exit /b 1

:tryJdk
if defined JAVA_HOME25 exit /b 0
if not exist "%~1\bin\javac.exe" exit /b 0
for /f "tokens=2" %%V in ('"%~1\bin\javac.exe" -version 2^>^&1') do for /f "tokens=1 delims=.-" %%M in ("%%V") do if "%%M"=="25" set "JAVA_HOME25=%~1"
exit /b 0
