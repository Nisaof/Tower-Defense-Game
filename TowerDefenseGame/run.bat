@echo off
echo Starting Tower Defense Game...
echo.

if not exist "bin" (
    echo Error: Game not compiled yet!
    echo Please run compile.bat first.
    pause
    exit /b 1
)

java -cp bin TowerDefenseGame

pause





