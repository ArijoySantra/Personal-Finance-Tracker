package view;

import database.*;
import model.*;
import utils.CurrencyFormatter;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class OverviewPanel extends JPanel {

    private User currentUser;
    private AccountDAO accountDAO = new AccountDAO();
    private CardDAO cardDAO = new CardDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private BudgetDAO budgetDAO = new BudgetDAO();
    private YearMonth currentMonth = YearMonth.now();

    // Dynamic labels
    private JLabel summaryBalanceLabel, summaryCreditLabel, summaryNetLabel;
    private JLabel thisMonthIncomeLabel, thisMonthExpenseLabel, thisMonthNetLabel;
    private JLabel lastMonthIncomeLabel, lastMonthExpenseLabel, lastMonthNetLabel;
    private JLabel accountTotalLabel, creditCardBalanceLabel, creditCardPercentLabel;
    private JPanel thisMonthDonutWrapper, lastMonthDonutWrapper;
    private JPanel creditProgressWrapper, accountsContainer;
    private JPanel incomeExpenseChartWrapper, budgetsContainer;

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US);
    private static final Color[] CHART_COLORS = {
            new Color(67, 160, 71), new Color(30, 136, 229), new Color(251, 140, 0),
            new Color(0, 188, 212), new Color(216, 67, 21), new Color(142, 36, 170)
    };

    public OverviewPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIUtils.BG_PAGE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.BG_PAGE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(buildTopRowPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(buildAccountsCard());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(buildCreditCardPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(buildIncomeExpenseChartPanel());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(buildBudgetsPanel());

        refreshData();
    }

    private JPanel buildTopRowPanel() {
        JPanel row = new JPanel(new GridLayout(1, 3, 20, 0));
        row.setOpaque(false);
        row.add(buildSummaryCard());
        row.add(buildThisMonthCard());
        row.add(buildLastMonthCard());
        return row;
    }

    private JPanel buildSummaryCard() {
        JPanel card = UIUtils.card(0, 0, 0, 140);
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Summary");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        card.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        card.add(new JLabel("Balance:"), gbc);
        gbc.gridx = 1;
        summaryBalanceLabel = new JLabel();
        summaryBalanceLabel.setFont(UIUtils.F_BODY);
        summaryBalanceLabel.setForeground(UIUtils.GREEN);
        summaryBalanceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(summaryBalanceLabel, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        card.add(new JLabel("Credit cards:"), gbc);
        gbc.gridx = 1;
        summaryCreditLabel = new JLabel();
        summaryCreditLabel.setFont(UIUtils.F_BODY);
        summaryCreditLabel.setForeground(UIUtils.RED);
        summaryCreditLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(summaryCreditLabel, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(230,232,236));
        sep.setPreferredSize(new Dimension(300, 1));
        card.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4; gbc.gridx = 0;
        summaryNetLabel = new JLabel();
        summaryNetLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        summaryNetLabel.setForeground(UIUtils.GREEN);
        summaryNetLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(summaryNetLabel, gbc);

        return card;
    }

    private JPanel buildThisMonthCard() {
        JPanel card = UIUtils.card(0, 0, 0, 140);
        card.setLayout(new BorderLayout(10, 0));
        JLabel title = new JLabel("This month");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        card.add(title, BorderLayout.NORTH);

        thisMonthDonutWrapper = new JPanel(new BorderLayout());
        thisMonthDonutWrapper.setOpaque(false);
        thisMonthDonutWrapper.setPreferredSize(new Dimension(100, 100));
        thisMonthDonutWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        card.add(thisMonthDonutWrapper, BorderLayout.WEST);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setOpaque(false);
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 10));

        thisMonthIncomeLabel = new JLabel();
        thisMonthIncomeLabel.setFont(UIUtils.F_BODY);
        thisMonthIncomeLabel.setForeground(UIUtils.GREEN);
        thisMonthIncomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        thisMonthExpenseLabel = new JLabel();
        thisMonthExpenseLabel.setFont(UIUtils.F_BODY);
        thisMonthExpenseLabel.setForeground(UIUtils.RED);
        thisMonthExpenseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        thisMonthNetLabel = new JLabel();
        thisMonthNetLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        thisMonthNetLabel.setForeground(UIUtils.GREEN);
        thisMonthNetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelsPanel.add(thisMonthIncomeLabel);
        labelsPanel.add(Box.createVerticalStrut(8));
        labelsPanel.add(thisMonthExpenseLabel);
        labelsPanel.add(Box.createVerticalStrut(8));
        labelsPanel.add(thisMonthNetLabel);

        card.add(labelsPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLastMonthCard() {
        JPanel card = UIUtils.card(0, 0, 0, 140);
        card.setLayout(new BorderLayout(10, 0));
        JLabel title = new JLabel("Last month");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        card.add(title, BorderLayout.NORTH);

        lastMonthDonutWrapper = new JPanel(new BorderLayout());
        lastMonthDonutWrapper.setOpaque(false);
        lastMonthDonutWrapper.setPreferredSize(new Dimension(100, 100));
        lastMonthDonutWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        card.add(lastMonthDonutWrapper, BorderLayout.WEST);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setOpaque(false);
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 10));

        lastMonthIncomeLabel = new JLabel();
        lastMonthIncomeLabel.setFont(UIUtils.F_BODY);
        lastMonthIncomeLabel.setForeground(UIUtils.GREEN);
        lastMonthIncomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lastMonthExpenseLabel = new JLabel();
        lastMonthExpenseLabel.setFont(UIUtils.F_BODY);
        lastMonthExpenseLabel.setForeground(UIUtils.RED);
        lastMonthExpenseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lastMonthNetLabel = new JLabel();
        lastMonthNetLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lastMonthNetLabel.setForeground(UIUtils.GREEN);
        lastMonthNetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelsPanel.add(lastMonthIncomeLabel);
        labelsPanel.add(Box.createVerticalStrut(8));
        labelsPanel.add(lastMonthExpenseLabel);
        labelsPanel.add(Box.createVerticalStrut(8));
        labelsPanel.add(lastMonthNetLabel);

        card.add(labelsPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildAccountsCard() {
        JPanel card = UIUtils.card(0, 0, 1070, 170);
        card.setLayout(new BorderLayout());
        JLabel title = new JLabel("Accounts");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        card.add(title, BorderLayout.NORTH);

        accountsContainer = new JPanel();
        accountsContainer.setLayout(new GridLayout(0, 2, 20, 8));
        accountsContainer.setOpaque(false);
        accountsContainer.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        card.add(accountsContainer, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 16, 12, 16));
        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.BORDER_COLOR);
        footer.add(sep, BorderLayout.NORTH);
        JLabel totalLbl = new JLabel("Total");
        totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLbl.setForeground(UIUtils.TEXT_DARK);
        footer.add(totalLbl, BorderLayout.WEST);
        accountTotalLabel = new JLabel();
        accountTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        accountTotalLabel.setForeground(UIUtils.GREEN);
        footer.add(accountTotalLabel, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildCreditCardPanel() {
        JPanel card = UIUtils.card(0, 0, 1070, 130);
        card.setLayout(new BorderLayout());
        JLabel title = new JLabel("Credit Cards");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        content.add(new JLabel("Total Outstanding:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        creditCardBalanceLabel = new JLabel();
        creditCardBalanceLabel.setFont(UIUtils.F_BODY);
        creditCardBalanceLabel.setForeground(UIUtils.RED);
        content.add(creditCardBalanceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        creditProgressWrapper = new JPanel(new BorderLayout());
        creditProgressWrapper.setOpaque(false);
        creditProgressWrapper.setPreferredSize(new Dimension(475, 12));
        content.add(creditProgressWrapper, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        creditCardPercentLabel = new JLabel("0%");
        creditCardPercentLabel.setFont(UIUtils.F_SMALL);
        creditCardPercentLabel.setForeground(UIUtils.TEXT_MID);
        content.add(creditCardPercentLabel, gbc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildIncomeExpenseChartPanel() {
        JPanel card = UIUtils.card(0, 0, 1070, 350);
        card.setLayout(new BorderLayout());
        JLabel title = new JLabel("Last 7 Days: Income vs Expenses");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        card.add(title, BorderLayout.NORTH);

        incomeExpenseChartWrapper = new JPanel(new BorderLayout());
        incomeExpenseChartWrapper.setOpaque(false);
        incomeExpenseChartWrapper.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        incomeExpenseChartWrapper.setPreferredSize(new Dimension(1000, 280));
        card.add(incomeExpenseChartWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildBudgetsPanel() {
        JPanel card = UIUtils.card(0, 0, 1070, 400);
        card.setLayout(new BorderLayout());
        JLabel title = new JLabel("Monthly Budgets");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 0));
        card.add(title, BorderLayout.NORTH);

        budgetsContainer = new JPanel();
        budgetsContainer.setLayout(new GridLayout(0, 2, 20, 12));
        budgetsContainer.setOpaque(false);
        budgetsContainer.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        card.add(budgetsContainer, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        double totalAssets = accountDAO.getTotalBalance(currentUser.getId());
        double creditDebt = cardDAO.getTotalOutstanding(currentUser.getId());
        double netWorth = totalAssets - creditDebt;

        summaryBalanceLabel.setText(CurrencyFormatter.format(totalAssets));
        summaryCreditLabel.setText("-" + CurrencyFormatter.format(creditDebt));
        summaryNetLabel.setText(CurrencyFormatter.format(netWorth));
        summaryNetLabel.setForeground(netWorth >= 0 ? UIUtils.GREEN : UIUtils.RED);

        List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getId());
        accountsContainer.removeAll();
        for (Account acc : accounts) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            JLabel name = new JLabel(acc.getAccountName());
            name.setFont(UIUtils.F_SMALL);
            name.setForeground(UIUtils.TEXT_MID);
            JLabel bal = new JLabel(CurrencyFormatter.format(acc.getBalance()));
            bal.setFont(UIUtils.F_SMALL);
            bal.setForeground(UIUtils.GREEN);
            row.add(name, BorderLayout.WEST);
            row.add(bal, BorderLayout.EAST);
            accountsContainer.add(row);
        }
        accountTotalLabel.setText(CurrencyFormatter.format(totalAssets));

        double monthIncome = transactionDAO.getTotalIncome(currentUser.getId(), currentMonth);
        double monthExpense = transactionDAO.getTotalExpense(currentUser.getId(), currentMonth);
        double monthNet = monthIncome - monthExpense;
        thisMonthIncomeLabel.setText(CurrencyFormatter.format(monthIncome));
        thisMonthExpenseLabel.setText("-" + CurrencyFormatter.format(monthExpense));
        thisMonthNetLabel.setText(CurrencyFormatter.format(monthNet));
        thisMonthNetLabel.setForeground(monthNet >= 0 ? UIUtils.GREEN : UIUtils.RED);

        thisMonthDonutWrapper.removeAll();
        thisMonthDonutWrapper.add(buildIncomeExpenseDonut(monthIncome, monthExpense), BorderLayout.CENTER);

        YearMonth lastMonth = currentMonth.minusMonths(1);
        double lastIncome = transactionDAO.getTotalIncome(currentUser.getId(), lastMonth);
        double lastExpense = transactionDAO.getTotalExpense(currentUser.getId(), lastMonth);
        double lastNet = lastIncome - lastExpense;
        lastMonthIncomeLabel.setText(CurrencyFormatter.format(lastIncome));
        lastMonthExpenseLabel.setText("-" + CurrencyFormatter.format(lastExpense));
        lastMonthNetLabel.setText(CurrencyFormatter.format(lastNet));
        lastMonthNetLabel.setForeground(lastNet >= 0 ? UIUtils.GREEN : UIUtils.RED);

        lastMonthDonutWrapper.removeAll();
        lastMonthDonutWrapper.add(buildIncomeExpenseDonut(lastIncome, lastExpense), BorderLayout.CENTER);

        creditCardBalanceLabel.setText("-" + CurrencyFormatter.format(creditDebt));
        double totalCreditLimit = cardDAO.getCardsByUser(currentUser.getId()).stream()
                .filter(c -> "Credit".equalsIgnoreCase(c.getCardType()))
                .mapToDouble(Card::getCreditLimit).sum();
        int pct = totalCreditLimit > 0 ? (int)((creditDebt / totalCreditLimit) * 100) : 0;
        creditProgressWrapper.removeAll();
        creditProgressWrapper.add(UIUtils.buildProgressBar(0, 0, 475, 12, pct, UIUtils.RED));
        creditCardPercentLabel.setText(pct + "%");

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        String[] labels = new String[7];
        int[] incomes = new int[7];
        int[] expenses = new int[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            labels[i] = date.format(DateTimeFormatter.ofPattern("EEE"));
            incomes[i] = (int) transactionDAO.getTotalIncomeForDate(currentUser.getId(), date);
            expenses[i] = (int) transactionDAO.getTotalExpenseForDate(currentUser.getId(), date);
        }
        incomeExpenseChartWrapper.removeAll();
        incomeExpenseChartWrapper.add(buildGroupedBarChart(labels, incomes, expenses), BorderLayout.CENTER);


        List<Budget> budgets = budgetDAO.getMonthlyBudgets(currentUser.getId(), currentMonth);
        budgets.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        budgetsContainer.removeAll();
        for (Budget b : budgets) {
            budgetsContainer.add(buildBudgetRow(b));
        }
        if (budgets.isEmpty()) {
            JLabel empty = new JLabel("No budgets set for this month.");
            empty.setFont(UIUtils.F_SMALL);
            empty.setForeground(UIUtils.TEXT_LIGHT);
            budgetsContainer.add(empty);
        }

        revalidate();
        repaint();
    }

    private JPanel buildIncomeExpenseDonut(double income, double expense) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (w == 0 || h == 0) return;

                double total = income + expense;
                int cx = w / 2, cy = h / 2;
                int outerR = Math.min(w, h) / 2 - 5;
                int innerR = outerR * 2 / 3;

                if (total == 0) {
                    g2.setColor(UIUtils.TEXT_LIGHT);
                    g2.drawString("No data", cx - 20, cy);
                    g2.dispose();
                    return;
                }

                double incomeAngle = (income / total) * 360;
                g2.setColor(UIUtils.GREEN);
                g2.fill(makeAnnularSlice(cx, cy, innerR, outerR, -90, incomeAngle - 2));

                if (expense > 0) {
                    g2.setColor(UIUtils.RED);
                    g2.fill(makeAnnularSlice(cx, cy, innerR, outerR, -90 + incomeAngle, (expense / total) * 360 - 2));
                }

                g2.setColor(UIUtils.WHITE);
                g2.fillOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

                double net = income - expense;
                g2.setColor(net >= 0 ? UIUtils.GREEN : UIUtils.RED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String netStr = CurrencyFormatter.format(net);
                if (netStr.length() > 8) {
                    netStr = net >= 1000 ? CurrencyFormatter.getCurrencyCode() + (int)(net / 1000) + "k" : netStr;
                }
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(netStr, cx - fm.stringWidth(netStr) / 2, cy + fm.getAscent() / 2 - 2);

                g2.dispose();
            }
        };
    }

    private Shape makeAnnularSlice(int cx, int cy, int inner, int outer, double startDeg, double arcDeg) {
        Area outerArea = new Area(new Arc2D.Double(cx - outer, cy - outer, outer*2, outer*2, startDeg, arcDeg, Arc2D.PIE));
        outerArea.subtract(new Area(new Ellipse2D.Double(cx - inner, cy - inner, inner*2, inner*2)));
        return outerArea;
    }

    private JPanel buildGroupedBarChart(String[] labels, int[] incomes, int[] expenses) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (w == 0 || h == 0) return;

                int padL = 40, padR = 20, padT = 30, padB = 35;
                int n = labels.length;
                int chartW = w - padL - padR;
                int chartH = h - padT - padB;
                int groupW = chartW / n;
                int barW = Math.max(8, (int)(groupW * 0.3));
                int maxV = 1;
                for (int v : incomes) maxV = Math.max(maxV, v);
                for (int v : expenses) maxV = Math.max(maxV, v);

                g2.setColor(new Color(230, 232, 236));
                for (int i = 0; i <= 4; i++) {
                    int gy = padT + (int)(chartH * (1.0 - i / 4.0));
                    g2.drawLine(padL, gy, padL + chartW, gy);
                }

                for (int i = 0; i < n; i++) {
                    int groupX = padL + i * groupW + groupW / 2;
                    int incomeH = (int)((double) incomes[i] / maxV * chartH);
                    int expenseH = (int)((double) expenses[i] / maxV * chartH);

                    if (incomeH > 0) {
                        g2.setPaint(new GradientPaint(groupX - barW - 2, padT + chartH - incomeH,
                                UIUtils.GREEN.brighter(), groupX - barW - 2, padT + chartH, UIUtils.GREEN));
                        g2.fillRoundRect(groupX - barW - 2, padT + chartH - incomeH, barW, incomeH, 4, 4);
                    }
                    if (expenseH > 0) {
                        g2.setPaint(new GradientPaint(groupX + 2, padT + chartH - expenseH,
                                UIUtils.RED.brighter(), groupX + 2, padT + chartH, UIUtils.RED));
                        g2.fillRoundRect(groupX + 2, padT + chartH - expenseH, barW, expenseH, 4, 4);
                    }

                    g2.setColor(UIUtils.TEXT_MID);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i], groupX - fm.stringWidth(labels[i]) / 2, h - 8);
                }

                g2.setColor(UIUtils.GREEN);
                g2.fillRect(w - 180, 6, 12, 12);
                g2.setColor(UIUtils.TEXT_MID);
                g2.drawString("Income", w - 163, 17);
                g2.setColor(UIUtils.RED);
                g2.fillRect(w - 100, 6, 12, 12);
                g2.setColor(UIUtils.TEXT_MID);
                g2.drawString("Expense", w - 83, 17);

                g2.dispose();
            }
        };
    }

    private JPanel buildBudgetRow(Budget b) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(new JLabel(b.getCategoryName()), BorderLayout.WEST);
        JLabel amt = new JLabel(CurrencyFormatter.format(b.getSpent()) + " / " + CurrencyFormatter.format(b.getAmount()));
        amt.setFont(UIUtils.F_SMALL);
        amt.setForeground(UIUtils.TEXT_LIGHT);
        top.add(amt, BorderLayout.EAST);
        row.add(top);

        int pct = b.getPercentage();
        Color barColor = pct >= 100 ? UIUtils.RED : (pct >= 70 ? UIUtils.ORANGE : UIUtils.GREEN);
        JPanel bar = UIUtils.buildProgressBar(0, 0, 400, 8, pct, barColor);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        row.add(bar);

        JLabel pctLbl = new JLabel(pct + "%");
        pctLbl.setFont(UIUtils.F_SMALL);
        pctLbl.setForeground(barColor);
        pctLbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        row.add(pctLbl);

        return row;
    }
}