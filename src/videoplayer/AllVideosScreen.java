package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * All Videos Screen - Light modern theme with card grid
 */
public class AllVideosScreen extends JFrame {
    private static final Color BG = LoginScreen.BG_WHITE;
    private static final Color SURFACE = LoginScreen.SURFACE;
    private static final Color BORDER = LoginScreen.BORDER;
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

    private JComboBox<String> categoryComboBox;
    private JTextField searchField;
    private JPanel videoGridPanel;
    private ArrayList<VideoData> allVideos;

    public AllVideosScreen() {
        allVideos = new ArrayList<>();
        initComponents();
        loadCategories();
        loadVideos();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("VMS - Browse Videos");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.add(createNavBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        content.add(createFilterBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        videoGridPanel = new JPanel(new GridLayout(0, 3, 22, 22));
        videoGridPanel.setBackground(BG);

        JScrollPane scroll = new JScrollPane(videoGridPanel);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        center.add(scroll, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);
        mainPanel.add(content, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // ====== NAV BAR ======
    private JPanel createNavBar() {
        JPanel nav = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(NAV_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        nav.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        // Brand icon
        JLabel brandIcon = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 2, ACCENT, 28, 24, ACCENT2);
                g2.setPaint(gp);
                g2.fillRoundRect(2, 4, 26, 20, 6, 6);
                g2.setColor(Color.WHITE);
                int[] xp = {12, 12, 22};
                int[] yp = {10, 20, 15};
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

        left.add(createNavBtn("Home", true, 0));
        JButton myBtn = createNavBtn("My Videos", false, 1);
        myBtn.addActionListener(e -> { dispose(); new MyDashboardScreen(); });
        left.add(myBtn);
        JButton uploadBtn = createNavBtn("Upload", false, 2);
        uploadBtn.addActionListener(e -> { dispose(); new UploadVideoScreen(); });
        left.add(uploadBtn);

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
                String init = session.getUsername().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init, (32 - fm.stringWidth(init)) / 2, (32 + fm.getAscent() - fm.getDescent()) / 2);
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

    // ====== FILTER BAR ======
    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catLabel.setForeground(TEXT_SECONDARY);
        bar.add(catLabel);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryComboBox.setPreferredSize(new Dimension(170, 34));
        categoryComboBox.setBackground(SURFACE);
        categoryComboBox.setForeground(TEXT_PRIMARY);
        categoryComboBox.addItem("All Categories");
        categoryComboBox.addActionListener(e -> filterVideos());
        bar.add(categoryComboBox);
        bar.add(Box.createHorizontalStrut(16));

        // Search icon
        JLabel searchIcon = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawOval(3, 3, 10, 10);
                g2.drawLine(12, 12, 16, 16);
            }
        };
        searchIcon.setPreferredSize(new Dimension(20, 20));
        bar.add(searchIcon);

        searchField = new JTextField(22);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(280, 34));
        searchField.setBackground(SURFACE);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filterVideos(); }
        });
        bar.add(searchField);
        return bar;
    }

    // iconType: 0=home, 1=video, 2=upload, 3=admin
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
                        g2.setColor(new Color(241, 245, 249));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                    txtC = TEXT_SECONDARY;
                }
                int ix = 10, iy = (getHeight() - 14) / 2;
                switch (iconType) {
                    case 0: LoginScreen.drawHomeIcon(g2, ix, iy, 14, txtC); break;
                    case 1: LoginScreen.drawVideoIcon(g2, ix, iy, 14, txtC); break;
                    case 2: LoginScreen.drawUploadIcon(g2, ix, iy, 14, txtC); break;
                    case 3: LoginScreen.drawAdminIcon(g2, ix, iy, 14, txtC); break;
                }
                g2.setColor(txtC);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 28, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        int w = btn.getFontMetrics(btn.getFont()).stringWidth(text) + 42;
        btn.setPreferredSize(new Dimension(w, 34));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createLogoutBtn() {
        JButton btn = new JButton("Logout") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(225, 29, 72, 15) : new Color(0, 0, 0, 0));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(ROSE);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { Session.getInstance().logout(); dispose(); new LoginScreen(); });
        return btn;
    }

    // ====== DATA ======
    private void loadCategories() {
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT name FROM categories_video ORDER BY name");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) categoryComboBox.addItem(rs.getString("name"));
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void loadVideos() {
        allVideos.clear();
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT v.id AS video_id, v.title, c.name AS category_name, v.user_id, " +
                "v.upload_date, v.views, v.downloads, v.likes " +
                "FROM videos_video v LEFT JOIN categories_video c ON v.category_id = c.id " +
                "ORDER BY v.upload_date DESC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                VideoData v = new VideoData();
                v.id = rs.getInt("video_id");
                v.title = rs.getString("title");
                v.category = rs.getString("category_name");
                v.views = rs.getInt("views");
                v.likes = rs.getInt("likes");
                v.downloads = rs.getInt("downloads");
                v.uploadDate = rs.getDate("upload_date");
                allVideos.add(v);
            }
            rs.close(); stmt.close(); conn.close();
            displayVideos(allVideos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading videos: " + ex.getMessage());
        }
    }

    private void filterVideos() {
        String cat = (String) categoryComboBox.getSelectedItem();
        String search = searchField.getText().trim().toLowerCase();
        ArrayList<VideoData> filtered = new ArrayList<>();
        for (VideoData v : allVideos) {
            boolean mc = cat.equals("All Categories") || cat.equals(v.category);
            boolean ms = search.isEmpty() || v.title.toLowerCase().contains(search);
            if (mc && ms) filtered.add(v);
        }
        displayVideos(filtered);
    }

    private void displayVideos(ArrayList<VideoData> videos) {
        videoGridPanel.removeAll();
        if (videos.isEmpty()) {
            JLabel empty = new JLabel("No videos found", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            empty.setForeground(TEXT_MUTED);
            videoGridPanel.setLayout(new BorderLayout());
            videoGridPanel.add(empty, BorderLayout.CENTER);
        } else {
            videoGridPanel.setLayout(new GridLayout(0, 3, 22, 22));
            for (VideoData v : videos) videoGridPanel.add(createVideoCard(v));
        }
        videoGridPanel.revalidate();
        videoGridPanel.repaint();
    }

    // ====== VIDEO CARD ======
    private JPanel createVideoCard(VideoData video) {
        JPanel card = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 3, getWidth() - 2, getHeight() - 2, 16, 16);
                // Body
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 4, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thumbnail
        JPanel thumb = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 16, 16, 16));
                GradientPaint gp = new GradientPaint(0, 0, ACCENT, getWidth(), getHeight(), ACCENT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(getWidth() - 80, -30, 120, 120);
                g2.fillOval(-40, getHeight() - 60, 100, 100);
                // Play circle
                int s = 50, cx = (getWidth() - s) / 2, cy = (getHeight() - s) / 2;
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(cx - 4, cy - 4, s + 8, s + 8);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(cx, cy, s, s);
                g2.setColor(new Color(255, 255, 255, 230));
                int[] xp = {cx + 18, cx + 18, cx + 36};
                int[] yp = {cy + 13, cy + 37, cy + 25};
                g2.fillPolygon(xp, yp, 3);
                // Category badge
                if (video.category != null) {
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(video.category) + 14;
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(10, 10, tw, 22, 8, 8);
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.drawString(video.category, 17, 25);
                }
                g2.setClip(null);
            }
        };
        thumb.setPreferredSize(new Dimension(320, 170));
        thumb.setOpaque(false);
        thumb.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { playVideo(video.id); }
        });
        card.add(thumb, BorderLayout.NORTH);

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel titleLabel = new JLabel(truncate(video.title, 38));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setToolTipText(video.title);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(titleLabel);
        info.add(Box.createVerticalStrut(6));

        // Stats with drawn icons
        JPanel statsPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(TEXT_MUTED);

                LoginScreen.drawEyeIcon(g2, 0, 1, 14, TEXT_MUTED);
                String viewsStr = formatNum(video.views);
                g2.drawString(viewsStr, 18, 12);
                int offset = 18 + g2.getFontMetrics().stringWidth(viewsStr) + 12;

                LoginScreen.drawHeartIcon(g2, offset, 2, 11, TEXT_MUTED);
                String likesStr = formatNum(video.likes);
                g2.drawString(likesStr, offset + 15, 12);
                int offset2 = offset + 15 + g2.getFontMetrics().stringWidth(likesStr) + 12;

                LoginScreen.drawDownloadIcon(g2, offset2, 1, 12, TEXT_MUTED);
                g2.drawString(formatNum(video.downloads), offset2 + 16, 12);
            }
        };
        statsPanel.setPreferredSize(new Dimension(280, 16));
        statsPanel.setMaximumSize(new Dimension(400, 16));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setOpaque(false);
        info.add(statsPanel);
        info.add(Box.createVerticalStrut(12));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton playBtn = createCardBtn("Play", GREEN, 0);
        playBtn.addActionListener(e -> playVideo(video.id));
        btnPanel.add(playBtn);

        JButton saveBtn = createCardBtn("Save", BLUE, 1);
        saveBtn.addActionListener(e -> downloadVideo(video.id));
        btnPanel.add(saveBtn);

        JButton likeBtn = createCardBtn("Like", ROSE, 2);
        likeBtn.addActionListener(e -> likeVideo(video.id));
        btnPanel.add(likeBtn);

        info.add(btnPanel);
        card.add(info, BorderLayout.CENTER);
        return card;
    }

    // iconType: 0=play, 1=download, 2=heart
    private JButton createCardBtn(String text, Color color, int iconType) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 25)
                        : new Color(color.getRed(), color.getGreen(), color.getBlue(), 12);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                int ix = 8, iy = (getHeight() - 10) / 2;
                switch (iconType) {
                    case 0: LoginScreen.drawPlayIcon(g2, ix, iy, 10, color); break;
                    case 1: LoginScreen.drawDownloadIcon(g2, ix, iy, 10, color); break;
                    case 2: LoginScreen.drawHeartIcon(g2, ix, iy, 9, color); break;
                }
                g2.setColor(color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 22, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(78, 28));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ====== ACTIONS ======
    private void playVideo(int videoId) {
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT video_path, title FROM videos_video WHERE id = ?");
            stmt.setInt(1, videoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String videoPath = rs.getString("video_path");
                if (videoPath != null && !videoPath.isEmpty()) {
                    File videoFile = new File(LoginScreen.VIDEOS_DIR, videoPath);
                    if (videoFile.exists()) {
                        Desktop.getDesktop().open(videoFile);
                        PreparedStatement u = conn.prepareStatement("UPDATE videos_video SET views = views + 1 WHERE id = ?");
                        u.setInt(1, videoId); u.executeUpdate(); u.close();
                        loadVideos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Video file not found on disk");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Video path not found");
                }
            }
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error playing video: " + ex.getMessage());
        }
    }

    private void downloadVideo(int videoId) {
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT video_path, title FROM videos_video WHERE id = ?");
            stmt.setInt(1, videoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String videoPath = rs.getString("video_path");
                String title = rs.getString("title");
                if (videoPath != null && !videoPath.isEmpty()) {
                    File videoFile = new File(LoginScreen.VIDEOS_DIR, videoPath);
                    if (videoFile.exists()) {
                        String ext = videoPath.contains(".") ? videoPath.substring(videoPath.lastIndexOf(".")) : ".mp4";
                        JFileChooser fc = new JFileChooser();
                        fc.setSelectedFile(new File(title + ext));
                        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                            try (FileInputStream fis = new FileInputStream(videoFile);
                                 FileOutputStream fos = new FileOutputStream(fc.getSelectedFile())) {
                                byte[] buf = new byte[4096];
                                int n;
                                while ((n = fis.read(buf)) != -1) fos.write(buf, 0, n);
                            }
                            PreparedStatement u = conn.prepareStatement("UPDATE videos_video SET downloads = downloads + 1 WHERE id = ?");
                            u.setInt(1, videoId); u.executeUpdate(); u.close();
                            JOptionPane.showMessageDialog(this, "Video downloaded successfully!");
                            loadVideos();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Video file not found on disk");
                    }
                }
            }
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error downloading video: " + ex.getMessage());
        }
    }

    private void likeVideo(int videoId) {
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE videos_video SET likes = likes + 1 WHERE id = ?");
            stmt.setInt(1, videoId);
            stmt.executeUpdate();
            stmt.close(); conn.close();
            loadVideos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ====== UTILS ======
    private String truncate(String t, int max) {
        if (t == null) return "";
        return t.length() > max ? t.substring(0, max) + "..." : t;
    }

    private String formatNum(int n) {
        if (n >= 1000000) return String.format("%.1fM", n / 1000000.0);
        if (n >= 1000) return String.format("%.1fK", n / 1000.0);
        return String.valueOf(n);
    }

    private class VideoData {
        int id, views, likes, downloads;
        String title, category;
        java.sql.Date uploadDate;
    }
}
