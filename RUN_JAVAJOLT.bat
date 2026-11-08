@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java Development Kit not found. Install JDK 21 or newer and check PATH.
    pause
    exit /b 1
)

if not exist out mkdir out
echo Compiling JavaJolt...
javac -encoding UTF-8 -d out -sourcepath src src\edu\batstateu\javajolt\GameApplication.java
if errorlevel 1 (
    echo.
    echo Compilation failed. See errors above.
    pause
    exit /b 1
)

echo Starting JavaJolt...
java -cp out edu.batstateu.javajolt.GameApplication %*
if errorlevel 1 (
    echo.
    echo JavaJolt exited with an error. See the message above.
    pause
    exit /b 1
)
exit /b 0
