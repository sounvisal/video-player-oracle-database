package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import javax.swing.*;

/**
 * Login Screen - Modern light eye-friendly UI
 */
public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    
    // Modern Light Theme
    static final Color BG_WHITE = new Color(248, 250, 252);
    static final Color SURFACE = new Color(255, 255, 255);
    static final Color SURFACE_ALT = new Color(241, 245, 249);
    static final Color BORDER = new Color(226, 232, 240);
    static final Color BORDER_FOCUS = new Color(99, 102, 241);
    static final Color ACCENT = new Color(79, 70, 229);
    static final Color ACCENT2 = new Color(124, 58, 237);
    static final Color ACCENT_LIGHT = new Color(238, 242, 255);
    static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    static final Color TEXT_SECONDARY = new Color(71, 85, 105);
    static final Color TEXT_MUTED = new Color(148, 163, 184);
    static final Color SUCCESS = new Color(22, 163, 74);
    static final Color ERROR = new Color(220, 38, 38);
    static final Color NAV_BG = new Color(255, 255, 255);
    static final Color NAV_BORDER = new Color(226, 232, 240);
    static final Color GREEN_BTN = new Color(22, 163, 74);
    static final Color BLUE_BTN = new Color(37, 99, 235);
    static final Color ROSE_BTN = new Color(225, 29, 72);
    static final Color AMBER_BTN = new Color(217, 119, 6);
    static final Color TABLE_HEADER_BG = new Color(248, 250, 252);
    static final Color TABLE_ROW_ALT = new Color(248, 250, 252);
    
    // Video file storage directory
    static final String VIDEOS_DIR = System.getProperty("user.dir") + java.io.File.separator + "videos";
    
    public LoginScreen() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("VMS - Sign In");
        setSize(960, 580);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // ====== LEFT PANEL - gradient brand ======
        JPanel leftPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(79, 70, 229),
                    getWidth(), getHeight(), new Color(124, 58, 237));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 280, 280);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(getWidth()-160, getHeight()-200, 300, 300);
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillOval(100, getHeight()-120, 200, 200);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.insets = new Insets(8, 40, 8, 40);
        
        // Play icon drawn with Graphics2D
        JLabel iconLabel = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = 72, cx = (getWidth()-s)/2, cy = (getHeight()-s)/2;
                g2.setColor(new Color(255,255,255,25));
                g2.fillOval(cx-10, cy-10, s+20, s+20);
                g2.setColor(new Color(255,255,255,40));
                g2.fillOval(cx, cy, s, s);
                // Play triangle
                g2.setColor(Color.WHITE);
                int[] xp = {cx+27, cx+27, cx+52};
                int[] yp = {cy+20, cy+52, cy+36};
                g2.fillPolygon(xp, yp, 3);
            }
        };
        iconLabel.setPreferredSize(new Dimension(100, 95));
        lc.gridy = 0;
        leftPanel.add(iconLabel, lc);
        
        JLabel brand = new JLabel("VMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 44));
        brand.setForeground(Color.WHITE);
        lc.gridy = 1; lc.insets = new Insets(2, 40, 0, 40);
        leftPanel.add(brand, lc);
        
        JLabel brandSub = new JLabel("Video Management System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        brandSub.setForeground(new Color(255,255,255,200));
        lc.gridy = 2; lc.insets = new Insets(0, 40, 25, 40);
        leftPanel.add(brandSub, lc);
        
        JLabel desc = new JLabel("<html><div style='text-align:center;width:220px;color:rgba(255,255,255,0.7);'>" +
                "Stream, manage & share your video collection with a modern experience.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lc.gridy = 3; lc.insets = new Insets(5, 40, 20, 40);
        leftPanel.add(desc, lc);
        
        // ====== RIGHT PANEL - white form ======
        JPanel rightPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(BG_WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(TEXT_PRIMARY);
        gc.gridy = 0; gc.insets = new Insets(30, 55, 3, 55);
        rightPanel.add(welcome, gc);
        
        JLabel subtitle = new JLabel("Sign in to continue to your dashboard");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        gc.gridy = 1; gc.insets = new Insets(0, 55, 22, 55);
        rightPanel.add(subtitle, gc);
        
        // Username
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        uLabel.setForeground(TEXT_SECONDARY);
        gc.gridy = 2; gc.insets = new Insets(6, 55, 4, 55);
        rightPanel.add(uLabel, gc);
        
        usernameField = createModernField();
        gc.gridy = 3; gc.insets = new Insets(0, 55, 10, 55);
        rightPanel.add(usernameField, gc);
        
        // Password
        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pLabel.setForeground(TEXT_SECONDARY);
        gc.gridy = 4; gc.insets = new Insets(6, 55, 4, 55);
        rightPanel.add(pLabel, gc);
        
        passwordField = new JPasswordField(20);
        styleField(passwordField);
        gc.gridy = 5; gc.insets = new Insets(0, 55, 6, 55);
        rightPanel.add(passwordField, gc);
        
        // Message
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR);
        gc.gridy = 6; gc.insets = new Insets(0, 55, 6, 55);
        rightPanel.add(messageLabel, gc);
        
        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);
        
        loginButton = createGradientButton("Sign In", ACCENT, ACCENT2);
        loginButton.addActionListener(new LoginButtonListener());
        btnPanel.add(loginButton);
        
        registerButton = createOutlineButton("Register");
        registerButton.addActionListener(e -> { dispose(); new RegisterScreen(); });
        btnPanel.add(registerButton);
        
        gc.gridy = 7; gc.insets = new Insets(14, 55, 14, 55);
        rightPanel.add(btnPanel, gc);
        
        JLabel footer = new JLabel("\u00A9 2026 VMS  -  All Rights Reserved");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(TEXT_MUTED);
        gc.gridy = 8; gc.insets = new Insets(20, 55, 25, 55);
        rightPanel.add(footer, gc);
        
        // Enter key
        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loginButton.doClick();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        setContentPane(mainPanel);
    }
    
    private JTextField createModernField() {
        JTextField f = new JTextField(20);
        styleField(f);
        return f;
    }
    
    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(280, 44));
        f.setBackground(SURFACE);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 2, true),
                    BorderFactory.createEmptyBorder(7, 13, 7, 13)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1, true),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        });
    }
    
    // ========== Shared button factories (used by other screens) ==========
    static JButton createGradientButton(String text, Color c1, Color c2) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Subtle shadow
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(0, getHeight()-4, getWidth(), 4, 12, 12);
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2 - 1);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 44));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(79, 70, 229, 12));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.setColor(new Color(79, 70, 229));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 44));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    // ========== Custom icon drawing helpers ==========
    static void drawHomeIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[] hx = {x, x+size/2, x+size};
        int[] hy = {y+size/2, y, y+size/2};
        g2.drawPolyline(hx, hy, 3);
        g2.drawRect(x+size/5, y+size/2, size*3/5, size/2);
    }
    
    static void drawVideoIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawRoundRect(x, y+2, (int)(size*0.65), size-4, 3, 3);
        int[] tx = {x+(int)(size*0.68), x+size, x+(int)(size*0.68)};
        int[] ty = {y+size/4, y+size/2, y+size*3/4};
        g2.drawPolyline(tx, ty, 3);
    }
    
    static void drawUploadIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x+size/2, y+3, x+size/2, y+size-3);
        g2.drawLine(x+3, y+size/2, x+size-3, y+size/2);
    }
    
    static void drawAdminIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cs = size/2;
        g2.drawOval(x+size/4, y, cs, cs);
        g2.drawArc(x, y+cs, size, size, 30, 120);
    }
    
    static void drawPlayIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        int[] px = {x+3, x+3, x+size-2};
        int[] py = {y+1, y+size-1, y+size/2};
        g2.fillPolygon(px, py, 3);
    }
    
    static void drawDownloadIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x+size/2, y+2, x+size/2, y+size-5);
        g2.drawLine(x+size/4, y+size*2/3, x+size/2, y+size-4);
        g2.drawLine(x+size*3/4, y+size*2/3, x+size/2, y+size-4);
        g2.drawLine(x+2, y+size-2, x+size-2, y+size-2);
    }
    
    static void drawHeartIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        int w = size, h = size;
        GeneralPath path = new GeneralPath();
        path.moveTo(x+w/2, y+h);
        path.curveTo(x-w/4, y+h*0.55, x, y-h*0.1, x+w/2, y+h*0.35);
        path.curveTo(x+w, y-h*0.1, x+w+w/4, y+h*0.55, x+w/2, y+h);
        path.closePath();
        g2.fill(path);
    }
    
    static void drawEyeIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x+1, y+size/4, size-2, size/2);
        g2.fillOval(x+size/2-3, y+size/2-3, 6, 6);
    }
    
    static void drawEditIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x+3, y+size-3, x+size-3, y+3);
        g2.drawLine(x+size-3, y+3, x+size-6, y);
        g2.drawLine(x+3, y+size-3, x, y+size-6);
    }
    
    static void drawDeleteIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x+3, y+3, x+size-3, y+size-3);
        g2.drawLine(x+size-3, y+3, x+3, y+size-3);
    }
    
    static void drawRefreshIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(x+2, y+2, size-4, size-4, 45, 270);
        g2.drawLine(x+size/2, y+1, x+size-3, y+4);
    }
    
    static void drawGearIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(x+size/3, y+size/3, size/3, size/3);
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3;
            int x1 = x+size/2 + (int)(size/3 * Math.cos(angle));
            int y1 = y+size/2 + (int)(size/3 * Math.sin(angle));
            int x2 = x+size/2 + (int)(size*0.45 * Math.cos(angle));
            int y2 = y+size/2 + (int)(size*0.45 * Math.sin(angle));
            g2.drawLine(x1, y1, x2, y2);
        }
    }
    
    static void drawBrowseIcon(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawRoundRect(x+1, y+3, size-2, size-4, 3, 3);
        g2.drawLine(x+1, y+7, x+size/2, y+7);
        g2.fillRect(x+1, y+3, size/2-1, 4);
    }
    
    // ========== Login Logic ==========
    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Username and password are required");
                messageLabel.setForeground(ERROR);
                return;
            }
            
            if (validateLogin(username, password)) {
                messageLabel.setText("Login successful! Redirecting...");
                messageLabel.setForeground(SUCCESS);
                Timer t = new Timer(800, ev -> { dispose(); new AllVideosScreen(); });
                t.setRepeats(false); t.start();
            } else {
                messageLabel.setText("Invalid username or password");
                messageLabel.setForeground(ERROR);
                passwordField.setText("");
            }
        }
        
        private boolean validateLogin(String username, String password) {
            try {
                java.sql.Connection conn = Connection.getConnection();
                String sql = "SELECT user_id, role FROM users_video WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username); stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    if (role == null || role.isEmpty()) role = "USER";
                    Session.getInstance().login(userId, username, role);
                    rs.close(); stmt.close(); conn.close();
                    return true;
                }
                rs.close(); stmt.close(); conn.close();
            } catch (SQLException ex) {
                messageLabel.setText("Database error: " + ex.getMessage());
                messageLabel.setForeground(ERROR);
            }
            return false;
        }
    }
}
