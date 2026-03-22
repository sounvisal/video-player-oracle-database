package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import javax.swing.*;

/**
 * Register Screen - Modern light eye-friendly UI
 */
public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, loginButton;
    private JLabel messageLabel;
    
    private static final Color BG_WHITE = LoginScreen.BG_WHITE;
    private static final Color SURFACE = LoginScreen.SURFACE;
    private static final Color BORDER = LoginScreen.BORDER;
    private static final Color ACCENT = LoginScreen.ACCENT;
    private static final Color ACCENT2 = LoginScreen.ACCENT2;
    private static final Color TEXT_PRIMARY = LoginScreen.TEXT_PRIMARY;
    private static final Color TEXT_SECONDARY = LoginScreen.TEXT_SECONDARY;
    private static final Color TEXT_MUTED = LoginScreen.TEXT_MUTED;
    private static final Color SUCCESS = LoginScreen.SUCCESS;
    private static final Color ERROR = LoginScreen.ERROR;
    private static final Color BORDER_FOCUS = LoginScreen.BORDER_FOCUS;
    
    public RegisterScreen() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("VMS - Create Account");
        setSize(960, 620);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // ====== LEFT PANEL ======
        JPanel leftPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,15));
                g2.fillOval(-60, -60, 280, 280);
                g2.setColor(new Color(255,255,255,10));
                g2.fillOval(getWidth()-160, getHeight()-200, 300, 300);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.insets = new Insets(8, 40, 8, 40);
        
        // Plus icon
        JLabel iconLabel = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = 72, cx = (getWidth()-s)/2, cy = (getHeight()-s)/2;
                g2.setColor(new Color(255,255,255,25));
                g2.fillOval(cx-10, cy-10, s+20, s+20);
                g2.setColor(new Color(255,255,255,40));
                g2.fillOval(cx, cy, s, s);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx+22, cy+36, cx+50, cy+36);
                g2.drawLine(cx+36, cy+22, cx+36, cy+50);
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
                "Create your account and start managing your video collection today!</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lc.gridy = 3; lc.insets = new Insets(5, 40, 20, 40);
        leftPanel.add(desc, lc);
        
        // ====== RIGHT PANEL ======
        JPanel rightPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                g.setColor(BG_WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        gc.gridy = 0; gc.insets = new Insets(25, 55, 3, 55);
        rightPanel.add(title, gc);
        
        JLabel sub = new JLabel("Fill in your details to get started");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        gc.gridy = 1; gc.insets = new Insets(0, 55, 18, 55);
        rightPanel.add(sub, gc);
        
        rightPanel.add(makeLabel("Username"), setGbc(gc, 2, new Insets(5,55,3,55)));
        usernameField = createField();
        rightPanel.add(usernameField, setGbc(gc, 3, new Insets(0,55,8,55)));
        
        rightPanel.add(makeLabel("Password"), setGbc(gc, 4, new Insets(5,55,3,55)));
        passwordField = new JPasswordField(20); styleField(passwordField);
        rightPanel.add(passwordField, setGbc(gc, 5, new Insets(0,55,8,55)));
        
        rightPanel.add(makeLabel("Confirm Password"), setGbc(gc, 6, new Insets(5,55,3,55)));
        confirmPasswordField = new JPasswordField(20); styleField(confirmPasswordField);
        rightPanel.add(confirmPasswordField, setGbc(gc, 7, new Insets(0,55,5,55)));
        
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(ERROR);
        rightPanel.add(messageLabel, setGbc(gc, 8, new Insets(0,55,5,55)));
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        btnPanel.setOpaque(false);
        registerButton = LoginScreen.createGradientButton("Register", ACCENT, ACCENT2);
        registerButton.addActionListener(new RegisterButtonListener());
        btnPanel.add(registerButton);
        loginButton = LoginScreen.createOutlineButton("Sign In");
        loginButton.addActionListener(e -> { dispose(); new LoginScreen(); });
        btnPanel.add(loginButton);
        rightPanel.add(btnPanel, setGbc(gc, 9, new Insets(12,55,12,55)));
        
        JLabel footer = new JLabel("\u00A9 2026 VMS  -  All Rights Reserved");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(TEXT_MUTED);
        rightPanel.add(footer, setGbc(gc, 10, new Insets(15,55,20,55)));
        
        KeyAdapter enter = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) registerButton.doClick();
            }
        };
        usernameField.addKeyListener(enter);
        passwordField.addKeyListener(enter);
        confirmPasswordField.addKeyListener(enter);
        
        mainPanel.add(leftPanel); mainPanel.add(rightPanel);
        setContentPane(mainPanel);
    }
    
    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(TEXT_SECONDARY);
        return l;
    }
    private GridBagConstraints setGbc(GridBagConstraints gc, int y, Insets i) {
        gc.gridy = y; gc.insets = i; return gc;
    }
    private JTextField createField() { JTextField f = new JTextField(20); styleField(f); return f; }
    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(280, 42));
        f.setBackground(SURFACE);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(7,14,7,14)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 2, true),
                    BorderFactory.createEmptyBorder(6,13,6,13)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1, true),
                    BorderFactory.createEmptyBorder(7,14,7,14)));
            }
        });
    }
    
    private class RegisterButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) { showError("All fields are required"); return; }
            if (username.length() < 3) { showError("Username must be at least 3 characters"); return; }
            if (password.length() < 4) { showError("Password must be at least 4 characters"); return; }
            if (!password.equals(confirm)) { showError("Passwords do not match"); confirmPasswordField.setText(""); return; }
            if (registerUser(username, password)) {
                messageLabel.setText("Registration successful! Redirecting...");
                messageLabel.setForeground(SUCCESS);
                Timer t = new Timer(1000, ev -> { dispose(); new LoginScreen(); });
                t.setRepeats(false); t.start();
            }
        }
        private void showError(String msg) { messageLabel.setText(msg); messageLabel.setForeground(ERROR); }
        private boolean registerUser(String username, String password) {
            try {
                java.sql.Connection conn = Connection.getConnection();
                PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM users_video WHERE username = ?");
                chk.setString(1, username);
                ResultSet rs = chk.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) { showError("Username already exists"); rs.close(); chk.close(); conn.close(); return false; }
                rs.close(); chk.close();
                PreparedStatement mx = conn.prepareStatement("SELECT NVL(MAX(user_id), 0) + 1 FROM users_video");
                ResultSet mr = mx.executeQuery();
                int nextId = 1; if (mr.next()) nextId = mr.getInt(1); mr.close(); mx.close();
                PreparedStatement ins = conn.prepareStatement("INSERT INTO users_video (user_id, username, password, role) VALUES (?, ?, ?, 'USER')");
                ins.setInt(1, nextId); ins.setString(2, username); ins.setString(3, password);
                int rows = ins.executeUpdate(); ins.close(); conn.close();
                return rows > 0;
            } catch (SQLException ex) { showError("Database error: " + ex.getMessage()); return false; }
        }
    }
}
