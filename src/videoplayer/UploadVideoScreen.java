package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;

/**
 * Upload Video Screen - Light modern theme
 */
public class UploadVideoScreen extends JFrame {
    private static final Color BG = LoginScreen.BG_WHITE;
    private static final Color SURFACE = LoginScreen.SURFACE;
    private static final Color BORDER = LoginScreen.BORDER;
    private static final Color BORDER_FOCUS = LoginScreen.BORDER_FOCUS;
    private static final Color ACCENT = LoginScreen.ACCENT;
    private static final Color ACCENT2 = LoginScreen.ACCENT2;
    private static final Color ACCENT_LIGHT = LoginScreen.ACCENT_LIGHT;
    private static final Color GREEN = LoginScreen.GREEN_BTN;
    private static final Color BLUE = LoginScreen.BLUE_BTN;
    private static final Color ROSE = LoginScreen.ROSE_BTN;
    private static final Color TEXT_PRIMARY = LoginScreen.TEXT_PRIMARY;
    private static final Color TEXT_SECONDARY = LoginScreen.TEXT_SECONDARY;
    private static final Color TEXT_MUTED = LoginScreen.TEXT_MUTED;
    private static final Color NAV_BG = LoginScreen.NAV_BG;
    private static final Color NAV_BORDER = LoginScreen.NAV_BORDER;

    private JTextField titleField;
    private JComboBox<String> categoryComboBox;
    private JTextField filePathField;
    private File selectedFile;

    public UploadVideoScreen() {
        initComponents();
        loadCategories();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("VMS - Upload Video");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.add(createNavBar(), BorderLayout.NORTH);

        // Center: form card
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);

        JPanel formCard = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(3, 4, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 20, 20);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 5, 20, 20);
            }
        };
        formCard.setLayout(new GridBagLayout());
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(560, 480));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 30, 8, 30);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridwidth = 2;

        // Header with icon
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setOpaque(false);
        JLabel uploadIcon = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                LoginScreen.drawUploadIcon(g2, 4, 4, 22, ACCENT);
            }
        };
        uploadIcon.setPreferredSize(new Dimension(30, 30));
        headerPanel.add(uploadIcon);
        JLabel headerLabel = new JLabel("Upload Video");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(headerLabel);

        gc.gridy = 0;
        gc.insets = new Insets(24, 30, 4, 30);
        formCard.add(headerPanel, gc);

        JLabel subtitle = new JLabel("Share your video with the community");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridy = 1;
        gc.insets = new Insets(0, 30, 18, 30);
        formCard.add(subtitle, gc);

        // Title field
        gc.gridy = 2;
        gc.insets = new Insets(4, 30, 2, 30);
        formCard.add(createLabel("Video Title"), gc);
        gc.gridy = 3;
        gc.insets = new Insets(2, 30, 10, 30);
        titleField = createTextField("Enter video title");
        formCard.add(titleField, gc);

        // Category
        gc.gridy = 4;
        gc.insets = new Insets(4, 30, 2, 30);
        formCard.add(createLabel("Category"), gc);
        gc.gridy = 5;
        gc.insets = new Insets(2, 30, 10, 30);
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(0, 42));
        categoryComboBox.setBackground(SURFACE);
        categoryComboBox.setForeground(TEXT_PRIMARY);
        formCard.add(categoryComboBox, gc);

        // File picker
        gc.gridy = 6;
        gc.insets = new Insets(4, 30, 2, 30);
        formCard.add(createLabel("Video File"), gc);
        gc.gridy = 7;
        gc.insets = new Insets(2, 30, 10, 30);
        JPanel filePanel = new JPanel(new BorderLayout(8, 0));
        filePanel.setOpaque(false);
        filePathField = createTextField("No file selected");
        filePathField.setEditable(false);
        filePanel.add(filePathField, BorderLayout.CENTER);

        JButton browseBtn = new JButton("Browse") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 30)
                    : new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                int ix = 8, iy = (getHeight()-12)/2;
                LoginScreen.drawBrowseIcon(g2, ix, iy, 12, ACCENT);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 24, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        browseBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        browseBtn.setPreferredSize(new Dimension(100, 42));
        browseBtn.setFocusPainted(false); browseBtn.setBorderPainted(false); browseBtn.setContentAreaFilled(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseBtn.addActionListener(e -> browseFile());
        filePanel.add(browseBtn, BorderLayout.EAST);
        formCard.add(filePanel, gc);

        // Buttons
        gc.gridy = 8;
        gc.insets = new Insets(20, 30, 24, 30);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);

        JButton uploadBtn = LoginScreen.createGradientButton("Upload Video", ACCENT, ACCENT2);
        uploadBtn.setPreferredSize(new Dimension(180, 44));
        uploadBtn.addActionListener(e -> uploadVideo());
        btnPanel.add(uploadBtn);

        JButton cancelBtn = LoginScreen.createOutlineButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 44));
        cancelBtn.addActionListener(e -> { dispose(); new AllVideosScreen(); });
        btnPanel.add(cancelBtn);

        formCard.add(btnPanel, gc);
        center.add(formCard);
        mainPanel.add(center, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBackground(SURFACE);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        // Placeholder
        field.setText(placeholder);
        field.setForeground(TEXT_MUTED);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText(""); field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 2, true),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder); field.setForeground(TEXT_MUTED);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1, true),
                    BorderFactory.createEmptyBorder(8, 14, 8, 14)));
            }
        });
        return field;
    }

    // ====== NAV BAR ======
    private JPanel createNavBar() {
        JPanel nav = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(NAV_BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        nav.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel brandIcon = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 2, ACCENT, 28, 24, ACCENT2);
                g2.setPaint(gp);
                g2.fillRoundRect(2, 4, 26, 20, 6, 6);
                g2.setColor(Color.WHITE);
                int[] xp = {12,12,22}; int[] yp = {10,20,15};
                g2.fillPolygon(xp, yp, 3);
            }
        };
        brandIcon.setPreferredSize(new Dimension(30, 28));
        left.add(brandIcon);

        JLabel brand = new JLabel("VMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(TEXT_PRIMARY);
        left.add(brand);
        left.add(Box.createHorizontalStrut(20));

        JButton homeBtn = createNavBtn("Home", false, 0);
        homeBtn.addActionListener(e -> { dispose(); new AllVideosScreen(); });
        left.add(homeBtn);
        JButton myBtn = createNavBtn("My Videos", false, 1);
        myBtn.addActionListener(e -> { dispose(); new MyDashboardScreen(); });
        left.add(myBtn);
        left.add(createNavBtn("Upload", true, 2));

        Session session = Session.getInstance();
        if (session.getRole() != null && session.getRole().equalsIgnoreCase("ADMIN")) {
            JButton adminBtn = createNavBtn("Admin", false, 3);
            adminBtn.addActionListener(e -> { dispose(); new AdminDashboard(); });
            left.add(adminBtn);
        }
        nav.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel avatar = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, 30, 30, ACCENT2);
                g2.setPaint(gp);
                g2.fillOval(2, 2, 28, 28);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String init = session.getUsername().substring(0,1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init, (32-fm.stringWidth(init))/2, (32+fm.getAscent()-fm.getDescent())/2);
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        right.add(avatar);
        JLabel userName = new JLabel(session.getUsername());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(TEXT_PRIMARY);
        right.add(userName);
        right.add(createLogoutBtn());
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JButton createNavBtn(String text, boolean active, int iconType) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color txtC;
                if (active) {
                    g2.setColor(ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    txtC = ACCENT;
                } else {
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(241,245,249));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                    txtC = TEXT_SECONDARY;
                }
                int ix = 10, iy = (getHeight()-14)/2;
                switch(iconType) {
                    case 0: LoginScreen.drawHomeIcon(g2, ix, iy, 14, txtC); break;
                    case 1: LoginScreen.drawVideoIcon(g2, ix, iy, 14, txtC); break;
                    case 2: LoginScreen.drawUploadIcon(g2, ix, iy, 14, txtC); break;
                    case 3: LoginScreen.drawAdminIcon(g2, ix, iy, 14, txtC); break;
                }
                g2.setColor(txtC);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 28, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        int w = btn.getFontMetrics(btn.getFont()).stringWidth(text) + 42;
        btn.setPreferredSize(new Dimension(w, 34));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createLogoutBtn() {
        JButton btn = new JButton("Logout") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(225,29,72,15) : new Color(0,0,0,0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ROSE);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 8, 8);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { Session.getInstance().logout(); dispose(); new LoginScreen(); });
        return btn;
    }

    // ====== ACTIONS ======
    private void loadCategories() {
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM categories_video ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) categoryComboBox.addItem(rs.getString("name"));
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void browseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Video Files", "mp4", "avi", "mkv", "mov", "wmv"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            filePathField.setText(selectedFile.getName());
            filePathField.setForeground(TEXT_PRIMARY);
        }
    }

    private void uploadVideo() {
        String title = titleField.getText().trim();
        if (title.isEmpty() || title.equals("Enter video title")) {
            JOptionPane.showMessageDialog(this, "Please enter a video title");
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a video file");
            return;
        }
        if (categoryComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a category");
            return;
        }
        try {
            java.sql.Connection conn = Connection.getConnection();
            // Get category ID
            PreparedStatement catStmt = conn.prepareStatement("SELECT id FROM categories_video WHERE name = ?");
            catStmt.setString(1, (String) categoryComboBox.getSelectedItem());
            ResultSet catRs = catStmt.executeQuery();
            int categoryId = 0;
            if (catRs.next()) categoryId = catRs.getInt("id");
            catRs.close(); catStmt.close();

            // Get next video id
            PreparedStatement idStmt = conn.prepareStatement("SELECT NVL(MAX(id), 0) + 1 AS next_id FROM videos_video");
            ResultSet idRs = idStmt.executeQuery();
            int nextId = 1;
            if (idRs.next()) nextId = idRs.getInt("next_id");
            idRs.close(); idStmt.close();

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO videos_video (id, title, category_id, user_id, video_data, video_path, upload_date, views, likes, downloads) " +
                "VALUES (?, ?, ?, ?, EMPTY_BLOB(), ?, SYSDATE, 0, 0, 0)");
            stmt.setInt(1, nextId);
            stmt.setString(2, title);
            stmt.setInt(3, categoryId);
            stmt.setInt(4, Session.getInstance().getUserId());

            // Copy video file to local videos directory
            File videosDir = new File(LoginScreen.VIDEOS_DIR);
            if (!videosDir.exists()) videosDir.mkdirs();
            String ext = selectedFile.getName().contains(".") 
                ? selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".")) 
                : ".mp4";
            String storedName = "video_" + nextId + "_" + System.currentTimeMillis() + ext;
            File dest = new File(videosDir, storedName);
            try (FileInputStream fis = new FileInputStream(selectedFile);
                 FileOutputStream fos = new FileOutputStream(dest)) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = fis.read(buf)) != -1) fos.write(buf, 0, n);
            }
            // Store file name in video_path column
            stmt.setString(5, storedName);
            stmt.executeUpdate();
            stmt.close(); conn.close();

            JOptionPane.showMessageDialog(this, "Video uploaded successfully!");
            dispose();
            new AllVideosScreen();
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error uploading video: " + ex.getMessage());
        }
    }
}
