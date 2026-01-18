import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Level Selection Panel
 */
public class LevelSelectPanel extends JPanel {
    private TowerDefenseGame game;
    
    public LevelSelectPanel(TowerDefenseGame game) {
        this.game = game;
        setLayout(null);
        setBackground(new Color(0x21, 0x42, 0x1E));
        
        initComponents();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("SELECT LEVEL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 80, 600, 80);
        add(titleLabel);
        
        // Level 1 button
        JButton level1Btn = createStyledButton("LEVEL 1", new Color(76, 175, 80));
        level1Btn.setBounds(500, 220, 200, 60);
        level1Btn.addActionListener(e -> {
            if (!game.getAuthSystem().isLoggedIn()) {
                game.showPanel("LOGIN");
            } else {
                game.startGame(1);
            }
        });
        add(level1Btn);
        
        // Level 2 button
        JButton level2Btn = createStyledButton("LEVEL 2", new Color(33, 150, 243));
        level2Btn.setBounds(500, 300, 200, 60);
        level2Btn.addActionListener(e -> {
            if (!game.getAuthSystem().isLoggedIn()) {
                game.showPanel("LOGIN");
            } else {
                game.startGame(2);
            }
        });
        add(level2Btn);
        
        // Level 3 button
        JButton level3Btn = createStyledButton("LEVEL 3", new Color(244, 67, 54));
        level3Btn.setBounds(500, 380, 200, 60);
        level3Btn.addActionListener(e -> {
            if (!game.getAuthSystem().isLoggedIn()) {
                game.showPanel("LOGIN");
            } else {
                game.startGame(3);
            }
        });
        add(level3Btn);
        
        // Back button
        JButton backBtn = createStyledButton("BACK", new Color(128, 128, 128));
        backBtn.setBounds(500, 480, 200, 50);
        backBtn.addActionListener(e -> game.showPanel("MENU"));
        add(backBtn);
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
}
