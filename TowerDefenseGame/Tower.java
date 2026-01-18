/**
 * Tower Class
 * Represents a defensive tower that shoots at enemies
 */
public class Tower {
    public enum TowerType {
        BASIC(140, 15, 150, 60, 50, "tower1"),
        ADVANCED(170, 25, 180, 25, 75, "tower2"), // Faster fire rate for tile250 tower
        HEAVY(220, 50, 200, 90, 100, "tower3"),
        MISSILE(190, 80, 250, 120, 150, "tower4");
        
        public final int health;
        public final int damage;
        public final int range;
        public final int fireRate;
        public final int cost;
        public final String assetKey;
        
        TowerType(int health, int damage, int range, int fireRate, int cost, String assetKey) {
            this.health = health;
            this.damage = damage;
            this.range = range;
            this.fireRate = fireRate;
            this.cost = cost;
            this.assetKey = assetKey;
        }
    }
    
    private int gridX, gridY;
    private int x, y;
    private TowerType type;
    private int cooldown;
    private int flashTimer;
    private Enemy target;
    private double angle; // Rotation angle in radians
    private int maxHealth;
    private int health;
    private boolean alive;
    
    public Tower(int gridX, int gridY, TowerType type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.x = gridX * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2;
        this.y = gridY * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2 + 100; // Account for UI offset
        this.type = type;
        this.cooldown = 0;
        this.flashTimer = 0;
        this.target = null;
        this.angle = 0;
        this.maxHealth = type.health;
        this.health = maxHealth;
        this.alive = true;
    }
    
    public void update(java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles) {
        if (!alive) return;

        if (cooldown > 0) {
            cooldown--;
        }
        if (flashTimer > 0) {
            flashTimer--;
        }
        
        // Find target
        target = null;
        double nearestDist = Double.MAX_VALUE;
        
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.shouldSpawn()) {
                double dist = Math.sqrt(Math.pow(enemy.getX() - x, 2) + Math.pow(enemy.getY() - y, 2));
                if (dist <= type.range && dist < nearestDist) {
                    target = enemy;
                    nearestDist = dist;
                }
            }
        }
        
        // Update angle and shoot
        if (target != null) {
            // Calculate angle to target
            angle = Math.atan2(target.getY() - y, target.getX() - x);
            
            // Shoot if cooldown is ready
            if (cooldown <= 0) {
                projectiles.add(new Projectile(x, y, target, type.damage));
                cooldown = type.fireRate;
                flashTimer = 5;
            }
        }
    }
    
    public void takeDamage(int damage) {
        if (!alive) return;
        health -= damage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    public boolean isAlive() { return alive; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public double getAngle() { return angle; }
    public boolean isFlashing() { return flashTimer > 0; }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public TowerType getType() { return type; }
    public int getRange() { return type.range; }
    public int getCost() { return type.cost; }
}

