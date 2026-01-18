import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Game Panel
 * Main game screen with rendering and game logic
 */
public class GamePanel extends JPanel implements ActionListener {
    private TowerDefenseGame game;
    private Timer gameTimer;
    private LevelData levelData;
    private int levelNumber;
    
    // Game state
    private int money;
    private int health;
    private int wave;
    private int enemiesKilled;
    private int moneySpent;
    private boolean paused;
    private boolean gameOver;
    private boolean gameOverHandled;
    private int gameOverTimer;
    private boolean levelComplete;
    
    // Game objects
    private List<Tower> towers;
    private List<Enemy> enemies;
    private List<Projectile> projectiles;
    private List<EnemyProjectile> enemyProjectiles;
    
    // Decorative animated elements (visual only)
    private List<DecorativeElement> decorativeElements;
    
    // Wave management
    private int spawnTimer;
    private boolean waveActive;
    
    // Inner class for decorative animated elements
    private static class DecorativeElement {
        double x, y;
        double vx, vy;
        String assetKey;
        int size;
        
        DecorativeElement(double x, double y, double vx, double vy, String assetKey, int size) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.assetKey = assetKey;
            this.size = size;
        }
        
        void update(int screenWidth, int screenHeight) {
            x += vx;
            y += vy;
            
            // Wrap around screen edges
            if (x < -size) x = screenWidth + size;
            if (x > screenWidth + size) x = -size;
            if (y < -size) y = screenHeight + size;
            if (y > screenHeight + size) y = -size;
        }
    }
    
    // UI state
    private Tower.TowerType selectedTowerType;
    private boolean sellMode;
    private Tower hoveredTower;
    private int hoveredGridX = -1;
    private int hoveredGridY = -1;
    
    // UI bounds
    private Rectangle leaveButton;
    private Rectangle pauseButton;
    private Rectangle prevLevelButton;
    private Rectangle nextLevelButton;
    private Rectangle[] towerButtons;
    private Rectangle sellButton;
    
    public GamePanel(TowerDefenseGame game, int levelNumber) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.levelData = LevelData.createLevels().get(levelNumber - 1);
        
        setLayout(null);
        setBackground(new Color(100, 200, 100));
        setFocusable(true);
        
        initGame();
        initUI();
        setupListeners();
    }
    
    private void initGame() {
        money = 500;
        health = 100;
        wave = 1;
        enemiesKilled = 0;
        moneySpent = 0;
        paused = false;
        gameOver = false;
        gameOverHandled = false;
        gameOverTimer = 0;
        levelComplete = false;
        
        towers = new ArrayList<>();
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        decorativeElements = new ArrayList<>();
        
        // Initialize decorative animated elements
        initDecorativeElements();
        
        spawnTimer = 0;
        waveActive = false;
        
        selectedTowerType = null;
        sellMode = false;
        hoveredTower = null;
    }
    
    private void initDecorativeElements() {
        decorativeElements.clear();
        java.util.Random rand = new java.util.Random();
        
        // Create 8-12 decorative elements that move across the screen
        int count = 8 + rand.nextInt(5);
        for (int i = 0; i < count; i++) {
            double x = rand.nextDouble() * TowerDefenseGame.SCREEN_WIDTH;
            double y = rand.nextDouble() * TowerDefenseGame.SCREEN_HEIGHT;
            double vx = (rand.nextDouble() - 0.5) * 0.5; // Slow horizontal movement
            double vy = (rand.nextDouble() - 0.5) * 0.5; // Slow vertical movement
            String assetKey = (rand.nextBoolean()) ? "deco_anim1" : "deco_anim2";
            int size = 30 + rand.nextInt(20); // Random size between 30-50
            
            decorativeElements.add(new DecorativeElement(x, y, vx, vy, assetKey, size));
        }
    }
    
    private void initUI() {
        // Button positions - Match positions used in drawUI()
        int rightButtonsX = TowerDefenseGame.SCREEN_WIDTH - 230;
        pauseButton = new Rectangle(rightButtonsX, 20, 100, 40);
        leaveButton = new Rectangle(rightButtonsX + 110, 20, 100, 40);
        
        // Level navigation buttons
        prevLevelButton = new Rectangle(10, TowerDefenseGame.SCREEN_HEIGHT / 2 - 40, 60, 80);
        nextLevelButton = new Rectangle(TowerDefenseGame.SCREEN_WIDTH - 70, TowerDefenseGame.SCREEN_HEIGHT / 2 - 40, 60, 80);
        
        // Tower buttons - Match positions used in drawUI()
        Tower.TowerType[] types = Tower.TowerType.values();
        int btnStartX = 480;
        int btnY = 10;
        int btnSize = 55;
        int gap = 12;
        
        towerButtons = new Rectangle[types.length];
        for (int i = 0; i < types.length; i++) {
            towerButtons[i] = new Rectangle(btnStartX + i * (btnSize + gap), btnY, btnSize, btnSize);
        }
        
        sellButton = new Rectangle(btnStartX + types.length * (btnSize + gap) + 10, btnY, btnSize, btnSize);
    }
    
    private void setupListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e.getPoint());
            }
        });
    }
    
    private void handleMouseClick(Point p) {
        // Check pause button (always allow pausing/resuming)
        if (pauseButton.contains(p)) {
            paused = !paused;
            repaint();
            return;
        }
        
        // If paused, don't handle other clicks
        if (paused || gameOver || levelComplete) return;
        
        // Check leave button
        if (leaveButton.contains(p)) {
            handleLeave();
            return;
        }
        
        // Check previous level button
        if (prevLevelButton.contains(p)) {
            if (levelNumber > 1) {
                stopGame();
                game.startGame(levelNumber - 1);
            }
            return;
        }
        
        // Check next level button
        if (nextLevelButton.contains(p)) {
            if (levelNumber < 3) {
                stopGame();
                game.startGame(levelNumber + 1);
            }
            return;
        }
        
        // Check tower buttons
        for (int i = 0; i < towerButtons.length; i++) {
            if (towerButtons[i].contains(p)) {
                Tower.TowerType[] types = Tower.TowerType.values();
                if (money >= types[i].cost) {
                    selectedTowerType = types[i];
                    sellMode = false;
                }
                return;
            }
        }
        
        // Check sell button
        if (sellButton.contains(p)) {
            sellMode = true;
            selectedTowerType = null;
            return;
        }
        
        // Check game area (accounting for UI offset ~100px)
        if (p.y > 100) {
            int gridX = p.x / TowerDefenseGame.TILE_SIZE;
            int gridY = (p.y - 100) / TowerDefenseGame.TILE_SIZE; // Account for UI offset
            
            if (sellMode) {
                sellTower(gridX, gridY);
            } else if (selectedTowerType != null) {
                placeTower(gridX, gridY);
            }
        }
    }
    
    private void handleMouseMove(Point p) {
        hoveredTower = null;
        hoveredGridX = -1;
        hoveredGridY = -1;
        
        if (p.y > 100) {
            int gridX = p.x / TowerDefenseGame.TILE_SIZE;
            int gridY = (p.y - 100) / TowerDefenseGame.TILE_SIZE; // Account for UI offset
            
            // Track hovered grid position for tower placement preview
            hoveredGridX = gridX;
            hoveredGridY = gridY;
            
            for (Tower tower : towers) {
                if (tower.getGridX() == gridX && tower.getGridY() == gridY) {
                    hoveredTower = tower;
                    break;
                }
            }
        }
        
        repaint(); // Repaint to show preview
    }
    
    private void placeTower(int gridX, int gridY) {
        // Check if spot is on path (cannot place tower on path)
        if (levelData.isPath(gridX, gridY)) {
            return;
        }
        
        // Check if spot has obstacle (cannot place tower on obstacle)
        if (levelData.isObstacle(gridX, gridY)) {
            return;
        }
        
        // Check if spot is empty
        for (Tower tower : towers) {
            if (tower.getGridX() == gridX && tower.getGridY() == gridY) {
                return;
            }
        }
        
        // Check if can afford
        if (money >= selectedTowerType.cost) {
            Tower newTower = new Tower(gridX, gridY, selectedTowerType);
            towers.add(newTower);
            money -= selectedTowerType.cost;
            moneySpent += selectedTowerType.cost;
            selectedTowerType = null;
        }
    }
    
    private void sellTower(int gridX, int gridY) {
        for (int i = 0; i < towers.size(); i++) {
            Tower tower = towers.get(i);
            if (tower.getGridX() == gridX && tower.getGridY() == gridY) {
                money += tower.getCost() / 2;
                towers.remove(i);
                sellMode = false;
                break;
            }
        }
    }
    
    private void handleLeave() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to leave the game?",
            "Leave Game",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            saveAndExit();
        }
    }
    
    private void saveAndExit() {
        int score = calculateScore();
        game.getAuthSystem().saveScore(levelNumber, score, enemiesKilled, health, moneySpent);
        stopGame();
        game.showPanel("MENU");
    }
    
    public void startGame() {
        gameTimer = new Timer(1000 / 60, this); // 60 FPS
        gameTimer.start();
    }
    
    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver && !levelComplete) {
            updateGame();
        }
        // Game over countdown to show overlay before dialog
        if (gameOver && !gameOverHandled && gameOverTimer > 0) {
            gameOverTimer--;
            if (gameOverTimer == 0) {
                showGameOver();
                gameOverHandled = true;
            }
        }
        repaint();
    }
    
    private void updateGame() {
        // Spawn waves
        if (!waveActive && wave <= levelData.getWaves()) {
            if (spawnTimer <= 0) {
                spawnWave();
                spawnTimer = 60; // 1 second delay between waves
            } else {
                spawnTimer--;
            }
        }
        
        // Update enemies
        List<Enemy> toRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            // Always update to let spawnDelay countdown happen
            if (!enemy.update(towers, enemyProjectiles)) {
                if (enemy.hasReachedEnd()) {
                    health -= 20;
                    if (health < 0) health = 0;
                }
                toRemove.add(enemy);
            }
            
            if (!enemy.isAlive()) {
                money += enemy.getReward();
                enemiesKilled++;
                toRemove.add(enemy);
            }
        }
        enemies.removeAll(toRemove);
        
        // Check if wave is complete
        if (waveActive && enemies.isEmpty()) {
            waveActive = false;
            wave++;
            spawnTimer = 0; // Next wave immediately
        }
        
        // Update towers
        List<Tower> deadTowers = new ArrayList<>();
        for (Tower tower : towers) {
            tower.update(enemies, projectiles);
            if (!tower.isAlive()) {
                deadTowers.add(tower);
            }
        }
        towers.removeAll(deadTowers);
        
        // Update projectiles
        List<Projectile> toRemoveProj = new ArrayList<>();
        for (Projectile proj : projectiles) {
            if (!proj.update()) {
                toRemoveProj.add(proj);
            }
        }
        projectiles.removeAll(toRemoveProj);
        
        // Update enemy projectiles
        List<EnemyProjectile> toRemoveEnemyProj = new ArrayList<>();
        for (EnemyProjectile proj : enemyProjectiles) {
            if (!proj.update()) {
                toRemoveEnemyProj.add(proj);
            }
        }
        enemyProjectiles.removeAll(toRemoveEnemyProj);
        
        // Update decorative elements (visual only, no gameplay impact)
        for (DecorativeElement elem : decorativeElements) {
            elem.update(getWidth(), getHeight());
        }
        
        // Check game over
        if (health <= 0 && !gameOver) {
            gameOver = true;
            gameOverTimer = 90; // ~1.5 seconds at 60 FPS to show overlay
        }
        
        // Check level complete
        if (wave > levelData.getWaves() && enemies.isEmpty()) {
            levelComplete = true;
            showLevelComplete();
        }
    }
    
    private void spawnWave() {
        int numEnemies = 5 + wave * 2;
        Enemy.EnemyType[] allTypes = Enemy.EnemyType.values();
        
        // Use a structure where we spawn types in order
        // Wave 1: only LIGHT
        // Wave 2: LIGHT then MEDIUM
        // Wave 3: LIGHT, MEDIUM, HEAVY
        // etc.
        
        int typeLimit = Math.min(wave, allTypes.length);
        int enemiesPerType = numEnemies / typeLimit;
        
        for (int i = 0; i < numEnemies; i++) {
            // Determine type based on index to ensure they come in groups/order
            int typeIndex = Math.min(i / Math.max(1, enemiesPerType), typeLimit - 1);
            Enemy.EnemyType type = allTypes[typeIndex];
            
            Enemy enemy = new Enemy(levelData.getPath(), type, wave, i * 30);
            enemies.add(enemy);
        }
        
        waveActive = true;
    }
    
    private int calculateScore() {
        return (enemiesKilled * 20) + health + moneySpent;
    }
    
    private void showGameOver() {
        int score = calculateScore();
        game.getAuthSystem().saveScore(levelNumber, score, enemiesKilled, health, moneySpent);
        
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                "GAME OVER!\n\n" +
                "Final Score: %d\n" +
                "Enemies Killed: %d\n" +
                "Health Remaining: %d%%\n" +
                "Money Spent: $%d",
                score, enemiesKilled, health, moneySpent
            );
            
            int result = JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Main Menu"},
                "Main Menu"
            );
            
            stopGame();
            game.showPanel("MENU");
        });
    }
    
    private void showLevelComplete() {
        int score = calculateScore();
        game.getAuthSystem().saveScore(levelNumber, score, enemiesKilled, health, moneySpent);
        
        SwingUtilities.invokeLater(() -> {
            String message = String.format(
                "LEVEL COMPLETE!\n\n" +
                "Final Score: %d\n" +
                "Enemies Killed: %d\n" +
                "Health Remaining: %d%%\n" +
                "Money Spent: $%d",
                score, enemiesKilled, health, moneySpent
            );
            
            // If not the last level, offer to go to next level
            if (levelNumber < 3) {
                int result = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Level Complete",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Next Level", "Main Menu"},
                    "Next Level"
                );
                
                stopGame();
                if (result == 0) {
                    // Next Level button clicked
                    game.startGame(levelNumber + 1);
                } else {
                    // Main Menu button clicked
                    game.showPanel("MENU");
                }
            } else {
                // Last level completed
                JOptionPane.showMessageDialog(
                    this,
                    message + "\n\nCongratulations! You completed all levels!",
                    "All Levels Complete!",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                stopGame();
                game.showPanel("MENU");
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background
        drawBackground(g2d);
        
        // Draw obstacles
        drawObstacles(g2d);
        
        // Draw decorations
        drawDecorations(g2d);
        
        // Draw decorative animated elements (visual only, behind game objects)
        drawDecorativeElements(g2d);
        
        // Draw towers
        for (Tower tower : towers) {
            drawTower(g2d, tower);
        }
        
        // Draw tower placement preview
        if (selectedTowerType != null && hoveredGridX >= 0 && hoveredGridY >= 0) {
            drawTowerPlacementPreview(g2d, hoveredGridX, hoveredGridY);
        }
        
        // Draw range circle for hovered tower
        if (hoveredTower != null) {
            g2d.setColor(new Color(255, 255, 255, 50));
            g2d.setStroke(new BasicStroke(2));
            int range = hoveredTower.getRange();
            g2d.drawOval(
                hoveredTower.getX() - range,
                hoveredTower.getY() - range,
                range * 2,
                range * 2
            );
        }
        
        // Draw enemies
        for (Enemy enemy : enemies) {
            if (enemy.shouldSpawn()) {
                drawEnemy(g2d, enemy);
            }
        }
        
        // Draw projectiles
        for (Projectile proj : projectiles) {
            drawProjectile(g2d, proj);
        }
        
        // Draw enemy projectiles
        for (EnemyProjectile proj : enemyProjectiles) {
            drawEnemyProjectile(g2d, proj);
        }
        
        // Draw UI
        drawUI(g2d);
        
        // Draw level navigation buttons
        drawLevelNavButtons(g2d);
        
        // Draw pause overlay
        if (paused) {
            g2d.setColor(new Color(0, 0, 0, 128));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 72));
            String text = "PAUSED";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }

        // Draw game over overlay before dialog
        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 170));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 80));
            String text = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }
    
    private void drawTower(Graphics2D g2d, Tower tower) {
        // Smaller tower size for better precision
        int towerSize = TowerDefenseGame.TILE_SIZE - 12;
        Image img = game.getAssetManager().getScaledAsset(
            tower.getType().assetKey,
            towerSize,
            towerSize
        );
        
        int drawX = tower.getGridX() * TowerDefenseGame.TILE_SIZE + 6;
        int drawY = tower.getGridY() * TowerDefenseGame.TILE_SIZE + 6 + 100; // Account for UI offset
        int centerX = drawX + towerSize / 2;
        int centerY = drawY + towerSize / 2;
        
        // Save current transform
        java.awt.geom.AffineTransform old = g2d.getTransform();
        
        // Rotate and draw
        g2d.rotate(tower.getAngle() + Math.PI / 2, centerX, centerY);
        g2d.drawImage(img, drawX, drawY, null);
        
        // Draw muzzle flash if flashing
        if (tower.isFlashing()) {
            Image flashImg = game.getAssetManager().getScaledAsset("bullet", 24, 24);
            // Draw flash slightly offset in front of the tower (at the barrel end)
            g2d.drawImage(flashImg, drawX + (towerSize - 24) / 2, drawY - 12, null);
        }
        
        // Restore transform
        g2d.setTransform(old);

        // Draw tower health bar
        int barWidth = towerSize - 8;
        int barHeight = 4;
        double hpRatio = Math.max(0, (double)tower.getHealth() / tower.getMaxHealth());
        int barX = drawX + 4;
        int barY = drawY - 8;
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        g2d.setColor(new Color(50, 200, 50));
        g2d.fillRect(barX, barY, (int)(barWidth * hpRatio), barHeight);
    }
    
    private void drawEnemy(Graphics2D g2d, Enemy enemy) {
        // Draw enemy sprite
        Image img = game.getAssetManager().getScaledAsset(enemy.getType().assetKey, 40, 40);
        g2d.drawImage(img, (int)enemy.getX() - 20, (int)enemy.getY() - 20, null);
        
        // Draw health bar
        int barWidth = 30;
        int barHeight = 4;
        double healthRatio = Math.max(0, (double)enemy.getHealth() / enemy.getMaxHealth());
        
        g2d.setColor(Color.RED);
        g2d.fillRect((int)enemy.getX() - barWidth/2, (int)enemy.getY() - 30, barWidth, barHeight);
        
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int)enemy.getX() - barWidth/2, (int)enemy.getY() - 30, 
                     (int)(barWidth * healthRatio), barHeight);
    }
    
    private void drawProjectile(Graphics2D g2d, Projectile proj) {
        // Save current transform
        java.awt.geom.AffineTransform old = g2d.getTransform();
        
        // Translate and rotate fire sprite to point at target
        g2d.translate(proj.getX(), proj.getY());
        g2d.rotate(proj.getAngle() + Math.PI / 2);
        
        Image fireImg = game.getAssetManager().getScaledAsset("bullet", 24, 24);
        g2d.drawImage(fireImg, -12, -12, null);
        
        // Restore transform
        g2d.setTransform(old);
    }

    private void drawEnemyProjectile(Graphics2D g2d, EnemyProjectile proj) {
        // Save current transform
        java.awt.geom.AffineTransform old = g2d.getTransform();
        
        // Translate and rotate fire sprite to point at target
        g2d.translate(proj.getX(), proj.getY());
        g2d.rotate(proj.getAngle() + Math.PI / 2);
        
        Image fireImg = game.getAssetManager().getScaledAsset("bullet", 20, 20);
        // Tint bullet red for enemy shots
        g2d.setColor(new Color(200, 50, 50, 180));
        g2d.fillOval(-6, -6, 12, 12);
        g2d.drawImage(fireImg, -10, -10, null);
        
        // Restore transform
        g2d.setTransform(old);
    }

    private void drawObstacles(Graphics2D g2d) {
        if (levelData == null) return;
        int tileSize = TowerDefenseGame.TILE_SIZE;
        for (LevelData.Obstacle obs : levelData.getObstacles()) {
            int x = obs.gridX * tileSize;
            int y = obs.gridY * tileSize + 100; // Account for UI offset
            Image img = game.getAssetManager().getScaledAsset(obs.assetKey, tileSize, tileSize);
            g2d.drawImage(img, x, y, null);
        }
    }
    
    private void drawDecorations(Graphics2D g2d) {
        if (levelData == null) return;
        for (LevelData.Decoration dec : levelData.getDecorations()) {
            Image img = game.getAssetManager().getScaledAsset(dec.assetKey, 32, 32);
            g2d.drawImage(img, dec.x - 16, dec.y - 16, null);
        }
    }
    
    private void drawDecorativeElements(Graphics2D g2d) {
        // Draw decorative animated elements with transparency for visual effect
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); // 40% opacity
        for (DecorativeElement elem : decorativeElements) {
            Image img = game.getAssetManager().getScaledAsset(elem.assetKey, elem.size, elem.size);
            g2d.drawImage(img, (int)(elem.x - elem.size/2), (int)(elem.y - elem.size/2), null);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset to full opacity
    }

    private void drawBackground(Graphics2D g2d) {
        // Draw background tile pattern if available, otherwise use color
        String bgAssetKey = levelData.getBackgroundAssetKey();
        if (bgAssetKey != null && !bgAssetKey.isEmpty()) {
            // Draw tiled background pattern
            BufferedImage bgTile = game.getAssetManager().getAsset(bgAssetKey);
            int tileSize = TowerDefenseGame.TILE_SIZE;
            for (int y = 100; y < getHeight(); y += tileSize) {
                for (int x = 0; x < getWidth(); x += tileSize) {
                    g2d.drawImage(bgTile, x, y, tileSize, tileSize, null);
                }
            }
        } else {
            // Use level-specific background color for visual distinction
            g2d.setColor(levelData.getBackgroundColor());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Draw path with level-specific color
        g2d.setColor(levelData.getPathColor());
        g2d.setStroke(new BasicStroke(40, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        List<Point> path = levelData.getPath();
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    private void drawTowerPlacementPreview(Graphics2D g2d, int gridX, int gridY) {
        int tileSize = TowerDefenseGame.TILE_SIZE;
        int x = gridX * tileSize;
        int y = gridY * tileSize + 100; // Account for UI offset
        
        // Check if placement is valid
        boolean canPlace = true;
        boolean isOnPath = levelData.isPath(gridX, gridY);
        boolean hasObstacle = levelData.isObstacle(gridX, gridY);
        boolean hasTower = false;
        boolean canAfford = money >= selectedTowerType.cost;
        
        // Check if spot already has a tower
        for (Tower tower : towers) {
            if (tower.getGridX() == gridX && tower.getGridY() == gridY) {
                hasTower = true;
                break;
            }
        }
        
        canPlace = !isOnPath && !hasObstacle && !hasTower && canAfford;
        
        // Draw preview square
        if (canPlace) {
            // Green for valid placement
            g2d.setColor(new Color(0, 255, 0, 100));
            g2d.fillRect(x, y, tileSize, tileSize);
            g2d.setColor(new Color(0, 200, 0, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, tileSize, tileSize);
        } else {
            // Red for invalid placement
            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRect(x, y, tileSize, tileSize);
            g2d.setColor(new Color(200, 0, 0, 200));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, tileSize, tileSize);
        }
    }
    
    private void drawLevelNavButtons(Graphics2D g2d) {
        // Previous level button (RIGHT arrow - reversed 180 degrees)
        if (levelNumber > 1) {
            g2d.setColor(new Color(33, 150, 243, 200));
            g2d.fillRoundRect(prevLevelButton.x, prevLevelButton.y, 
                             prevLevelButton.width, prevLevelButton.height, 10, 10);
            
            // Draw right arrow using Polygon (reversed for previous button)
            g2d.setColor(Color.WHITE);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int centerX = prevLevelButton.x + prevLevelButton.width / 2;
            int centerY = prevLevelButton.y + prevLevelButton.height / 2;
            int arrowSize = 20;
            
            int[] xPoints = {centerX - arrowSize/2, centerX + arrowSize/2, centerX + arrowSize/2};
            int[] yPoints = {centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            Polygon rightArrow = new Polygon(xPoints, yPoints, 3);
            g2d.fillPolygon(rightArrow);
        }
        
        // Next level button (LEFT arrow - reversed 180 degrees)
        if (levelNumber < 3) {
            g2d.setColor(new Color(33, 150, 243, 200));
            g2d.fillRoundRect(nextLevelButton.x, nextLevelButton.y, 
                             nextLevelButton.width, nextLevelButton.height, 10, 10);
            
            // Draw left arrow using Polygon (reversed for next button)
            g2d.setColor(Color.WHITE);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int centerX = nextLevelButton.x + nextLevelButton.width / 2;
            int centerY = nextLevelButton.y + nextLevelButton.height / 2;
            int arrowSize = 20;
            
            int[] xPoints = {centerX + arrowSize/2, centerX - arrowSize/2, centerX - arrowSize/2};
            int[] yPoints = {centerY, centerY - arrowSize/2, centerY + arrowSize/2};
            Polygon leftArrow = new Polygon(xPoints, yPoints, 3);
            g2d.fillPolygon(leftArrow);
        }
    }
    
    private void drawUI(Graphics2D g2d) {
        // Top bar - Darker background as in image
        g2d.setColor(new Color(26, 28, 35));
        g2d.fillRect(0, 0, getWidth(), 80);
        
        // Horizontal line separator
        g2d.setColor(new Color(60, 60, 70));
        g2d.drawLine(0, 80, getWidth(), 80);
        
        // Funds, Health, Wave, Towers text
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        int startX = 15;
        int yPos = 45;
        
        // Funds
        g2d.setColor(new Color(255, 215, 64)); // Yellowish
        g2d.drawString("Funds: ", startX, yPos);
        g2d.setColor(Color.WHITE);
        g2d.drawString("$" + money, startX + 60, yPos);
        
        // Separator
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawString("|", startX + 130, yPos);
        
        // Health
        g2d.setColor(new Color(76, 175, 80)); // Green
        g2d.drawString("Health: ", startX + 150, yPos);
        g2d.setColor(Color.WHITE);
        g2d.drawString(health + "%", startX + 215, yPos);
        
        // Separator
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawString("|", startX + 270, yPos);
        
        // Wave
        g2d.setColor(Color.WHITE);
        g2d.drawString("Wave: " + wave + "/" + levelData.getWaves(), startX + 290, yPos);
        
        // Separator
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawString("|", startX + 390, yPos);
        
        // Towers label
        g2d.setColor(Color.WHITE);
        g2d.drawString("Towers:", startX + 410, yPos);
        
        // Tower buttons styling - Square with dark background
        Tower.TowerType[] types = Tower.TowerType.values();
        int btnStartX = 480;
        int btnY = 10;
        int btnSize = 55; // Slightly larger box
        int gap = 12;
        
        for (int i = 0; i < types.length; i++) {
            Rectangle btn = new Rectangle(btnStartX + i * (btnSize + gap), btnY, btnSize, btnSize);
            towerButtons[i] = btn; 
            
            // Selection highlight
            if (selectedTowerType == types[i]) {
                g2d.setColor(new Color(0, 255, 255)); 
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(btn.x - 2, btn.y - 2, btn.width + 4, btn.height + 4);
            }
            
            // Button background
            g2d.setColor(new Color(45, 48, 55));
            g2d.fillRect(btn.x, btn.y, btn.width, btn.height);
            
            // Button border
            g2d.setColor(new Color(100, 100, 110));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(btn.x, btn.y, btn.width, btn.height);
            
            // Tower icon - Scaled down to fit properly inside 55px box
            Image img = game.getAssetManager().getScaledAsset(types[i].assetKey, 40, 40);
            g2d.drawImage(img, btn.x + (btnSize - 40) / 2, btn.y + (btnSize - 40) / 2, null);
            
            // Cost text below button
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.setColor(Color.WHITE);
            String costText = "$" + types[i].cost;
            FontMetrics fm = g2d.getFontMetrics();
            int costWidth = fm.stringWidth(costText);
            g2d.drawString(costText, btn.x + (btnSize - costWidth) / 2, btn.y + btnSize + 14);
        }
        
        // Sell button styling
        int sellX = btnStartX + types.length * (btnSize + gap) + 10;
        sellButton = new Rectangle(sellX, btnY, btnSize, btnSize);
        
        Color sellBg = sellMode ? new Color(211, 47, 47) : new Color(45, 48, 55);
        g2d.setColor(sellBg);
        g2d.fillRect(sellButton.x, sellButton.y, sellButton.width, sellButton.height);
        
        g2d.setColor(sellMode ? Color.WHITE : new Color(211, 47, 47));
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("$", sellButton.x + 18, sellButton.y + 35);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("SELL", sellButton.x + 10, sellButton.y + btnSize + 15);
        
        // Pause and Leave buttons - Side by side on the right
        // Update positions to match screen width (in case window is resized)
        int rightButtonsX = getWidth() - 230;
        pauseButton = new Rectangle(rightButtonsX, 20, 100, 40);
        leaveButton = new Rectangle(rightButtonsX + 110, 20, 100, 40);
        
        // Pause Button
        g2d.setColor(new Color(45, 48, 55));
        g2d.fillRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 5, 5);
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawRoundRect(pauseButton.x, pauseButton.y, pauseButton.width, pauseButton.height, 5, 5);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String pauseText = paused ? "RESUME" : "PAUSE";
        int pauseWidth = g2d.getFontMetrics().stringWidth(pauseText);
        g2d.drawString(pauseText, pauseButton.x + (pauseButton.width - pauseWidth) / 2, pauseButton.y + 25);
        
        // Leave Button
        g2d.setColor(new Color(45, 48, 55));
        g2d.fillRoundRect(leaveButton.x, leaveButton.y, leaveButton.width, leaveButton.height, 5, 5);
        g2d.setColor(new Color(100, 100, 110));
        g2d.drawRoundRect(leaveButton.x, leaveButton.y, leaveButton.width, leaveButton.height, 5, 5);
        
        g2d.setColor(Color.WHITE);
        int leaveWidth = g2d.getFontMetrics().stringWidth("LEAVE");
        g2d.drawString("LEAVE", leaveButton.x + (leaveButton.width - leaveWidth) / 2, leaveButton.y + 25);
    }
}

