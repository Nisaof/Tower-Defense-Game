import org.json.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Authentication System
 * Handles user registration, login, and score persistence
 * Uses CSV format (TXT file) for user data storage as per requirements
 */
public class AuthSystem {
    private static final String USERS_FILE = "users_data.txt"; // Changed to TXT format (CSV)
    private static final String SCORES_FILE = "game_scores.json";
    
    private Map<String, UserData> users; // Changed from JSONObject to Map
    private String currentUser;
    
    private static class UserData {
        String password;
        String created;
        
        UserData(String password, String created) {
            this.password = password;
            this.created = created;
        }
    }
    
    public AuthSystem() {
        loadUsers();
        currentUser = null;
        // Migrate old JSON file if exists
        migrateFromJSON();
    }
    
    private void loadUsers() {
        users = new HashMap<>();
        try {
            File file = new File(USERS_FILE);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue; // Skip empty lines and comments
                        
                        // CSV format: username,password,created_timestamp
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            String username = parts[0].trim();
                            String password = parts[1].trim();
                            String created = parts.length >= 3 ? parts[2].trim() : getCurrentDateTime();
                            users.put(username, new UserData(password, created));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            users = new HashMap<>();
        }
    }
    
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("# User data file - Format: username,password,created_timestamp");
            for (Map.Entry<String, UserData> entry : users.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue().password + "," + entry.getValue().created);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void migrateFromJSON() {
        File jsonFile = new File("users_data.json");
        if (jsonFile.exists() && users.isEmpty()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(jsonFile.toPath()));
                JSONObject jsonUsers = new JSONObject(content);
                
                for (String username : jsonUsers.keySet()) {
                    JSONObject userData = jsonUsers.getJSONObject(username);
                    String password = userData.getString("password");
                    String created;
                    try {
                        created = userData.getString("created");
                    } catch (Exception e) {
                        created = getCurrentDateTime();
                    }
                    users.put(username, new UserData(password, created));
                }
                
                saveUsers(); // Save to new TXT format
                System.out.println("Migrated users from JSON to TXT format");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public String register(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return "Username and password cannot be empty";
        }
        
        if (username.length() < 3 || password.length() < 3) {
            return "Username and password must be at least 3 characters";
        }
        
        // Check for comma in username/password (CSV delimiter)
        if (username.contains(",") || password.contains(",")) {
            return "Username and password cannot contain commas";
        }
        
        if (users.containsKey(username)) {
            return "Username already exists";
        }
        
        try {
            users.put(username, new UserData(password, getCurrentDateTime()));
            saveUsers();
            
            return "SUCCESS";
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }
    
    public String login(String username, String password) {
        if (!users.containsKey(username)) {
            return "Username not found";
        }
        
        try {
            UserData userData = users.get(username);
            if (!userData.password.equals(password)) {
                return "Incorrect password";
            }
            
            currentUser = username;
            return "SUCCESS";
        } catch (Exception e) {
            return "Login failed: " + e.getMessage();
        }
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public void saveScore(int level, int score, int enemiesKilled, int health, int moneySpent) {
        if (currentUser == null) return;
        
        try {
            JSONObject scores;
            File file = new File(SCORES_FILE);
            
            if (file.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                scores = new JSONObject(content);
            } else {
                scores = new JSONObject();
            }
            
            if (!scores.has(currentUser)) {
                scores.put(currentUser, new JSONArray());
            }
            
            JSONArray userScores = scores.getJSONArray(currentUser);
            JSONObject scoreData = new JSONObject();
            scoreData.put("date", getCurrentDate());
            scoreData.put("time", getCurrentTime());
            scoreData.put("level", level);
            scoreData.put("score", score);
            scoreData.put("enemies_killed", enemiesKilled);
            scoreData.put("health", health);
            scoreData.put("money_spent", moneySpent);
            
            userScores.put(scoreData);
            
            try (FileWriter writer = new FileWriter(SCORES_FILE)) {
                writer.write(scores.toString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<ScoreEntry> getHighScores(int limit) {
        List<ScoreEntry> scoresList = new ArrayList<>();
        
        try {
            File file = new File(SCORES_FILE);
            if (!file.exists()) return scoresList;
            
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONObject allScores = new JSONObject(content);
            
            for (String username : allScores.keySet()) {
                JSONArray userScores = allScores.getJSONArray(username);
                for (int i = 0; i < userScores.length(); i++) {
                    JSONObject scoreData = userScores.getJSONObject(i);
                    scoresList.add(new ScoreEntry(
                        username,
                        scoreData.getString("date"),
                        scoreData.getString("time"),
                        scoreData.getInt("level"),
                        scoreData.getInt("score"),
                        scoreData.getInt("enemies_killed"),
                        scoreData.getInt("health"),
                        scoreData.getInt("money_spent")
                    ));
                }
            }
            
            scoresList.sort((a, b) -> Integer.compare(b.score, a.score));
            
            if (scoresList.size() > limit) {
                return scoresList.subList(0, limit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return scoresList;
    }
    
    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    public static class ScoreEntry {
        public String username;
        public String date;
        public String time;
        public int level;
        public int score;
        public int enemiesKilled;
        public int health;
        public int moneySpent;
        
        public ScoreEntry(String username, String date, String time, int level, 
                         int score, int enemiesKilled, int health, int moneySpent) {
            this.username = username;
            this.date = date;
            this.time = time;
            this.level = level;
            this.score = score;
            this.enemiesKilled = enemiesKilled;
            this.health = health;
            this.moneySpent = moneySpent;
        }
    }
}


