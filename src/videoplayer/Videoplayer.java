 
package videoplayer;

/**
 * Main entry point for Video Management System
 * Launches the login screen
 */
public class Videoplayer {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginScreen();
        });
    }
}
