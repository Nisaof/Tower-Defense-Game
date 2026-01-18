import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Level Data Class
 * Contains level configuration
 * Uses grid-based mapping: 1 = path, 0 = empty (tower placeable)
 */
public class LevelData {
    private String name;
    private int[][] grid; // Grid mapping: 1 = path, 0 = empty
    private List<Point> path; // Calculated from grid
    private List<Decoration> decorations;
    private List<Obstacle> obstacles; // Obstacles that block tower placement
    private int waves;
    private int spawnInterval;
    private Color backgroundColor;
    private Color pathColor;
    private String backgroundAssetKey; // Asset key for background tile pattern
    private int gridWidth;
    private int gridHeight;
    
    public static class Obstacle {
        public final int gridX, gridY;
        public final String assetKey;
        public Obstacle(int gridX, int gridY, String assetKey) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.assetKey = assetKey;
        }
    }
    
    public static class Decoration {
        public final int x, y;
        public final String assetKey;
        public Decoration(int x, int y, String assetKey) {
            this.x = x;
            this.y = y;
            this.assetKey = assetKey;
        }
    }
    
    // Grid-based constructor
    public LevelData(String name, String gridString, int waves, int spawnInterval, Color backgroundColor, Color pathColor) {
        this.name = name;
        this.waves = waves;
        this.spawnInterval = spawnInterval;
        this.decorations = new ArrayList<>();
        this.backgroundColor = backgroundColor;
        this.pathColor = pathColor;
        this.backgroundAssetKey = null; // Default: no tile pattern
        
        // Parse grid from string (format: "1 0 0 1\n0 0 0 1")
        parseGrid(gridString);
        
        // Calculate path from grid
        this.path = calculatePathFromGrid();
    }
    
    // Grid-based constructor with background asset
    public LevelData(String name, String gridString, int waves, int spawnInterval, Color backgroundColor, Color pathColor, String backgroundAssetKey) {
        this.name = name;
        this.waves = waves;
        this.spawnInterval = spawnInterval;
        this.decorations = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.backgroundColor = backgroundColor;
        this.pathColor = pathColor;
        this.backgroundAssetKey = backgroundAssetKey;
        
        // Parse grid from string (format: "1 0 0 1\n0 0 0 1")
        parseGrid(gridString);
        
        // Calculate path from grid
        this.path = calculatePathFromGrid();
    }
    
    // Legacy constructor for backward compatibility
    public LevelData(String name, List<Point> path, int waves, int spawnInterval) {
        this.name = name;
        this.path = path;
        this.waves = waves;
        this.spawnInterval = spawnInterval;
        this.decorations = new ArrayList<>();
        // Default colors
        this.backgroundColor = new Color(90, 200, 90);
        this.pathColor = new Color(150, 120, 80);
        // Create grid from path (for compatibility)
        this.grid = null;
    }
    
    // Legacy constructor for backward compatibility
    public LevelData(String name, List<Point> path, int waves, int spawnInterval, Color backgroundColor, Color pathColor) {
        this.name = name;
        this.path = path;
        this.waves = waves;
        this.spawnInterval = spawnInterval;
        this.decorations = new ArrayList<>();
        this.backgroundColor = backgroundColor;
        this.pathColor = pathColor;
        // Create grid from path (for compatibility)
        this.grid = null;
    }
    
    private void parseGrid(String gridString) {
        String[] lines = gridString.trim().split("\n");
        gridHeight = lines.length;
        gridWidth = 0;
        
        // Find max width
        for (String line : lines) {
            String[] cells = line.trim().split("\\s+");
            gridWidth = Math.max(gridWidth, cells.length);
        }
        
        grid = new int[gridHeight][gridWidth];
        
        for (int y = 0; y < lines.length; y++) {
            String[] cells = lines[y].trim().split("\\s+");
            for (int x = 0; x < cells.length; x++) {
                grid[y][x] = Integer.parseInt(cells[x]);
            }
        }
    }
    
    private List<Point> calculatePathFromGrid() {
        List<Point> path = new ArrayList<>();
        
        // Find start point: first 1 in grid (leftmost, then topmost)
        int startX = -1, startY = -1;
        
        // Find first 1 by scanning left to right, top to bottom
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == 1) {
                    startX = x;
                    startY = y;
                    break;
                }
            }
            if (startX != -1) break;
        }
        
        if (startX == -1) {
            // No path found, return empty
            return path;
        }
        
        // Follow path using improved pathfinding
        int currentX = startX;
        int currentY = startY;
        boolean[][] visited = new boolean[gridHeight][gridWidth];
        
        path.add(new Point(
            currentX * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2,
            currentY * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2 + 100 // UI offset
        ));
        
        // Improved path following: prioritize forward direction, then perpendicular
        int lastDirX = 1, lastDirY = 0; // Default: moving right
        
        while (true) {
            visited[currentY][currentX] = true;
            int nextX = -1, nextY = -1;
            
            // Priority order: continue in same direction, then perpendicular directions
            int[][] directions;
            if (lastDirX != 0) {
                // Moving horizontally, check horizontal first, then vertical
                directions = new int[][]{
                    {lastDirX, 0},      // Continue same direction
                    {0, 1}, {0, -1},    // Perpendicular (down, up)
                    {-lastDirX, 0}     // Opposite direction (last resort)
                };
            } else {
                // Moving vertically, check vertical first, then horizontal
                directions = new int[][]{
                    {0, lastDirY},      // Continue same direction
                    {1, 0}, {-1, 0},   // Perpendicular (right, left)
                    {0, -lastDirY}     // Opposite direction (last resort)
                };
            }
            
            for (int[] dir : directions) {
                int nx = currentX + dir[0];
                int ny = currentY + dir[1];
                
                if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight &&
                    grid[ny][nx] == 1 && !visited[ny][nx]) {
                    nextX = nx;
                    nextY = ny;
                    lastDirX = dir[0];
                    lastDirY = dir[1];
                    break;
                }
            }
            
            if (nextX == -1) {
                // Check if we're at the end (rightmost column or bottom row)
                if (currentX == gridWidth - 1 || currentY == gridHeight - 1) {
                    break;
                }
                // Try to find any unvisited adjacent 1 (backtrack prevention)
                for (int[] dir : new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}}) {
                    int nx = currentX + dir[0];
                    int ny = currentY + dir[1];
                    if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight &&
                        grid[ny][nx] == 1) {
                        nextX = nx;
                        nextY = ny;
                        lastDirX = dir[0];
                        lastDirY = dir[1];
                        break;
                    }
                }
                if (nextX == -1) break;
            }
            
            currentX = nextX;
            currentY = nextY;
            
            path.add(new Point(
                currentX * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2,
                currentY * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2 + 100 // UI offset
            ));
        }
        
        return path;
    }
    
    public boolean isPath(int gridX, int gridY) {
        if (grid == null) return false;
        if (gridY < 0 || gridY >= gridHeight || gridX < 0 || gridX >= gridWidth) {
            return false;
        }
        return grid[gridY][gridX] == 1;
    }
    
    public int[][] getGrid() {
        return grid;
    }
    
    public void addDecoration(int x, int y, String key) {
        // Check if decoration is on path - don't add decorations on path
        int gridX = x / TowerDefenseGame.TILE_SIZE;
        int gridY = (y - 100) / TowerDefenseGame.TILE_SIZE; // Account for UI offset
        
        // Only add decoration if it's not on the path
        if (!isPath(gridX, gridY)) {
            decorations.add(new Decoration(x, y, key));
        }
    }
    
    public void addDecorationAtGrid(int gridX, int gridY, String key) {
        // Grid koordinatını pixel'e çevir
        int pixelX = gridX * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2;
        int pixelY = gridY * TowerDefenseGame.TILE_SIZE + TowerDefenseGame.TILE_SIZE / 2 + 100; // UI offset
        addDecoration(pixelX, pixelY, key);
    }
    
    public void addObstacle(int gridX, int gridY, String assetKey) {
        // Add obstacle that blocks tower placement (only if not on path)
        if (!isPath(gridX, gridY)) {
            obstacles.add(new Obstacle(gridX, gridY, assetKey));
        }
    }
    
    public boolean isObstacle(int gridX, int gridY) {
        for (Obstacle obs : obstacles) {
            if (obs.gridX == gridX && obs.gridY == gridY) {
                return true;
            }
        }
        return false;
    }
    
    public List<Decoration> getDecorations() { return decorations; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public String getName() { return name; }
    public List<Point> getPath() { return path; }
    public int getWaves() { return waves; }
    public int getSpawnInterval() { return spawnInterval; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getPathColor() { return pathColor; }
    public String getBackgroundAssetKey() { return backgroundAssetKey; }
    
    public static List<LevelData> createLevels() {
        List<LevelData> levels = new ArrayList<>();
        
        // Level 1 - Simple S-curve path (EASY - Beginner friendly)
        // Grid: 1 = path, 0 = empty (tower placeable)
        // Screen: 1200x700, TILE_SIZE: 64, Grid: 18x9 (accounting for UI offset ~100px)
        // Path: Left -> Right -> Up -> Right (simple S-curve)
        // Path starts from left edge (column 0) and exits from right edge (column 17)
        String grid1 = 
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "1 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0";
        
        LevelData lvl1 = new LevelData("Level 1", grid1, 5, 60, 
            new Color(90, 200, 90),  // Bright green background (fallback)
            new Color(150, 120, 80), // Brown path
            "level1_bg"); // Use tile024 as background pattern
        
        // Grid: 18 sütun (0-17), 9 satır (0-8)
        // Decoration: lvl1.addDecorationAtGrid(sütun, satır, "dec1"); // Görsel, tower engellemez
        // Obstacle: lvl1.addObstacle(sütun, satır, "obstacle"); // Tower engelleyici
        // Asset seçimi: "dec1"-"dec5" = görsel, "obstacle" = engelleyici
        
        levels.add(lvl1);
        
        // Level 2 - Restored to previous path
        String grid2 = 
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 0 0\n" +
            "0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 1 0 0\n" +
            "0 1 1 1 1 0 0 0 0 0 0 0 0 0 0 1 0 0\n" +
            "0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0\n" +
            "0 1 1 1 1 1 0 0 0 0 0 0 1 1 1 1 0 0\n" +
            "0 0 0 0 0 1 0 0 0 0 0 0 1 0 0 0 0 0\n" +
            "0 0 0 0 0 1 0 0 0 0 0 0 1 1 1 1 1 1";
        
        // Level 2 - Green theme - MEDIUM difficulty
        LevelData lvl2 = new LevelData("Level 2", grid2, 8, 45,
            new Color(90, 200, 90),  // Green background (fallback)
            new Color(150, 120, 80), // Brown path (same as Level 1)
            "level2_bg"); // Use tile024 as background pattern
        
        // lvl2.addDecorationAtGrid(sütun, satır, "dec1");
        // lvl2.addObstacle(sütun, satır, "obstacle");
        
        levels.add(lvl2);
        
        // Level 3 - EXTREME complex meandering path (VERY HARD - Extremely challenging)
        // Path: Complex pattern matching reference image exactly
        // Pattern: Left -> Right -> Sharp Down -> Right -> Up -> Right -> Down -> Right (exit)
        // Creates an extremely challenging path that maximizes enemy travel distance
        // This path has multiple sharp turns and loops - requires perfect strategic tower placement
        // Similar to reference image with winding path through grid area
        // Path starts from left edge (column 0) and exits from right edge (column 17)
        // Row 0: Left to right across top (entrance from left, column 0-17)
        // Row 1-4: Sharp down turn at right edge (column 17, rows 1-4)
        // Row 4: Right turn (column 12-17, row 4)
        // Row 5-6: Up turn (column 12, rows 5-6)
        // Row 6: Right turn (column 12-17, row 6)
        // Row 7-8: Down turn (column 17, rows 7-8)
        // Row 8: Right exit (column 17, row 8) - path continues to edge
        // Updated Level 3 path: single-width snake with many turns; starts at left edge and exits bottom-right
        String grid3 = 
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1\n" +
            "0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1\n" +
            "0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 1 1 1 1 1 1 1 1 1 1 0 0 0 0\n" +
            "0 1 1 1 1 1 1 1 1 1 1 1 1 0 0 0 0 0\n" +
            "0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0\n" +
            "0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 0\n" +
            "0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 1 1 1";
        
        // Level 3 - Green theme - EXTREME difficulty
        LevelData lvl3 = new LevelData("Level 3", grid3, 12, 35,
            new Color(90, 200, 90),  // Green background (fallback)
            new Color(150, 120, 80), // Brown path (same as Level 1)
            "level3_bg"); // Use tile024 as background pattern
        
        // lvl3.addDecorationAtGrid(sütun, satır, "dec1");
        // lvl3.addObstacle(sütun, satır, "obstacle");
        // Obstacles (tile136) placed right next to the path edges (3 in a row each)
        // Near top-right segment (adjacent to path endpoint on row 2)
        lvl3.addObstacle(14, 2, "obstacle136");
        lvl3.addObstacle(15, 2, "obstacle136");
        lvl3.addObstacle(16, 2, "obstacle136");
        // Right edge of middle horizontal stretch (row 5)
        lvl3.addObstacle(14, 5, "obstacle136");
        lvl3.addObstacle(15, 5, "obstacle136");
        lvl3.addObstacle(16, 5, "obstacle136");
        // Left edge of lower horizontal stretch (row 9)
        lvl3.addObstacle(2, 9, "obstacle136");
        lvl3.addObstacle(3, 9, "obstacle136");
        lvl3.addObstacle(4, 9, "obstacle136");
        
        levels.add(lvl3);
        
        return levels;
    }

    private static void addDecorationCluster(LevelData lvl, int x, int y) {
        // Create a natural-looking cluster with variety
        lvl.addDecoration(x, y, "dec" + ((x+y)%5 + 1));
        lvl.addDecoration(x + 25, y + 18, "dec" + ((x+y+1)%5 + 1));
        lvl.addDecoration(x - 18, y + 28, "dec" + ((x+y+2)%5 + 1));
        lvl.addDecoration(x + 12, y - 22, "dec" + ((x+y+3)%5 + 1));
        lvl.addDecoration(x - 10, y + 8, "dec" + ((x+y+4)%5 + 1));
        lvl.addDecoration(x + 30, y + 5, "dec" + ((x+y+5)%5 + 1));
    }
}


