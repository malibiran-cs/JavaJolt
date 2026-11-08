@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>&1
if errorlevel 1 (
    echo ERROR: JDK 21 or newer is required.
    pause
    exit /b 1
)

if not exist out mkdir out
echo Compiling JavaJolt and its tests...
javac -encoding UTF-8 -d out -sourcepath src src\edu\batstateu\javajolt\GameApplication.java src\edu\batstateu\javajolt\preview\MechanicsCheck.java tests\ProgressConsistencyCheck.java tests\ContentCoverageCheck.java tests\SwingTimerFlowCheck.java tests\SwingNavigationSmokeCheck.java
if errorlevel 1 goto failed

echo.
echo ==== Gameplay and saving ====
java -cp out ProgressConsistencyCheck
if errorlevel 1 goto failed

echo.
echo ==== Question bank and lessons ====
java -cp out ContentCoverageCheck
if errorlevel 1 goto failed

echo.
echo ==== Game mechanics ====
java -cp out edu.batstateu.javajolt.preview.MechanicsCheck
if errorlevel 1 goto failed

echo.
echo ==== Swing navigation ====
java -cp out SwingNavigationSmokeCheck
if errorlevel 1 goto failed

echo.
echo ==== Swing timer, power-ups, and feedback ====
java -cp out SwingTimerFlowCheck
if errorlevel 1 goto failed

echo.
echo ALL JAVAJOLT TEST SUITES PASSED
pause
exit /b 0

:failed
echo.
echo A test or compilation failed. Please copy the error output.
pause
exit /b 1
