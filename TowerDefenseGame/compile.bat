@echo off
echo Compiling Tower Defense Game...

if not exist "bin" mkdir bin

javac -d bin -encoding UTF-8 ^
    TowerDefenseGame.java ^
    AuthSystem.java ^
    AssetManager.java ^
    MainMenuPanel.java ^
    LoginPanel.java ^
    LevelSelectPanel.java ^
    GamePanel.java ^
    HighScoresPanel.java ^
    Tower.java ^
    Enemy.java ^
    Projectile.java ^
    LevelData.java ^
    org/json/JSONObject.java ^
    org/json/JSONArray.java

if %errorlevel% equ 0 (
    echo.
    echo Compilation successful!
    echo Run the game with: run.bat
) else (
    echo.
    echo Compilation failed!
)

pause

