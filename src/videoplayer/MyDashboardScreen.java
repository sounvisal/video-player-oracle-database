package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

/**
 * My Dashboard Screen - Light modern theme with styled table
 */
public class MyDashboardScreen extends JFrame {
    private static final Color BG = LoginScreen.BG_WHITE;
    private static final Color SURFACE = LoginScreen.SURFACE;
    private static final Color BORDER = LoginScreen.BORDER;
    private static final Color ACCENT = LoginScreen.ACCENT;
    private static final Color ACCENT2 = LoginScreen.ACCENT2;
    private static final Color ACCENT_LIGHT = LoginScreen.ACCENT_LIGHT;
    private static final Color GREEN = LoginScreen.GREEN_BTN;
    private static final Color BLUE = LoginScreen.BLUE_BTN;
    private static final Color ROSE = LoginScreen.ROSE_BTN;
    private static final Color AMBER = LoginScreen.AMBER_BTN;
    private static final Color TEXT_PRIMARY = LoginScreen.TEXT_PRIMARY;
    private static final Color TEXT_SECONDARY = LoginScreen.TEXT_SECONDARY;
    private static final Color TEXT_MUTED = LoginScreen.TEXT_MUTED;
    private static final Color NAV_BG = LoginScreen.NAV_BG;
    private static final Color NAV_BORDER = LoginScreen.NAV_BORDER;
    private static final Color TABLE_HEADER = LoginScreen.TABLE_HEADER_BG;
    private static final Color TABLE_ALT = LoginScreen.TABLE_ROW_ALT;

    private JTable videoTable;
    private DefaultTableModel tableModel;

    public MyDashboardScreen() {
        initComponents();
        loadMyVideos();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("VMS - My Videos");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 650));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.add(createNavBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("My Videos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = createActionButton("Refresh", BLUE, 0);
        refreshBtn.addActionListener(e -> loadMyVideos());
        header.add(refreshBtn, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Title", "Category", "Views", "Likes", "Downloads", "Upload Date", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        videoTable = new JTable(tableModel);
        videoTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        videoTable.setRowHeight(48);
        videoTable.setShowGrid(false);
        videoTable.setIntercellSpacing(new Dimension(0, 0));
        videoTable.setBackground(SURFACE);
        videoTable.setForeground(TEXT_PRIMARY);
        videoTable.setSelectionBackground(ACCENT_LIGHT);
        videoTable.setSelectionForeground(TEXT_PRIMARY);
        videoTable.setFillsViewportHeight(true);

        // Header styling
        JTableHeader th = videoTable.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(TABLE_HEADER);
        th.setForeground(TEXT_SECONDARY);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        th.setPreferredSize(new Dimension(0, 42));

        // Alternating rows
        videoTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? SURFACE : TABLE_ALT);
                }
                comp.setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return comp;
            }
        });

        // Action column
        videoTable.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        videoTable.getColumnModel().getColumn(7).setCellEditor(new ActionEditor());

        // Column widths
        videoTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        videoTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        videoTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        videoTable.getColumnModel().getColumn(7).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(videoTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(SURFACE);
        content.add(scroll, BorderLayout.CENTER);
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

        JLabel brandIcon = new JLabel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 2, ACCENT, 28, 24, ACCENT2);
                g2.setPaint(gp);
                g2.fillRoundRect(2, 4, 26, 20, 6, 6);
                g2.setColor(Color.WHITE);
                int[] xp = {12, 12, 22}; int[] yp = {10, 20, 15};
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
        left.add(createNavBtn("My Videos", true, 1));
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
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
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
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { Session.getInstance().logout(); dispose(); new LoginScreen(); });
        return btn;
    }

    // ====== STYLED ACTION BUTTON ======
    private JButton createActionButton(String text, Color color, int iconType) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, color, getWidth(), getHeight(),
                    new Color(Math.max(color.getRed()-20,0), Math.max(color.getGreen()-20,0), Math.max(color.getBlue()-20,0)));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                int ix = 12, iy = (getHeight() - 12) / 2;
                if (iconType == 0) LoginScreen.drawRefreshIcon(g2, ix, iy, 12, Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 28, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ====== DATA ======
    private void loadMyVideos() {
        tableModel.setRowCount(0);
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT v.id, v.title, c.name AS category_name, v.views, v.likes, v.downloads, v.upload_date " +
                "FROM videos_video v LEFT JOIN categories_video c ON v.category_id = c.id " +
                "WHERE v.user_id = ? ORDER BY v.upload_date DESC");
            stmt.setInt(1, Session.getInstance().getUserId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("category_name"),
                    rs.getInt("views"), rs.getInt("likes"), rs.getInt("downloads"),
                    rs.getDate("upload_date"), "actions"
                });
            }
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading videos: " + ex.getMessage());
        }
    }

    private void editVideo(int videoId) {
        dispose();
        new EditVideoScreen(videoId);
    }

    private void deleteVideo(int videoId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this video?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                java.sql.Connection conn = Connection.getConnection();
                // Delete physical file first
                PreparedStatement pathStmt = conn.prepareStatement("SELECT video_path FROM videos_video WHERE id = ? AND user_id = ?");
                pathStmt.setInt(1, videoId);
                pathStmt.setInt(2, Session.getInstance().getUserId());
                ResultSet pathRs = pathStmt.executeQuery();
                if (pathRs.next()) {
                    String videoPath = pathRs.getString("video_path");
                    if (videoPath != null && !videoPath.isEmpty()) {
                        new java.io.File(LoginScreen.VIDEOS_DIR, videoPath).delete();
                    }
                }
                pathRs.close(); pathStmt.close();
                // Delete database record
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM videos_video WHERE id = ? AND user_id = ?");
                stmt.setInt(1, videoId);
                stmt.setInt(2, Session.getInstance().getUserId());
                stmt.executeUpdate();
                stmt.close(); conn.close();
                loadMyVideos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting video: " + ex.getMessage());
            }
        }
    }

    // ====== TABLE RENDERERS ======
    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn, deleteBtn;

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
            setOpaque(true);
            editBtn = makeSmallBtn("Edit", AMBER);
            deleteBtn = makeSmallBtn("Delete", ROSE);
            add(editBtn);
            add(deleteBtn);
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? ACCENT_LIGHT : (r % 2 == 0 ? SURFACE : TABLE_ALT));
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editBtn, deleteBtn;

        public ActionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            panel.setOpaque(true);
            editBtn = makeSmallBtn("Edit", AMBER);
            deleteBtn = makeSmallBtn("Delete", ROSE);

            editBtn.addActionListener(e -> {
                int row = videoTable.getSelectedRow();
                if (row >= 0) editVideo((int) tableModel.getValueAt(row, 0));
                fireEditingStopped();
            });
            deleteBtn.addActionListener(e -> {
                int row = videoTable.getSelectedRow();
                if (row >= 0) deleteVideo((int) tableModel.getValueAt(row, 0));
                fireEditingStopped();
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            panel.setBackground(ACCENT_LIGHT);
            return panel;
        }

        public Object getCellEditorValue() { return "actions"; }
    }

    private JButton makeSmallBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)
                    : new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                // Icon
                int ix = 6, iy = (getHeight() - 10) / 2;
                if (text.equals("Edit")) LoginScreen.drawEditIcon(g2, ix, iy, 10, color);
                else LoginScreen.drawDeleteIcon(g2, ix, iy, 10, color);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 20, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(72, 30));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
