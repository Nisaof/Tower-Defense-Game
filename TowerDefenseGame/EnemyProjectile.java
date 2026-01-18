/**
 * EnemyProjectile Class
 * Represents a bullet fired from an enemy toward a tower
 */
public class EnemyProjectile {
    private double x, y;
    private Tower target;
    private int damage;
    private double speed;
    private boolean active;
    private double angle;
    
    public EnemyProjectile(double x, double y, Tower target, int damage) {
        this.x = x;
        this.y = y;
        this.target = target;
        this.damage = damage;
        this.speed = 7.0;
        this.active = true;
        this.angle = Math.atan2(target.getY() - y, target.getX() - x);
    }
    
    public boolean update() {
        if (target == null || !target.isAlive()) {
            active = false;
            return false;
        }
        
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist < 5) {
            // Hit target
            target.takeDamage(damage);
            active = false;
            return false;
        }
        
        // Move towards target
        x += (dx / dist) * speed;
        y += (dy / dist) * speed;
        
        return true;
    }
    
    public boolean isActive() { return active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public int getDamage() { return damage; }
}

