import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * High Scores Panel
 */
public class HighScoresPanel extends JPanel {
    private TowerDefenseGame game;
    private JPanel scoresPanel;
    
    public HighScoresPanel(TowerDefenseGame game) {
        this.game = game;
        setLayout(null);
        // Green background to match game levels
        setBackground(new Color(90, 200, 90));
        
        initComponents();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 52));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 50, 600, 70);
        add(titleLabel);
        
        // Scores panel
        scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBackground(new Color(90, 200, 90));
        
        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setBounds(150, 140, 900, 450);
        scrollPane.setBackground(new Color(90, 200, 90));
        scrollPane.getViewport().setBackground(new Color(90, 200, 90));
        scrollPane.setBorder(null);
        add(scrollPane);
        
        // Back button
        JButton backBtn = createStyledButton("BACK", new Color(128, 128, 128));
        backBtn.setBounds(530, 610, 140, 40);
        backBtn.addActionListener(e -> game.showPanel("MENU"));
        add(backBtn);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
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
        scoresPanel.removeAll();
        
        List<AuthSystem.ScoreEntry> scores = game.getAuthSystem().getHighScores(10);
        
        if (scores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No scores yet. Play a game to see your scores here!");
            noScoresLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            noScoresLabel.setForeground(Color.WHITE);
            noScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoresPanel.add(Box.createVerticalStrut(150));
            scoresPanel.add(noScoresLabel);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                AuthSystem.ScoreEntry entry = scores.get(i);
                
                String scoreText = String.format("%d. %s: %d pts (Lvl %d) - %s", 
                    i + 1, entry.username, entry.score, entry.level, entry.date);
                
                JLabel scoreLabel = new JLabel(scoreText);
                scoreLabel.setFont(new Font("Arial", Font.PLAIN, 22));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                scoresPanel.add(Box.createVerticalStrut(10));
                scoresPanel.add(scoreLabel);
            }
        }
        
        scoresPanel.revalidate();
        scoresPanel.repaint();
    }
}





