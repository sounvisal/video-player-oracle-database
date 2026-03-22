package videoplayer;

/**
 * Session management class to store authenticated user information
 */
public class Session {
    private static Session instance;
    private int userId;
    private String username;
    private String role;
    
    private Session() {
    }
    
    /**
     * Get singleton instance of Session
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    /**
     * Initialize session with user data
     */
    public void login(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
    
    /**
     * Clear session on logout
     */
    public void logout() {
        this.userId = 0;
        this.username = null;
        this.role = null;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return username != null;
    }
    
    // Getters
    public int getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
        return role;
    }
}
