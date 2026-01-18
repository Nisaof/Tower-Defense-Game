import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private String assetPath;
    private Map<String, BufferedImage> assets;
    
    public AssetManager() {
        assets = new HashMap<>();
        findAssetPath();
        loadAssets();
    }
    
    private void findAssetPath() {
        // Get the current working directory
        String userDir = System.getProperty("user.dir");
        File currentDir = new File(userDir);
        
        // Try to find the asset folder by searching up the directory tree
        File searchDir = currentDir;
        int maxDepth = 5; // Prevent infinite loops
        int depth = 0;
        
        while (searchDir != null && depth < maxDepth) {
            File assetDir = new File(searchDir, "kenney_tower-defense-top-downn" + File.separator + "PNG" + File.separator + "Default size");
            File testFile = new File(assetDir, "towerDefense_tile179.png");
            
            if (testFile.exists()) {
                assetPath = assetDir.getAbsolutePath() + File.separator;
                System.out.println("Found assets at: " + testFile.getAbsolutePath());
                return;
            }
            
            // Move up one directory
            searchDir = searchDir.getParentFile();
            depth++;
        }
        
        // If not found, try relative paths from current directory
        String[] relativePaths = {
            "kenney_tower-defense-top-downn" + File.separator + "PNG" + File.separator + "Default size" + File.separator,
            ".." + File.separator + "kenney_tower-defense-top-downn" + File.separator + "PNG" + File.separator + "Default size" + File.separator,
            ".." + File.separator + ".." + File.separator + "kenney_tower-defense-top-downn" + File.separator + "PNG" + File.separator + "Default size" + File.separator
        };
        
        for (String path : relativePaths) {
            File testFile = new File(path + "towerDefense_tile179.png");
            if (testFile.exists()) {
                assetPath = path;
                System.out.println("Found assets at: " + testFile.getAbsolutePath());
                return;
            }
        }
        
        // Final fallback
        assetPath = ".." + File.separator + "kenney_tower-defense-top-downn" + File.separator + "PNG" + File.separator + "Default size" + File.separator;
        System.err.println("Warning: Could not find asset directory. Using fallback: " + assetPath);
        System.err.println("Current working directory: " + userDir);
    }
    
    private void loadAssets() {
        // Tiles
        // RGB added because I understood wrong way then I couldn't change.
        loadAsset("grass", "towerDefense_tile179.png", new Color(100, 200, 100));
        loadAsset("grass_alt", "towerDefense_tile180.png", new Color(90, 180, 90));
        loadAsset("path", "towerDefense_tile049.png", new Color(150, 120, 80));
        loadAsset("water", "towerDefense_tile109.png", new Color(100, 150, 200));
        loadAsset("sand", "towerDefense_tile128.png", new Color(210, 190, 140));
        loadAsset("level1_bg", "towerDefense_tile129.png", new Color(90, 200, 90)); // Level 1 background 
        loadAsset("level2_bg", "towerDefense_tile129.png", new Color(90, 200, 90)); // Level 2 background 
        loadAsset("level3_bg", "towerDefense_tile129.png", new Color(90, 200, 90)); // Level 3 background 
        loadAsset("obstacle", "towerDefense_tile160.png", new Color(100, 100, 100)); // Obstacle/blocker 
        loadAsset("obstacle136", "towerDefense_tile136.png", new Color(120, 160, 120)); // Additional obstacle 
        
        // Decorations 
        loadAsset("dec1", "towerDefense_tile130.png", new Color(100, 200, 100));
        loadAsset("dec2", "towerDefense_tile131.png", new Color(100, 200, 100));
        loadAsset("dec3", "towerDefense_tile132.png", new Color(100, 200, 100));
        loadAsset("dec4", "towerDefense_tile133.png", new Color(100, 200, 100));
        loadAsset("dec5", "towerDefense_tile134.png", new Color(100, 200, 100));
        
        // Towers 
        loadAsset("tower1", "towerDefense_tile249.png", new Color(100, 200, 100)); // Yellow Soldier / New Tower 1
        loadAsset("tower2", "towerDefense_tile250.png", new Color(200, 100, 100)); // 75$ Tower - Updated Asset
        loadAsset("tower3", "towerDefense_tile205.png", new Color(100, 100, 255)); // Blue Double Barrel
        loadAsset("tower4", "towerDefense_tile206.png", new Color(150, 150, 150)); // Gray Double Barrel
        
        // Enemies - Sequential soldier assets as requested
        loadAsset("enemy1", "towerDefense_tile245.png", new Color(100, 255, 100)); // First soldier
        loadAsset("enemy2", "towerDefense_tile246.png", new Color(255, 100, 100)); // Second soldier
        loadAsset("enemy3", "towerDefense_tile247.png", new Color(100, 100, 255)); // Third soldier
        loadAsset("enemy4", "towerDefense_tile248.png", new Color(200, 200, 100)); // Fourth soldier
        
        // Advanced Enemies - Planes (for later waves)
        loadAsset("enemy5", "towerDefense_tile248.png", new Color(150, 150, 150)); // Updated to tile248
        loadAsset("enemy6", "towerDefense_tile269.png", new Color(255, 50, 50));   // Red Plane
        loadAsset("enemy7", "towerDefense_tile270.png", new Color(50, 100, 255));  // Blue Plane
        
        // Projectiles - fire effects
        loadAsset("bullet", "towerDefense_tile296.png", new Color(255, 150, 0));
        loadAsset("fire1", "towerDefense_tile295.png", new Color(255, 150, 0));
        loadAsset("fire2", "towerDefense_tile296.png", new Color(255, 150, 0));
        loadAsset("fire3", "towerDefense_tile297.png", new Color(255, 150, 0));
        
        // Plane
        loadAsset("plane", "towerDefense_tile248.png", new Color(100, 100, 100)); // Updated to tile248
        
        // Decorative animated elements (visual only, no gameplay impact)
        loadAsset("deco_anim1", "towerDefense_tile270.png", new Color(100, 150, 200));
        loadAsset("deco_anim2", "towerDefense_tile271.png", new Color(150, 100, 200));
    }
    
    private void loadAsset(String key, String filename, Color fallbackColor) {
        try {
            File file = new File(assetPath + filename);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                assets.put(key, img);
            } else {
                System.err.println("Warning: Asset not found: " + file.getAbsolutePath());
                assets.put(key, createPlaceholder(fallbackColor));
            }
        } catch (Exception e) {
            System.err.println("Error loading asset " + filename + ": " + e.getMessage());
            assets.put(key, createPlaceholder(fallbackColor));
        }
    }
    
    private BufferedImage createPlaceholder(Color color) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 64, 64);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 63, 63);
        g.dispose();
        return img;
    }
    
    public BufferedImage getAsset(String key) {
        return assets.getOrDefault(key, createPlaceholder(Color.GRAY));
    }
    
    public BufferedImage getScaledAsset(String key, int width, int height) {
        BufferedImage original = getAsset(key);
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }
}


