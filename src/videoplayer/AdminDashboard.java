package videoplayer;

import database.Connection;
import java.awt.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Admin Dashboard - Light modern theme with Graphics2D bar charts
 */
public class AdminDashboard extends JFrame {
    private static final Color BG = LoginScreen.BG_WHITE;
    private static final Color SURFACE = LoginScreen.SURFACE;
    private static final Color SURFACE_ALT = LoginScreen.SURFACE_ALT;
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

    // Chart colors
    private static final Color[] CHART_COLORS = {
        new Color(79, 70, 229),   // indigo
        new Color(22, 163, 74),   // green
        new Color(37, 99, 235),   // blue
        new Color(225, 29, 72),   // rose
        new Color(217, 119, 6),   // amber
        new Color(124, 58, 237),  // violet
        new Color(6, 182, 212),   // cyan
        new Color(234, 88, 12),   // orange
    };

    private JTabbedPane tabbedPane;
    // Stats data
    private int totalVideos, totalUsers, totalViews, totalLikes, totalDownloads;
    private ArrayList<String> catNames = new ArrayList<>();
    private ArrayList<Integer> catVideoCounts = new ArrayList<>();
    private ArrayList<Integer> catViewCounts = new ArrayList<>();
    private ArrayList<Integer> catLikeCounts = new ArrayList<>();
    private ArrayList<Integer> catDownloadCounts = new ArrayList<>();
    // Top videos data
    private ArrayList<String> topVideoTitles = new ArrayList<>();
    private ArrayList<Integer> topVideoViews = new ArrayList<>();
    private ArrayList<Integer> topVideoLikes = new ArrayList<>();
    private ArrayList<Integer> topVideoDownloads = new ArrayList<>();

    public AdminDashboard() {
        loadStatisticsData();
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        setTitle("VMS - Admin Dashboard");
        setSize(1320, 880);
        setMinimumSize(new Dimension(1100, 700));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);
        mainPanel.add(createNavBar(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane() {
            protected void paintComponent(Graphics g) {
                g.setColor(BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        tabbedPane.addTab("  Statistics  ", createStatisticsTab());
        tabbedPane.addTab("  Video Management  ", createVideoManagementTab());
        tabbedPane.addTab("  User Management  ", createUserManagementTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // =============== STATISTICS TAB ===============
    private JPanel createStatisticsTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(BG);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Stat cards row
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 18, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardsRow.add(createStatCard("Total Videos", totalVideos, ACCENT, 0));
        cardsRow.add(createStatCard("Total Users", totalUsers, GREEN, 1));
        cardsRow.add(createStatCard("Total Views", totalViews, BLUE, 2));
        cardsRow.add(createStatCard("Total Likes", totalLikes, ROSE, 3));
        content.add(cardsRow);
        content.add(Box.createVerticalStrut(24));

        // Row 2: Donut chart + Grouped bar chart
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        chartsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartsRow.add(createDonutChart());
        chartsRow.add(createGroupedBarChart());
        content.add(chartsRow);
        content.add(Box.createVerticalStrut(20));

        // Row 3: Top Videos ranking table
        JPanel topVideosPanel = createTopVideosPanel();
        topVideosPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        topVideosPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(topVideosPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        tab.add(scroll, BorderLayout.CENTER);
        return tab;
    }

    // iconType: 0=video, 1=user, 2=eye, 3=heart
    private JPanel createStatCard(String label, int value, Color color, int iconType) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 3, getWidth()-2, getHeight()-2, 16, 16);
                // Body
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-3, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-3, getHeight()-4, 16, 16);
                // Color accent stripe on top
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth()-3, 4, 16, 16);
                g2.fillRect(0, 2, getWidth()-3, 4);

                // Icon circle
                int circX = 20, circY = 24, circS = 42;
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2.fillOval(circX, circY, circS, circS);
                switch(iconType) {
                    case 0: LoginScreen.drawVideoIcon(g2, circX+11, circY+11, 20, color); break;
                    case 1: LoginScreen.drawAdminIcon(g2, circX+11, circY+11, 20, color); break;
                    case 2: LoginScreen.drawEyeIcon(g2, circX+11, circY+11, 20, color); break;
                    case 3: LoginScreen.drawHeartIcon(g2, circX+11, circY+11, 18, color); break;
                }

                // Text
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(label, 76, 38);
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                g2.drawString(formatNum(value), 76, 66);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(260, 100));
        return card;
    }

    // ====== DONUT CHART ======
    private JPanel createDonutChart() {
        // Filter to only categories with videos > 0
        ArrayList<String> fNames = new ArrayList<>();
        ArrayList<Integer> fCounts = new ArrayList<>();
        for (int i = 0; i < catNames.size(); i++) {
            if (catVideoCounts.get(i) > 0) {
                fNames.add(catNames.get(i));
                fCounts.add(catVideoCounts.get(i));
            }
        }
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int w = getWidth(), h = getHeight();

                // Card bg
                g2.setColor(new Color(0,0,0,8));
                g2.fillRoundRect(2, 3, w-2, h-2, 16, 16);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, w-2, h-3, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, w-3, h-4, 16, 16);

                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.drawString("Video Distribution", 24, 32);

                if (fNames.isEmpty()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.drawString("No data available", w/2 - 50, h/2);
                    return;
                }

                // Donut
                int total = 0;
                for (int c : fCounts) total += c;
                int cx = w/2 - 80, cy = h/2 + 10;
                int outerR = Math.min(w, h) / 2 - 50;
                int innerR = outerR * 55 / 100;

                double startAngle = 90;
                for (int i = 0; i < fCounts.size(); i++) {
                    double sweep = 360.0 * fCounts.get(i) / total;
                    Color c = CHART_COLORS[i % CHART_COLORS.length];
                    g2.setColor(c);
                    g2.fill(new Arc2D.Double(cx - outerR, cy - outerR, outerR*2, outerR*2,
                        startAngle, sweep, Arc2D.PIE));
                    startAngle += sweep;
                }
                // Inner circle (donut hole)
                g2.setColor(SURFACE);
                g2.fillOval(cx - innerR, cy - innerR, innerR*2, innerR*2);

                // Center text
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String totalStr = String.valueOf(total);
                g2.drawString(totalStr, cx - fm.stringWidth(totalStr)/2, cy + 4);
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                fm = g2.getFontMetrics();
                g2.drawString("videos", cx - fm.stringWidth("videos")/2, cy + 20);

                // Legend
                int legendX = cx + outerR + 30;
                int legendY = cy - (fNames.size() * 24) / 2;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                for (int i = 0; i < fNames.size(); i++) {
                    int ly = legendY + i * 26;
                    g2.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                    g2.fillRoundRect(legendX, ly, 14, 14, 4, 4);
                    g2.setColor(TEXT_PRIMARY);
                    g2.drawString(fNames.get(i) + " (" + fCounts.get(i) + ")", legendX + 22, ly + 12);
                }
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(500, 340));
        return panel;
    }

    // ====== GROUPED BAR CHART (Views / Likes / Downloads per category) ======
    private JPanel createGroupedBarChart() {
        // Filter to only categories with any activity
        ArrayList<String> fNames = new ArrayList<>();
        ArrayList<int[]> fData = new ArrayList<>(); // [views, likes, downloads]
        for (int i = 0; i < catNames.size(); i++) {
            int v = catViewCounts.get(i), l = catLikeCounts.get(i), d = catDownloadCounts.get(i);
            if (v + l + d > 0) {
                fNames.add(catNames.get(i));
                fData.add(new int[]{v, l, d});
            }
        }
        Color[] barColors = {BLUE, ROSE, AMBER};
        String[] legendLabels = {"Views", "Likes", "Downloads"};

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int w = getWidth(), h = getHeight();

                // Card bg
                g2.setColor(new Color(0,0,0,8));
                g2.fillRoundRect(2, 3, w-2, h-2, 16, 16);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, w-2, h-3, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, w-3, h-4, 16, 16);

                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.drawString("Engagement by Category", 24, 32);

                // Legend top-right
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                int lgX = w - 200;
                for (int i = 0; i < 3; i++) {
                    g2.setColor(barColors[i]);
                    g2.fillRoundRect(lgX, 18, 10, 10, 3, 3);
                    g2.setColor(TEXT_SECONDARY);
                    g2.drawString(legendLabels[i], lgX + 14, 27);
                    lgX += 58;
                }

                if (fNames.isEmpty()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.drawString("No data available", w/2 - 50, h/2);
                    return;
                }

                int chartLeft = 60, chartRight = w - 24;
                int chartTop = 50, chartBottom = h - 50;
                int chartW = chartRight - chartLeft;
                int chartH = chartBottom - chartTop;

                // Max value across all
                int maxVal = 1;
                for (int[] d : fData)
                    for (int val : d) if (val > maxVal) maxVal = val;
                int gridStep = Math.max(1, (int) Math.ceil(maxVal / 4.0));
                maxVal = gridStep * 4;

                // Grid
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setStroke(new BasicStroke(1f));
                for (int i = 0; i <= 4; i++) {
                    int yPos = chartBottom - (int)(chartH * i / 4.0);
                    g2.setColor(new Color(226,232,240,100));
                    g2.drawLine(chartLeft, yPos, chartRight, yPos);
                    g2.setColor(TEXT_MUTED);
                    String yl = formatNum(gridStep * i);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(yl, chartLeft - fm.stringWidth(yl) - 6, yPos + 4);
                }

                // Bars - 3 per group
                int groupCount = fNames.size();
                int groupGap = 24;
                int barGap = 3;
                int subBarW = Math.max(14, Math.min(28, (chartW - groupGap * (groupCount + 1)) / (groupCount * 3)));
                int groupW = subBarW * 3 + barGap * 2;
                int totalGroupsW = groupCount * groupW + (groupCount - 1) * groupGap;
                int startX = chartLeft + (chartW - totalGroupsW) / 2;

                for (int gi = 0; gi < groupCount; gi++) {
                    int gx = startX + gi * (groupW + groupGap);
                    int[] data = fData.get(gi);
                    for (int bi = 0; bi < 3; bi++) {
                        int bx = gx + bi * (subBarW + barGap);
                        int barH = maxVal > 0 ? (int)((double) data[bi] / maxVal * chartH) : 0;
                        int by = chartBottom - barH;
                        if (barH > 0) {
                            GradientPaint gp = new GradientPaint(bx, by, barColors[bi], bx, chartBottom,
                                new Color(barColors[bi].getRed(), barColors[bi].getGreen(), barColors[bi].getBlue(), 160));
                            g2.setPaint(gp);
                            g2.fillRoundRect(bx, by, subBarW, barH, 4, 4);
                        }
                        // Value label
                        if (data[bi] > 0) {
                            g2.setColor(barColors[bi]);
                            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                            FontMetrics fm = g2.getFontMetrics();
                            String vs = String.valueOf(data[bi]);
                            g2.drawString(vs, bx + (subBarW - fm.stringWidth(vs))/2, by - 4);
                        }
                    }
                    // Category label below
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    String lbl = fNames.get(gi).length() > 12 ? fNames.get(gi).substring(0,11)+".." : fNames.get(gi);
                    g2.drawString(lbl, gx + (groupW - fm.stringWidth(lbl))/2, chartBottom + 16);
                }

                // Axes
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(chartLeft, chartTop, chartLeft, chartBottom);
                g2.drawLine(chartLeft, chartBottom, chartRight, chartBottom);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(500, 340));
        return panel;
    }

    // ====== TOP VIDEOS RANKING TABLE ======
    private JPanel createTopVideosPanel() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                int w = getWidth(), h = getHeight();

                // Card bg
                g2.setColor(new Color(0,0,0,8));
                g2.fillRoundRect(2, 3, w-2, h-2, 16, 16);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, w-2, h-3, 16, 16);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, w-3, h-4, 16, 16);

                g2.setColor(TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.drawString("Top Videos by Views", 24, 32);

                if (topVideoTitles.isEmpty()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    g2.drawString("No videos yet", w/2 - 40, h/2);
                    return;
                }

                int maxViews = 1;
                for (int v : topVideoViews) if (v > maxViews) maxViews = v;

                // Column headers
                int y = 60;
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(TEXT_MUTED);
                g2.drawString("#", 30, y);
                g2.drawString("VIDEO TITLE", 55, y);
                g2.drawString("VIEWS", w - 360, y);
                g2.drawString("PROGRESS", w - 290, y);
                g2.drawString("LIKES", w - 130, y);
                g2.drawString("SAVES", w - 65, y);

                g2.setColor(BORDER);
                g2.drawLine(24, y + 8, w - 24, y + 8);

                // Rows
                Color[] rankColors = {new Color(250,204,21), new Color(148,163,184), new Color(217,119,6), ACCENT, BLUE};
                for (int i = 0; i < topVideoTitles.size(); i++) {
                    int ry = y + 26 + i * 44;

                    // Alternating bg
                    if (i % 2 == 1) {
                        g2.setColor(SURFACE_ALT);
                        g2.fillRoundRect(22, ry - 14, w - 44, 40, 8, 8);
                    }

                    // Rank badge
                    Color rc = i < rankColors.length ? rankColors[i] : TEXT_MUTED;
                    g2.setColor(new Color(rc.getRed(), rc.getGreen(), rc.getBlue(), 25));
                    g2.fillRoundRect(28, ry - 10, 22, 22, 6, 6);
                    g2.setColor(rc);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(String.valueOf(i+1), 39 - fm.stringWidth(String.valueOf(i+1))/2, ry + 5);

                    // Title
                    g2.setColor(TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    String title = topVideoTitles.get(i);
                    if (title.length() > 35) title = title.substring(0, 34) + "...";
                    g2.drawString(title, 58, ry + 5);

                    // Views count
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2.drawString(formatNum(topVideoViews.get(i)), w - 360, ry + 5);

                    // Progress bar
                    int barX = w - 290, barY = ry - 5, barW = 140, barH = 12;
                    g2.setColor(new Color(226,232,240));
                    g2.fillRoundRect(barX, barY, barW, barH, 6, 6);
                    int fillW = (int)((double) topVideoViews.get(i) / maxViews * barW);
                    if (fillW > 0) {
                        GradientPaint gp = new GradientPaint(barX, barY, ACCENT, barX + fillW, barY, ACCENT2);
                        g2.setPaint(gp);
                        g2.fillRoundRect(barX, barY, fillW, barH, 6, 6);
                    }

                    // Likes
                    g2.setColor(TEXT_SECONDARY);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    LoginScreen.drawHeartIcon(g2, w - 134, ry - 5, 10, ROSE);
                    g2.drawString(formatNum(topVideoLikes.get(i)), w - 120, ry + 5);

                    // Downloads
                    LoginScreen.drawDownloadIcon(g2, w - 68, ry - 5, 10, BLUE);
                    g2.drawString(formatNum(topVideoDownloads.get(i)), w - 54, ry + 5);
                }
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(800, 330));
        return panel;
    }

    // =============== VIDEO MANAGEMENT TAB ===============
    private JPanel createVideoManagementTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(BG);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel title = new JLabel("All Videos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        tab.add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Category", "Uploader", "Views", "Likes", "Downloads", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 7; }
        };
        JTable table = createStyledTable(model);

        // Action renderer/editor
        table.getColumnModel().getColumn(7).setCellRenderer(new DeleteBtnRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new DeleteBtnEditor(model, table, "video"));

        // Load data
        loadAllVideos(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(SURFACE);
        tab.add(scroll, BorderLayout.CENTER);
        return tab;
    }

    // =============== USER MANAGEMENT TAB ===============
    private JPanel createUserManagementTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(BG);
        tab.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        JLabel title = new JLabel("All Users");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        tab.add(header, BorderLayout.NORTH);

        String[] cols = {"User ID", "Username", "Role", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable table = createStyledTable(model);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setCellRenderer(new UserActionRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new UserActionEditor(model, table));

        loadAllUsers(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(SURFACE);
        tab.add(scroll, BorderLayout.CENTER);
        return tab;
    }

    // ====== STYLED TABLE ======
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(44);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(SURFACE);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(ACCENT_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setBackground(TABLE_HEADER);
        th.setForeground(TEXT_SECONDARY);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        th.setPreferredSize(new Dimension(0, 42));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? SURFACE : TABLE_ALT);
                comp.setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return comp;
            }
        });
        return table;
    }

    // ====== DATA LOADING ======
    private void loadStatisticsData() {
        try {
            java.sql.Connection conn = Connection.getConnection();
            // Totals
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM videos_video");
            if (rs.next()) totalVideos = rs.getInt("cnt");
            rs.close();
            rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM users_video");
            if (rs.next()) totalUsers = rs.getInt("cnt");
            rs.close();
            rs = st.executeQuery("SELECT NVL(SUM(views),0) AS s FROM videos_video");
            if (rs.next()) totalViews = rs.getInt("s");
            rs.close();
            rs = st.executeQuery("SELECT NVL(SUM(likes),0) AS s FROM videos_video");
            if (rs.next()) totalLikes = rs.getInt("s");
            rs.close();
            rs = st.executeQuery("SELECT NVL(SUM(downloads),0) AS s FROM videos_video");
            if (rs.next()) totalDownloads = rs.getInt("s");
            rs.close();
            st.close();

            // Per-category
            PreparedStatement ps = conn.prepareStatement(
                "SELECT c.name, COUNT(v.id) AS vid_count, NVL(SUM(v.views),0) AS total_views, " +
                "NVL(SUM(v.likes),0) AS total_likes, NVL(SUM(v.downloads),0) AS total_downloads " +
                "FROM categories_video c LEFT JOIN videos_video v ON c.id = v.category_id " +
                "GROUP BY c.name ORDER BY c.name");
            rs = ps.executeQuery();
            while (rs.next()) {
                catNames.add(rs.getString("name"));
                catVideoCounts.add(rs.getInt("vid_count"));
                catViewCounts.add(rs.getInt("total_views"));
                catLikeCounts.add(rs.getInt("total_likes"));
                catDownloadCounts.add(rs.getInt("total_downloads"));
            }
            rs.close(); ps.close();

            // Top 5 videos by views
            PreparedStatement topStmt = conn.prepareStatement(
                "SELECT title, views, likes, downloads FROM videos_video ORDER BY views DESC FETCH FIRST 5 ROWS ONLY");
            rs = topStmt.executeQuery();
            while (rs.next()) {
                topVideoTitles.add(rs.getString("title"));
                topVideoViews.add(rs.getInt("views"));
                topVideoLikes.add(rs.getInt("likes"));
                topVideoDownloads.add(rs.getInt("downloads"));
            }
            rs.close(); topStmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading statistics: " + ex.getMessage());
        }
    }

    private void loadAllVideos(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT v.id, v.title, c.name AS category_name, u.username, v.views, v.likes, v.downloads " +
                "FROM videos_video v LEFT JOIN categories_video c ON v.category_id = c.id " +
                "LEFT JOIN users_video u ON v.user_id = u.user_id ORDER BY v.id");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("category_name"),
                    rs.getString("username"), rs.getInt("views"), rs.getInt("likes"),
                    rs.getInt("downloads"), "delete"
                });
            }
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading videos: " + ex.getMessage());
        }
    }

    private void loadAllUsers(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            java.sql.Connection conn = Connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id, username, role FROM users_video ORDER BY user_id");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("user_id"), rs.getString("username"), rs.getString("role"), "delete"
                });
            }
            rs.close(); stmt.close(); conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    private void deleteRecord(String type, int id, DefaultTableModel model) {
        String msg = type.equals("video") ? "Delete this video?" : "Delete this user?";
        if (JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                java.sql.Connection conn = Connection.getConnection();
                // If deleting a video, delete the physical file first
                if (type.equals("video")) {
                    PreparedStatement pathStmt = conn.prepareStatement("SELECT video_path FROM videos_video WHERE id = ?");
                    pathStmt.setInt(1, id);
                    ResultSet pathRs = pathStmt.executeQuery();
                    if (pathRs.next()) {
                        String videoPath = pathRs.getString("video_path");
                        if (videoPath != null && !videoPath.isEmpty()) {
                            new java.io.File(LoginScreen.VIDEOS_DIR, videoPath).delete();
                        }
                    }
                    pathRs.close(); pathStmt.close();
                }
                String sql = type.equals("video")
                    ? "DELETE FROM videos_video WHERE id = ?"
                    : "DELETE FROM users_video WHERE user_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, id);
                stmt.executeUpdate();
                stmt.close(); conn.close();
                if (type.equals("video")) loadAllVideos(model);
                else loadAllUsers(model);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ====== USER ACTION BUTTONS (Role + Delete) ======
    private class UserActionRenderer extends JPanel implements TableCellRenderer {
        private JButton roleBtn, deleteBtn;
        public UserActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
            setOpaque(true);
            roleBtn = makeRoleBtn();
            deleteBtn = makeDeleteBtn();
            add(roleBtn);
            add(deleteBtn);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? ACCENT_LIGHT : (r % 2 == 0 ? SURFACE : TABLE_ALT));
            return this;
        }
    }

    private class UserActionEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private DefaultTableModel model;
        private JTable table;

        public UserActionEditor(DefaultTableModel model, JTable table) {
            this.model = model; this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            panel.setOpaque(true);

            JButton roleBtn = makeRoleBtn();
            roleBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int userId = (int) model.getValueAt(row, 0);
                    String username = (String) model.getValueAt(row, 1);
                    String currentRole = (String) model.getValueAt(row, 2);
                    fireEditingStopped();
                    changeUserRole(userId, username, currentRole, model);
                } else {
                    fireEditingStopped();
                }
            });

            JButton deleteBtn = makeDeleteBtn();
            deleteBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);
                    deleteRecord("user", id, model);
                }
                fireEditingStopped();
            });

            panel.add(roleBtn);
            panel.add(deleteBtn);
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            panel.setBackground(ACCENT_LIGHT);
            return panel;
        }
        public Object getCellEditorValue() { return "actions"; }
    }

    private JButton makeRoleBtn() {
        JButton btn = new JButton("Role") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(AMBER.getRed(), AMBER.getGreen(), AMBER.getBlue(), 30)
                    : new Color(AMBER.getRed(), AMBER.getGreen(), AMBER.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(AMBER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                int ix = 6, iy = (getHeight()-10)/2;
                LoginScreen.drawAdminIcon(g2, ix, iy, 10, AMBER);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 20, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(72, 30));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void changeUserRole(int userId, String username, String currentRole, DefaultTableModel model) {
        String newRole = currentRole.equalsIgnoreCase("ADMIN") ? "USER" : "ADMIN";
        String msg = "Change role of '" + username + "' from " + currentRole + " to " + newRole + "?";
        if (JOptionPane.showConfirmDialog(this, msg, "Confirm Role Change", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                java.sql.Connection conn = Connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE users_video SET role = ? WHERE user_id = ?");
                stmt.setString(1, newRole);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
                stmt.close(); conn.close();
                JOptionPane.showMessageDialog(this, "Role changed to " + newRole + " successfully!");
                loadAllUsers(model);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error changing role: " + ex.getMessage());
            }
        }
    }

    // ====== TABLE DELETE BUTTON ======
    private class DeleteBtnRenderer extends JPanel implements TableCellRenderer {
        private JButton deleteBtn;
        public DeleteBtnRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 6));
            setOpaque(true);
            deleteBtn = makeDeleteBtn();
            add(deleteBtn);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? ACCENT_LIGHT : (r % 2 == 0 ? SURFACE : TABLE_ALT));
            return this;
        }
    }

    private class DeleteBtnEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton deleteBtn;
        private DefaultTableModel model;
        private JTable table;
        private String type;

        public DeleteBtnEditor(DefaultTableModel model, JTable table, String type) {
            this.model = model; this.table = table; this.type = type;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            panel.setOpaque(true);
            deleteBtn = makeDeleteBtn();
            deleteBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int id = (int) model.getValueAt(row, 0);
                    deleteRecord(type, id, model);
                }
                fireEditingStopped();
            });
            panel.add(deleteBtn);
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            panel.setBackground(ACCENT_LIGHT);
            return panel;
        }
        public Object getCellEditorValue() { return "delete"; }
    }

    private JButton makeDeleteBtn() {
        JButton btn = new JButton("Delete") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()
                    ? new Color(ROSE.getRed(), ROSE.getGreen(), ROSE.getBlue(), 30)
                    : new Color(ROSE.getRed(), ROSE.getGreen(), ROSE.getBlue(), 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(ROSE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                int ix = 6, iy = (getHeight()-10)/2;
                LoginScreen.drawDeleteIcon(g2, ix, iy, 10, ROSE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 20, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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
        JButton uploadBtn = createNavBtn("Upload", false, 2);
        uploadBtn.addActionListener(e -> { dispose(); new UploadVideoScreen(); });
        left.add(uploadBtn);
        left.add(createNavBtn("Admin", true, 3));
        nav.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        Session session = Session.getInstance();
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

    private String formatNum(int n) {
        if (n >= 1000000) return String.format("%.1fM", n / 1000000.0);
        if (n >= 1000) return String.format("%.1fK", n / 1000.0);
        return String.valueOf(n);
    }
}
