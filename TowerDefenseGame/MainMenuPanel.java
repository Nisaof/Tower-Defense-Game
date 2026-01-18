import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Menu Panel
 */
public class MainMenuPanel extends JPanel {
    private TowerDefenseGame game;
    private JLabel statusLabel;
    
    public MainMenuPanel(TowerDefenseGame game) {
        this.game = game;
        setLayout(null);
        // Unified background color (#21421e)
        setBackground(new Color(0x21, 0x42, 0x1E));
        
        initComponents();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("TOWER DEFENSE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 80, 600, 80);
        add(titleLabel);
        
        // Status label
        statusLabel = new JLabel("Not logged in", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(400, 180, 400, 30);
        add(statusLabel);
        
        // Login button
        JButton loginBtn = createStyledButton("LOGIN", new Color(33, 150, 243));
        loginBtn.setBounds(500, 250, 200, 50);
        loginBtn.addActionListener(e -> game.showPanel("LOGIN"));
        add(loginBtn);
        
        // Start button
        JButton startBtn = createStyledButton("START", new Color(76, 175, 80));
        startBtn.setBounds(500, 320, 200, 50);
        startBtn.addActionListener(e -> {
            if (!game.getAuthSystem().isLoggedIn()) {
                game.showPanel("LOGIN");
            } else {
                game.showPanel("LEVEL_SELECT");
            }
        });
        add(startBtn);
        
        // Highscores button
        JButton highscoresBtn = createStyledButton("HIGHSCORES", new Color(255, 152, 0));
        highscoresBtn.setBounds(500, 390, 200, 50);
        highscoresBtn.addActionListener(e -> game.showPanel("HIGHSCORES"));
        add(highscoresBtn);
        
        // Credits
        JLabel creditsLabel = new JLabel("CSE212 - Fall 2025 - Yeditepe University", SwingConstants.CENTER);
        creditsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        creditsLabel.setForeground(Color.WHITE);
        creditsLabel.setBounds(400, 600, 400, 30);
        add(creditsLabel);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    public void refresh() {
        if (game.getAuthSystem().isLoggedIn()) {
            statusLabel.setText("Logged in as: " + game.getAuthSystem().getCurrentUser());
            statusLabel.setForeground(new Color(76, 175, 80));
        } else {
            statusLabel.setText("Not logged in");
            statusLabel.setForeground(Color.RED);
        }
    }
}


