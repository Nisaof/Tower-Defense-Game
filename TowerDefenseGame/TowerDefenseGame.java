import javax.swing.*;
import java.awt.*;

/**
 * Main Tower Defense Game Application
 * CSE212 - Software Development Methodologies
 * Yeditepe University - Fall 2025
 */
public class TowerDefenseGame {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AuthSystem authSystem;
    private AssetManager assetManager;
    
    // Panels
    private MainMenuPanel mainMenuPanel;
    private LoginPanel loginPanel;
    private LevelSelectPanel levelSelectPanel;
    private GamePanel gamePanel;
    private HighScoresPanel highScoresPanel;
    
    public static final int SCREEN_WIDTH = 1200;
    public static final int SCREEN_HEIGHT = 700;
    public static final int TILE_SIZE = 64;
    
    public TowerDefenseGame() {
        authSystem = new AuthSystem();
        assetManager = new AssetManager();
        
        initializeFrame();
        initializePanels();
        
        frame.setVisible(true);
    }
    
    private void initializeFrame() {
        frame = new JFrame("Tower Defense - CSE212 Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.add(mainPanel);
    }
    
    private void initializePanels() {
        mainMenuPanel = new MainMenuPanel(this);
        loginPanel = new LoginPanel(this);
        levelSelectPanel = new LevelSelectPanel(this);
        highScoresPanel = new HighScoresPanel(this);
        
        mainPanel.add(mainMenuPanel, "MENU");
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(levelSelectPanel, "LEVEL_SELECT");
        mainPanel.add(highScoresPanel, "HIGHSCORES");
        
        showPanel("MENU");
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        
        // Refresh panels when showing them
        if (panelName.equals("MENU")) {
            mainMenuPanel.refresh();
        } else if (panelName.equals("HIGHSCORES")) {
            highScoresPanel.refresh();
        }
    }
    
    public void startGame(int level) {
        // CRITICAL: Check if user is logged in before starting game
        if (!authSystem.isLoggedIn()) {
            showPanel("LOGIN");
            return;
        }
        
        // Remove old game panel if exists
        if (gamePanel != null) {
            mainPanel.remove(gamePanel);
        }
        
        gamePanel = new GamePanel(this, level);
        mainPanel.add(gamePanel, "GAME");
        showPanel("GAME");
        gamePanel.startGame();
    }
    
    public AuthSystem getAuthSystem() {
        return authSystem;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TowerDefenseGame();
        });
    }
}


