package view;

import database.BudgetDAO;
import model.Budget;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BudgetsPanel extends JPanel {

    private User currentUser;
    private BudgetDAO budgetDAO = new BudgetDAO();

    private boolean isMonthly = true;
    private YearMonth currentMonth = YearMonth.now();
    private int currentYear = YearMonth.now().getYear();

    private JLabel periodLabel;
    private JPanel contentPanel;
    private JPanel summaryCard;
    private JPanel gridCard;
    private JLabel budgetedLabel, spentLabel, remainingLabel;
    private JPanel progressWrapper;

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);
    private Color[] budgetColors = {UIUtils.GREEN, UIUtils.BLUE, UIUtils.ORANGE, new Color(0,188,212), UIUtils.RED, UIUtils.PURPLE};

    private static final int CARD_WIDTH = 500;
    private static final int CARD_HEIGHT = 130;
    private static final int COLUMNS = 2;
    private static final int HORIZONTAL_GAP = 20;
    private static final int VERTICAL_GAP = 15;

    public BudgetsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_PAGE);

        contentPanel = new JPanel(null);
        contentPanel.setBackground(UIUtils.BG_PAGE);
        contentPanel.setPreferredSize(new Dimension(1110, 850));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.BG_PAGE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        buildFixedUI();
        refreshData();
    }

    private void buildFixedUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBounds(20, 14, 1070, 50);
        header.setOpaque(false);
        contentPanel.add(header);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        navPanel.setOpaque(false);

        JButton prevBtn = createNavButton("←");
        prevBtn.addActionListener(e -> navigate(-1));

        periodLabel = new JLabel("", SwingConstants.CENTER);
        periodLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        periodLabel.setForeground(UIUtils.TEXT_DARK);

        JButton nextBtn = createNavButton("→");
        nextBtn.addActionListener(e -> navigate(1));

        navPanel.add(prevBtn);
        navPanel.add(periodLabel);
        navPanel.add(nextBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JToggleButton monthlyToggle = createPillToggleButton("Monthly", true);
        JToggleButton yearlyToggle = createPillToggleButton("Yearly", false);

        ButtonGroup group = new ButtonGroup();
        group.add(monthlyToggle);
        group.add(yearlyToggle);

        monthlyToggle.addActionListener(e -> {
            isMonthly = true;
            refreshData();
        });
        yearlyToggle.addActionListener(e -> {
            isMonthly = false;
            refreshData();
        });

        JPanel toggleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        toggleWrapper.setOpaque(false);
        toggleWrapper.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        toggleWrapper.add(monthlyToggle);
        toggleWrapper.add(yearlyToggle);

        JButton addBtn = createModernButton("+ New Budget", UIUtils.BLUE);
        addBtn.addActionListener(e -> {
            AddEditBudgetDialog dialog = new AddEditBudgetDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshData();
        });

        rightPanel.add(toggleWrapper);
        rightPanel.add(addBtn);

        header.add(navPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        summaryCard = UIUtils.card(20, 72, 1070, 100);
        summaryCard.setLayout(null);
        contentPanel.add(summaryCard);

        UIUtils.label(summaryCard, "Monthly Summary", UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 12);
        UIUtils.separator(summaryCard, 20, 38, 1030);

        UIUtils.label(summaryCard, "Budgeted:", UIUtils.F_BODY, UIUtils.TEXT_MID, 20, 48);
        budgetedLabel = new JLabel();
        budgetedLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        budgetedLabel.setForeground(UIUtils.BLUE);
        budgetedLabel.setBounds(120, 48, 150, 25);
        summaryCard.add(budgetedLabel);

        UIUtils.label(summaryCard, "Spent:", UIUtils.F_BODY, UIUtils.TEXT_MID, 300, 48);
        spentLabel = new JLabel();
        spentLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        spentLabel.setForeground(UIUtils.ORANGE);
        spentLabel.setBounds(370, 48, 150, 25);
        summaryCard.add(spentLabel);

        UIUtils.label(summaryCard, "Remaining:", UIUtils.F_BODY, UIUtils.TEXT_MID, 550, 48);
        remainingLabel = new JLabel();
        remainingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        remainingLabel.setForeground(UIUtils.GREEN);
        remainingLabel.setBounds(660, 48, 150, 25);
        summaryCard.add(remainingLabel);

        progressWrapper = new JPanel(null);
        progressWrapper.setBounds(20, 78, 1030, 14);
        progressWrapper.setOpaque(false);
        summaryCard.add(progressWrapper);

        gridCard = UIUtils.card(20, 190, 1070, 530);
        gridCard.setLayout(null);
        contentPanel.add(gridCard);
    }


    private JToggleButton createPillToggleButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text);
        btn.setFont(UIUtils.F_SMALL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        applyToggleStyle(btn, selected);


        btn.addItemListener(e -> applyToggleStyle(btn, btn.isSelected()));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.isSelected()) {
                    btn.setBackground(UIUtils.TEXT_LIGHT.brighter());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.isSelected()) {
                    btn.setBackground(UIUtils.TEXT_LIGHT);
                }
            }
        });

        return btn;
    }


    private void applyToggleStyle(JToggleButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(UIUtils.BLUE);
            btn.setForeground(Color.WHITE);
            // Subtle border for definition
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIUtils.BLUE.darker(), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        } else {
            btn.setBackground(UIUtils.TEXT_LIGHT);
            btn.setForeground(UIUtils.TEXT_DARK);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        }
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(UIUtils.TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private void navigate(int delta) {
        if (isMonthly) {
            currentMonth = currentMonth.plusMonths(delta);
        } else {
            currentYear += delta;
        }
        refreshData();
    }

    public void refreshData() {
        if (isMonthly) {
            periodLabel.setText(currentMonth.format(MONTH_FMT));
        } else {
            periodLabel.setText(String.valueOf(currentYear));
        }

        Component[] comps = summaryCard.getComponents();
        if (comps.length > 0 && comps[0] instanceof JLabel) {
            ((JLabel) comps[0]).setText(isMonthly ? "Monthly Summary" : "Yearly Summary");
        }

        List<Budget> budgets;
        String gridTitle;
        if (isMonthly) {
            budgets = budgetDAO.getMonthlyBudgets(currentUser.getId(), currentMonth);
            gridTitle = "Monthly Budget Categories – " + currentMonth.format(MONTH_FMT);
        } else {
            budgets = budgetDAO.getYearlyBudgets(currentUser.getId(), currentYear);
            gridTitle = "Yearly Budget Categories – " + currentYear;
        }

        double totalBudgeted = budgets.stream().mapToDouble(Budget::getAmount).sum();
        double totalSpent = budgets.stream().mapToDouble(Budget::getSpent).sum();
        double remaining = totalBudgeted - totalSpent;
        int overallPct = totalBudgeted > 0 ? (int)((totalSpent / totalBudgeted) * 100) : 0;

        budgetedLabel.setText(CurrencyFormatter.format(totalBudgeted));
        spentLabel.setText(CurrencyFormatter.format(totalSpent));
        remainingLabel.setText(CurrencyFormatter.format(remaining));
        remainingLabel.setForeground(remaining >= 0 ? UIUtils.GREEN : UIUtils.RED);

        progressWrapper.removeAll();
        progressWrapper.add(UIUtils.buildProgressBar(0, 0, 1030, 14, overallPct, UIUtils.ORANGE));
        progressWrapper.revalidate();
        progressWrapper.repaint();

        gridCard.removeAll();
        UIUtils.label(gridCard, gridTitle, UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 14);
        UIUtils.separator(gridCard, 20, 46, 1030);

        int startX = 20;
        int startY = 58;
        int col = 0;
        int row = 0;

        for (Budget b : budgets) {
            int x = startX + col * (CARD_WIDTH + HORIZONTAL_GAP);
            int y = startY + row * (CARD_HEIGHT + VERTICAL_GAP);

            String[] data = new String[]{
                    b.getCategoryName(),
                    CurrencyFormatter.format(b.getSpent()),
                    CurrencyFormatter.format(b.getAmount()),
                    String.valueOf(b.getPercentage())
            };
            JPanel card = buildBudgetCard(x, y, CARD_WIDTH, CARD_HEIGHT, data,
                    budgetColors[(col + row * COLUMNS) % budgetColors.length], b);
            gridCard.add(card);

            col++;
            if (col == COLUMNS) {
                col = 0;
                row++;
            }
        }

        if (budgets.isEmpty()) {
            String emptyMsg = isMonthly ? "No budgets set for this month." : "No budgets set for this year.";
            JLabel empty = new JLabel(emptyMsg, SwingConstants.CENTER);
            empty.setFont(UIUtils.F_BODY);
            empty.setForeground(UIUtils.TEXT_LIGHT);
            empty.setBounds(0, 200, 1070, 40);
            gridCard.add(empty);
        }

        gridCard.revalidate();
        gridCard.repaint();

        int totalHeight = 190 + gridCard.getPreferredSize().height + 40;
        contentPanel.setPreferredSize(new Dimension(1110, totalHeight));
        contentPanel.revalidate();
    }

    private JPanel buildBudgetCard(int x, int y, int w, int h, String[] data, Color accent, Budget budget) {
        JPanel pn = UIUtils.card(x, y, w, h);
        UIUtils.label(pn, data[0], UIUtils.F_SECTION, UIUtils.TEXT_DARK, 14, 12);
        UIUtils.label(pn, data[1] + " / " + data[2], UIUtils.F_SMALL, UIUtils.TEXT_LIGHT, 14, 36);
        int pct = Integer.parseInt(data[3]);
        Color barColor = pct >= 90 ? UIUtils.RED : (pct >= 70 ? UIUtils.ORANGE : UIUtils.GREEN);
        pn.add(UIUtils.buildProgressBar(14, 58, w - 28, 10, pct, barColor));
        JLabel pctLbl = new JLabel(pct + "%");
        pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pctLbl.setForeground(barColor);
        pctLbl.setBounds(14, 72, w - 28, 20);
        pn.add(pctLbl);

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setBounds(w - 170, 6, 160, 32);

        JButton editBtn = new JButton("Edit");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editBtn.setForeground(UIUtils.TEXT_DARK);
        editBtn.setBackground(UIUtils.WHITE);
        editBtn.setBorderPainted(false);
        editBtn.setContentAreaFilled(false);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.setPreferredSize(new Dimension(70, 28));
        editBtn.addActionListener(e -> {
            AddEditBudgetDialog dialog = new AddEditBudgetDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, budget);
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshData();
        });

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setBackground(UIUtils.RED);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setOpaque(true);
        deleteBtn.setPreferredSize(new Dimension(70, 28));
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete budget for '" + budget.getCategoryName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && budgetDAO.delete(budget.getId())) {
                refreshData();
            }
        });

        actions.add(editBtn);
        actions.add(deleteBtn);
        pn.add(actions);

        return pn;
    }
}