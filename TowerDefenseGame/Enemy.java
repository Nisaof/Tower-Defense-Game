import java.awt.Point;
import java.util.List;

/**
 * Enemy Class
 * Represents an enemy unit that follows a path
 */
public class Enemy {
    public enum EnemyType {
        LIGHT(50, 1.5, 20, "enemy1"), // Slowed down from 2.5
        MEDIUM(80, 1.2, 30, "enemy2"), // Slowed down from 2.0
        HEAVY(150, 1.0, 50, "enemy3"), // Slowed down from 1.5
        TANK_GREEN(120, 1.1, 60, "enemy4"), // Slowed down from 1.8
        TANK_BROWN(180, 1.3, 80, "enemy5"),
        TANK_BLUE(200, 1.6, 90, "enemy6"),
        TANK_GRAY(250, 1.2, 120, "enemy7");
        
        public final int baseHealth;
        public final double baseSpeed;
        public final int reward;
        public final String assetKey;
        
        EnemyType(int baseHealth, double baseSpeed, int reward, String assetKey) {
            this.baseHealth = baseHealth;
            this.baseSpeed = baseSpeed;
            this.reward = reward;
            this.assetKey = assetKey;
        }
    }
    
    private List<Point> path;
    private int pathIndex;
    private double x, y;
    private EnemyType type;
    private int maxHealth;
    private int health;
    private double speed;
    private int reward;
    private boolean alive;
    private boolean reachedEnd;
    private int spawnDelay;
    private int attackRange;
    private int attackDamage;
    private int attackCooldown;
    private int attackTimer;
    
    public Enemy(List<Point> path, EnemyType type, int waveNumber, int spawnDelay) {
        this.path = path;
        this.pathIndex = 0;
        Point start = path.get(0);
        this.x = start.x;
        this.y = start.y;
        this.type = type;
        this.spawnDelay = spawnDelay;
        
        // Calculate stats based on wave
        this.maxHealth = type.baseHealth + (waveNumber * 10);
        this.health = maxHealth;
        this.speed = type.baseSpeed + (waveNumber * 0.1);
        this.reward = type.reward;
        this.alive = true;
        this.reachedEnd = false;
        setAttackStats(type);
        this.attackTimer = 0;
    }
    
    private void setAttackStats(EnemyType type) {
        switch (type) {
            case LIGHT:
                attackDamage = 6;
                attackRange = 140;
                attackCooldown = 70;
                break;
            case MEDIUM:
                attackDamage = 9;
                attackRange = 150;
                attackCooldown = 65;
                break;
            case HEAVY:
                attackDamage = 14;
                attackRange = 160;
                attackCooldown = 70;
                break;
            case TANK_GREEN:
                attackDamage = 12;
                attackRange = 160;
                attackCooldown = 60;
                break;
            case TANK_BROWN:
                attackDamage = 16;
                attackRange = 170;
                attackCooldown = 65;
                break;
            case TANK_BLUE:
                attackDamage = 18;
                attackRange = 180;
                attackCooldown = 60;
                break;
            case TANK_GRAY:
                attackDamage = 20;
                attackRange = 190;
                attackCooldown = 60;
                break;
            default:
                attackDamage = 8;
                attackRange = 140;
                attackCooldown = 70;
                break;
        }
    }
    
    public boolean update(java.util.List<Tower> towers, java.util.List<EnemyProjectile> enemyProjectiles) {
        if (spawnDelay > 0) {
            spawnDelay--;
            return true;
        }
        
        if (pathIndex >= path.size() - 1) {
            reachedEnd = true;
            return false;
        }
        
        Point target = path.get(pathIndex + 1);
        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist < speed) {
            pathIndex++;
            if (pathIndex >= path.size() - 1) {
                reachedEnd = true;
                return false;
            }
        } else {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
        
        if (attackTimer > 0) {
            attackTimer--;
        }
        
        // Attack nearest tower in range
        Tower targetTower = null;
        double nearestDist = Double.MAX_VALUE;
        for (Tower tower : towers) {
            if (tower.isAlive()) {
                double distToTower = Math.hypot(tower.getX() - x, tower.getY() - y);
                if (distToTower <= attackRange && distToTower < nearestDist) {
                    nearestDist = distToTower;
                    targetTower = tower;
                }
            }
        }
        
        if (targetTower != null && attackTimer <= 0) {
            enemyProjectiles.add(new EnemyProjectile(x, y, targetTower, attackDamage));
            attackTimer = attackCooldown;
        }
        
        return true;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            alive = false;
        }
    }
    
    public boolean isAlive() { return alive; }
    public boolean hasReachedEnd() { return reachedEnd; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getReward() { return reward; }
    public EnemyType getType() { return type; }
    public boolean shouldSpawn() { return spawnDelay <= 0; }
}


