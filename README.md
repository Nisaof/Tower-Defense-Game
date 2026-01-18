# 🎮 Tower Defense Game

![Tower Defense Game Banner](screenshots/banner.png)

A Java-based Tower Defense game developed as a term project for **CSE212 - Software Development Methodologies** at Yeditepe University.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)

## 📋 Project Overview

This project implements a complete tower defense game featuring user authentication, multiple difficulty levels, dynamic enemy waves, and a comprehensive scoring system. Built entirely with Java Swing for the GUI, it demonstrates object-oriented programming principles and software development best practices.

## 📸 Screenshots

### Main Menu
<img width="1474" height="863" alt="Ekran görüntüsü 2026-01-19 002625" src="https://github.com/user-attachments/assets/b8072f4f-00d6-4ab8-a491-6321c9e27890" />


### Level Selection
<img width="1475" height="860" alt="Ekran görüntüsü 2026-01-19 002648" src="https://github.com/user-attachments/assets/4381474f-315a-4306-a253-9db1152ee4fd" />


### Gameplay - Level 1-2
<img width="1471" height="853" alt="Ekran görüntüsü 2026-01-19 004010" src="https://github.com/user-attachments/assets/384c7c70-843f-4e03-9326-b651389e44e8" />


### Gameplay - Level 3
<img width="1477" height="867" alt="Ekran görüntüsü 2026-01-19 004115" src="https://github.com/user-attachments/assets/62a7faa2-e6c5-4508-949e-0e1b3d2a4b28" />


**Course:** CSE212 - Software Development Methodologies  
**Institution:** Yeditepe University  
**Semester:** Fall 2025  
**Student:** Nisa Of

## ✨ Features

### Core Gameplay
- **3 Unique Levels** - Each with distinct visual designs and increasing difficulty
- **Multiple Tower Types** - 4 different tower classes with unique attributes (range, damage, fire rate)
- **Dynamic Difficulty** - Enemy speed and spawn count increase progressively within levels
- **Strategic Placement** - Mouse-controlled tower building system
- **Tower Management** - Sell towers for 50% refund to adjust strategy

### Game Systems
- **Authentication System** - User registration and login with password protection
- **Score Tracking** - Persistent high score system stored in JSON format
- **User Data Management** - Player profiles saved in text files
- **Pause/Resume** - Full game state control during gameplay
- **Health System** - Base health decreases when enemies escape

### User Interface
- Clean main menu with level selection
- In-game HUD showing:
  - Available funds
  - Tower health percentage
  - Tower selection bar with costs
  - Pause/Leave controls
- Confirmation dialogs for critical actions

## 🎯 Scoring System

The final score is calculated using:

```
Final Score = (Enemies Defeated × 20) + Remaining Health + Money Spent
```

- **Enemies Defeated**: +20 points per enemy
- **Remaining Health**: Percentage of base health at level end
- **Money Spent**: Total currency invested in towers

## 🛠️ Technical Details

### Requirements
- **Java JDK 8** or higher
- **Operating System**: Windows (batch files provided) or Linux/Mac (manual compilation)

### Project Structure
```
TowerDefenseGame/
├── TowerDefenseGame.java    # Main application entry point
├── GamePanel.java           # Core game logic and rendering
├── AssetManager.java        # Asset loading and management
├── AuthSystem.java          # User authentication
├── Tower.java               # Tower entity implementation
├── Enemy.java               # Enemy entity implementation
├── EnemyProjectile.java     # Projectile system
├── Projectile.java          # Tower projectile system
├── LevelData.java           # Level configuration data
├── MainMenuPanel.java       # Main menu UI
├── LoginPanel.java          # Login/registration UI
├── LevelSelectPanel.java    # Level selection UI
├── HighScoresPanel.java     # High scores display
├── compile.bat              # Windows compilation script
├── run.bat                  # Windows run script
└── org/json/                # JSON library for data persistence
```

## 🚀 Installation & Setup

### Option 1: Windows (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Tower-Defense-Game.git
   cd Tower-Defense-Game/TowerDefenseGame
   ```

2. **Ensure assets are in place**
   - The `PNG.zip` folder should be in the parent directory
   - Structure should be:
     ```
     Tower-Defense-Game/
     ├──TowerDefenseGame/          # Your code
     └── PNG/  # Assets
     ```

3. **Compile the game**
   ```bash
   compile.bat
   ```

4. **Run the game**
   ```bash
   run.bat
   ```

### Option 2: Linux/Mac (Manual)

1. **Compile all source files**
   ```bash
   javac -d bin -encoding UTF-8 *.java org/json/*.java
   ```

2. **Run the application**
   ```bash
   java -cp bin TowerDefenseGame
   ```

## 🎮 How to Play

### Getting Started
1. **Login/Register** - Create an account or use existing credentials
2. **Select Level** - Choose from 3 available difficulty levels
3. **Build Defense** - Click tower icons and place them strategically on the map

### Gameplay Controls
- **Mouse Click** - Select and place towers
- **Tower Icons** - Click to select tower type (costs displayed below)
- **Sell Button** - Click then select tower to sell (50% refund)
- **Pause Button (▶/❚❚)** - Pause/resume game
- **Leave Button** - Exit to main menu (saves progress)

### Strategy Tips
- Balance tower placement between coverage and cost
- Higher-tier towers cost more but deal more damage
- Use the sell feature to reposition ineffective towers
- Monitor your funds - running out of money early is dangerous
- Protect critical path choke points

## 📊 Data Persistence

The game stores data in the following files:

- `users_data.txt` - User credentials (username/password pairs)
- `game_scores.json` - High scores per user per level
- Both files are auto-generated on first run

**Note:** These files contain sensitive user data and are excluded from version control via `.gitignore`

## 🎨 Assets & Credits

Game assets are from [Kenney's Tower Defense Top-Down Pack](https://kenney.nl/assets/tower-defense-top-down), which provides high-quality, free-to-use game graphics.

All visual assets (sprites, tiles, UI elements) are located in the `kenney_tower-defense-top-downn` directory.

## 📦 Dependencies

- **org.json** - Lightweight JSON library for data serialization (included in repository)
- **Java Swing** - Built-in GUI framework (no external installation needed)

## 🔒 Security Note

This is an educational project. The authentication system uses basic password storage and should **not** be used as a reference for production applications. In real-world scenarios, always use proper password hashing (bcrypt, Argon2, etc.).

## 📝 Project Requirements Met

This project fulfills all CSE212 Term Project requirements:

- ✅ Multiple difficulty levels (3 levels)
- ✅ GUI implementation using Java Swing
- ✅ User registration and authentication
- ✅ Score tracking and file storage
- ✅ Mouse-based controls
- ✅ Tower selection bar with costs
- ✅ Sell tower functionality (50% refund)
- ✅ Health system
- ✅ Pause/resume functionality
- ✅ Leave confirmation dialog
- ✅ Dynamic difficulty progression
- ✅ Score calculation formula implementation

## 🐛 Known Issues

- Asset paths are hardcoded relative to project structure - ensure folder hierarchy matches expected layout
- Game data files created in project directory (not user home)

## 📄 License

This project is developed for educational purposes as part of CSE212 coursework at Yeditepe University.

## 👨‍💻 Author

Nisa Of 

