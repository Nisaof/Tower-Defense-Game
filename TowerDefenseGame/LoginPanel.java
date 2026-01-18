import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login and Registration Panel
 */
public class LoginPanel extends JPanel {
    private TowerDefenseGame game;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    
    public LoginPanel(TowerDefenseGame game) {
        this.game = game;
        setLayout(null);
        // Match menu background color (#21421e)
        setBackground(new Color(0x21, 0x42, 0x1E));
        
        initComponents();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("LOGIN / REGISTER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(300, 100, 600, 60);
        add(titleLabel);
        
        // Username label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(400, 220, 150, 30);
        add(usernameLabel);
        
        // Username field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameField.setBounds(450, 250, 300, 40);
        add(usernameField);
        
        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(400, 300, 150, 30);
        add(passwordLabel);
        
        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setBounds(450, 330, 300, 40);
        add(passwordField);
        
        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(400, 385, 400, 25);
        add(messageLabel);
        
        // Login button
        JButton loginBtn = createStyledButton("LOGIN", new Color(76, 175, 80));
        loginBtn.setBounds(450, 420, 140, 40);
        loginBtn.addActionListener(e -> handleLogin());
        add(loginBtn);
        
        // Register button
        JButton registerBtn = createStyledButton("REGISTER", new Color(33, 150, 243));
        registerBtn.setBounds(610, 420, 140, 40);
        registerBtn.addActionListener(e -> handleRegister());
        add(registerBtn);
        
        // Back button
        JButton backBtn = createStyledButton("BACK", new Color(128, 128, 128));
        backBtn.setBounds(530, 480, 140, 40);
        backBtn.addActionListener(e -> {
            clearFields();
            game.showPanel("MENU");
        });
        add(backBtn);
        
        // Enter key support
        passwordField.addActionListener(e -> handleLogin());
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
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        String result = game.getAuthSystem().login(username, password);
        
        if (result.equals("SUCCESS")) {
            messageLabel.setText("Login successful!");
            messageLabel.setForeground(new Color(76, 175, 80));
            clearFields();
            
            Timer timer = new Timer(500, e -> game.showPanel("LEVEL_SELECT"));
            timer.setRepeats(false);
            timer.start();
        } else {
            messageLabel.setText(result);
            messageLabel.setForeground(Color.RED);
        }
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        String result = game.getAuthSystem().register(username, password);
        
        if (result.equals("SUCCESS")) {
            // Auto-login after registration
            game.getAuthSystem().login(username, password);
            messageLabel.setText("Registration successful!");
            messageLabel.setForeground(new Color(76, 175, 80));
            clearFields();
            
            Timer timer = new Timer(500, e -> game.showPanel("LEVEL_SELECT"));
            timer.setRepeats(false);
            timer.start();
        } else {
            messageLabel.setText(result);
            messageLabel.setForeground(Color.RED);
        }
    }
    
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }
}


