package view;

import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel sidebarPanel;
    private JLabel lblTopTitle;
    private final Map<String, JButton> navBtns = new LinkedHashMap<>();
    private String activeSection = "Overview";

    private CardLayout cardLayout;
    private JPanel contentArea;

    private OverviewPanel overviewPanel;
    private SummaryPanel summaryPanel;
    private TransactionsPanel transactionsPanel;
    private ScheduledPanel scheduledPanel;
    private AccountsPanel accountsPanel;
    private CardsPanel cardsPanel;
    private BudgetsPanel budgetsPanel;
    private DebtsPanel debtsPanel;
    private CalendarPanel calendarPanel;
    private ToolsPanel toolsPanel;
    private PreferencesPanel preferencesPanel;

    private static final String[][] NAV_ITEMS = {
            {"Overview"},
            {"Summary"},
            {"Transactions"},
            {"Scheduled"},
            {"Accounts"},
            {"Credit Cards"},
            {"Budgets"},
            {"Debts"},
            {"Calendar"},
            {"---"},
            {"Tools"},
            {"User"},
            {"Preferences"},
            {"Logout"}
    };

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("Personal Finance System - Dashboard");
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel contentPane = new JPanel(null);
        contentPane.setBackground(UIUtils.BG_PAGE);
        setContentPane(contentPane);

        buildSidebar(contentPane);
        buildMainWrapper(contentPane);
        showSection("Overview");
    }

    private void buildSidebar(JPanel parent) {
        sidebarPanel = new JPanel(null);
        sidebarPanel.setBounds(0, 0, 260, 850);
        sidebarPanel.setBackground(UIUtils.SIDEBAR_BG);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIUtils.BORDER_COLOR));
        parent.add(sidebarPanel);

        // --- Modern Logo (Blue background, no icons/emojis) ---
        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();
        JLabel logo = new JLabel(currencySymbol + " FinTrack", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        logo.setBackground(UIUtils.BLUE);
        logo.setOpaque(true);
        logo.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        // Rounded corners
        logo.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(logo.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        logo.setBounds(20, 22, 200, 40);
        sidebarPanel.add(logo);

        JSeparator sep = new JSeparator();
        sep.setBounds(0, 72, 260, 2);
        sep.setForeground(UIUtils.BORDER_COLOR);
        sidebarPanel.add(sep);

        int y = 85;
        for (String[] item : NAV_ITEMS) {
            if (item[0].equals("---")) {
                JSeparator div = new JSeparator();
                div.setBounds(18, y + 4, 224, 1);
                div.setForeground(UIUtils.BORDER_COLOR);
                sidebarPanel.add(div);
                y += 18;
                continue;
            }
            JButton btn = buildNavButton(item[0], y);
            navBtns.put(item[0], btn);
            sidebarPanel.add(btn);
            y += 44;
        }

        JLabel darkMode = new JLabel("🌙  Dark mode");
        darkMode.setFont(UIUtils.F_SMALL);
        darkMode.setForeground(UIUtils.TEXT_MID);
        darkMode.setBounds(28, 808, 160, 28);
        sidebarPanel.add(darkMode);
    }

    private JButton buildNavButton(String label, int y) {
        JButton btn = new JButton(label) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                boolean active = activeSection.equals(label);
                setBackground(active ? UIUtils.BLUE_LIGHT : (hovered ? new Color(240,240,240) : UIUtils.SIDEBAR_BG));
                setForeground(active ? UIUtils.BLUE : UIUtils.TEXT_MID);
                setFont(active ? UIUtils.F_NAV_SEL : UIUtils.F_NAV);
                if (active) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(UIUtils.BLUE);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                super.paintComponent(g);
            }
        };
        btn.setBounds(0, y, 260, 42);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> navigateTo(label));
        return btn;
    }

    private void buildMainWrapper(JPanel parent) {
        JPanel mainWrapper = new JPanel(null);
        mainWrapper.setBounds(260, 0, 1140, 850);
        mainWrapper.setBackground(UIUtils.BG_PAGE);
        parent.add(mainWrapper);

        JPanel topBar = new JPanel(null);
        topBar.setBounds(0, 0, 1140, 66);
        topBar.setBackground(UIUtils.BLUE);
        mainWrapper.add(topBar);

        lblTopTitle = new JLabel("Overview");
        lblTopTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTopTitle.setForeground(UIUtils.WHITE);
        lblTopTitle.setBounds(30, 16, 300, 34);
        topBar.add(lblTopTitle);

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBounds(0, 66, 1140, 784);
        contentArea.setBackground(UIUtils.BG_PAGE);
        mainWrapper.add(contentArea);

        overviewPanel = new OverviewPanel(currentUser);
        contentArea.add(UIUtils.scrollWrap(overviewPanel), "Overview");

        summaryPanel = new SummaryPanel(currentUser);
        contentArea.add(UIUtils.scrollWrap(summaryPanel), "Summary");

        transactionsPanel = new TransactionsPanel(currentUser);
        contentArea.add(UIUtils.scrollWrap(transactionsPanel), "Transactions");

        scheduledPanel = new ScheduledPanel(currentUser);
        contentArea.add(scheduledPanel, "Scheduled");

        accountsPanel = new AccountsPanel(currentUser);
        contentArea.add(accountsPanel, "Accounts");

        cardsPanel = new CardsPanel(currentUser);
        contentArea.add(cardsPanel, "Credit Cards");

        budgetsPanel = new BudgetsPanel(currentUser);
        contentArea.add(UIUtils.scrollWrap(budgetsPanel), "Budgets");

        debtsPanel = new DebtsPanel(currentUser);
        contentArea.add(debtsPanel, "Debts");

        calendarPanel = new CalendarPanel(currentUser);
        contentArea.add(calendarPanel, "Calendar");

        toolsPanel = new ToolsPanel(currentUser);
        contentArea.add(toolsPanel, "Tools");

        contentArea.add(new UserProfilePanel(currentUser), "User");

        preferencesPanel = new PreferencesPanel(currentUser);
        contentArea.add(preferencesPanel, "Preferences");

        transactionsPanel.setOnDataChanged(() -> {
            if (accountsPanel != null) accountsPanel.refreshData();
            if (cardsPanel != null) cardsPanel.refreshCards();
            if (budgetsPanel != null) budgetsPanel.refreshData();
            if (overviewPanel != null) overviewPanel.refreshData();
            if (summaryPanel != null) summaryPanel.refreshData();
            if (debtsPanel != null) debtsPanel.refreshData();
        });

        toolsPanel.setOnDataChanged(() -> {
            if (accountsPanel != null) accountsPanel.refreshData();
            if (cardsPanel != null) cardsPanel.refreshCards();
            if (budgetsPanel != null) budgetsPanel.refreshData();
            if (overviewPanel != null) overviewPanel.refreshData();
            if (summaryPanel != null) summaryPanel.refreshData();
            if (debtsPanel != null) debtsPanel.refreshData();
            if (transactionsPanel != null) transactionsPanel.refreshTransactions();
        });

        preferencesPanel.setOnPreferencesChanged(() -> {
            if (accountsPanel != null) accountsPanel.refreshData();
            if (cardsPanel != null) cardsPanel.refreshCards();
            if (budgetsPanel != null) budgetsPanel.refreshData();
            if (overviewPanel != null) overviewPanel.refreshData();
            if (summaryPanel != null) summaryPanel.refreshData();
            if (debtsPanel != null) debtsPanel.refreshData();
            if (transactionsPanel != null) transactionsPanel.refreshTransactions();
            if (calendarPanel != null) calendarPanel.refreshData();
            if (scheduledPanel != null) scheduledPanel.refreshData();
            String newSymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();
            ((JLabel) sidebarPanel.getComponent(0)).setText(newSymbol + " FinTrack");
        });
    }

    private void navigateTo(String destination) {
        if (destination.equals("Logout")) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        activeSection = destination;
        navBtns.values().forEach(Component::repaint);
        lblTopTitle.setText(destination);
        cardLayout.show(contentArea, destination);

        if (destination.equals("Overview") && overviewPanel != null) {
            overviewPanel.refreshData();
        }
        if (destination.equals("Summary") && summaryPanel != null) {
            summaryPanel.refreshData();
        }
        if (destination.equals("Transactions") && transactionsPanel != null) {
            transactionsPanel.refreshTransactions();
        }
        if (destination.equals("Budgets") && budgetsPanel != null) {
            budgetsPanel.refreshData();
        }
        if (destination.equals("Calendar") && calendarPanel != null) {
            calendarPanel.refreshData();
        }
        if (destination.equals("Accounts") && accountsPanel != null) {
            accountsPanel.refreshData();
        }
        if (destination.equals("Credit Cards") && cardsPanel != null) {
            cardsPanel.refreshCards();
        }
        if (destination.equals("Debts") && debtsPanel != null) {
            debtsPanel.refreshData();
        }
    }

    private void showSection(String name) {
        activeSection = name;
        navBtns.values().forEach(Component::repaint);
        lblTopTitle.setText(name);
    }

    public void refreshBudgetsPanel() {
        if (budgetsPanel != null) {
            budgetsPanel.refreshData();
        }
    }
}