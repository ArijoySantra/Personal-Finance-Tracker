package view;

import database.TransactionDAO;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class SummaryPanel extends JPanel {

    private User currentUser;
    private TransactionDAO transactionDAO = new TransactionDAO();

    private JLabel totalIncomeLabel, totalExpenseLabel, netSavingsLabel, savingsRateLabel;

    private JTable monthlyTable, yearlyTable;
    private DefaultTableModel monthlyModel, yearlyModel;

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US);

    public SummaryPanel(User user) {
        this.currentUser = user;
        setLayout(null);
        setBackground(UIUtils.BG_PAGE);
        setPreferredSize(new Dimension(1110, 750));

        totalIncomeLabel = buildStatCard(20, 20, 250, 110, "Total Income", CurrencyFormatter.format(0), "", UIUtils.GREEN);
        totalExpenseLabel = buildStatCard(290, 20, 250, 110, "Total Expenses", CurrencyFormatter.format(0), "", UIUtils.RED);
        netSavingsLabel = buildStatCard(560, 20, 250, 110, "Net Savings", CurrencyFormatter.format(0), "", UIUtils.BLUE);
        savingsRateLabel = buildStatCard(830, 20, 250, 110, "Savings Rate", "0%", "", UIUtils.ORANGE);

        add(totalIncomeLabel.getParent());
        add(totalExpenseLabel.getParent());
        add(netSavingsLabel.getParent());
        add(savingsRateLabel.getParent());

        JPanel monthlyCard = UIUtils.card(20, 150, 1070, 280);
        add(monthlyCard);
        UIUtils.label(monthlyCard, "Monthly Breakdown", UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 14);
        UIUtils.separator(monthlyCard, 20, 46, 1030);

        String[] monthlyCols = {"Month", "Income", "Expenses", "Savings", "Savings Rate"};
        monthlyModel = new DefaultTableModel(monthlyCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        monthlyTable = new JTable(monthlyModel);
        styleTable(monthlyTable);
        centerAlignAllColumns(monthlyTable);
        JScrollPane monthlyScroll = new JScrollPane(monthlyTable);
        monthlyScroll.setBounds(20, 52, 1030, 215);
        monthlyScroll.setBorder(BorderFactory.createEmptyBorder());
        monthlyScroll.getViewport().setBackground(UIUtils.WHITE);
        monthlyCard.add(monthlyScroll);

        JPanel yearlyCard = UIUtils.card(20, 450, 1070, 260);
        add(yearlyCard);
        UIUtils.label(yearlyCard, "Yearly Summary", UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 14);
        UIUtils.separator(yearlyCard, 20, 46, 1030);

        String[] yearlyCols = {"Year", "Total Income", "Total Expenses", "Net Savings", "Avg. Savings Rate"};
        yearlyModel = new DefaultTableModel(yearlyCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        yearlyTable = new JTable(yearlyModel);
        styleTable(yearlyTable);
        centerAlignAllColumns(yearlyTable);
        JScrollPane yearlyScroll = new JScrollPane(yearlyTable);
        yearlyScroll.setBounds(20, 52, 1030, 195);
        yearlyScroll.setBorder(BorderFactory.createEmptyBorder());
        yearlyScroll.getViewport().setBackground(UIUtils.WHITE);
        yearlyCard.add(yearlyScroll);

        refreshData();
    }

    private JLabel buildStatCard(int x, int y, int w, int h, String title, String value, String change, Color accent) {
        JPanel pn = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                g2.dispose();
            }
        };
        pn.setBounds(x, y, w, h);
        pn.setBackground(UIUtils.WHITE);
        pn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel ttl = new JLabel(title);
        ttl.setFont(UIUtils.F_SMALL);
        ttl.setForeground(UIUtils.TEXT_LIGHT);
        ttl.setBounds(14, 14, w - 28, 20);
        pn.add(ttl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(accent);
        val.setBounds(14, 36, w - 28, 30);
        pn.add(val);

        if (!change.isEmpty()) {
            JLabel chg = new JLabel(change);
            chg.setFont(UIUtils.F_SMALL);
            chg.setForeground(change.startsWith("+") ? UIUtils.GREEN : UIUtils.RED);
            chg.setBounds(14, 68, w - 28, 20);
            pn.add(chg);
        }
        return val;
    }

    private void styleTable(JTable table) {
        table.setFont(UIUtils.F_BODY);
        table.setRowHeight(36);
        table.setBackground(UIUtils.WHITE);
        table.setForeground(UIUtils.TEXT_DARK);
        table.setGridColor(UIUtils.BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(UIUtils.F_SMALL);
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(UIUtils.TEXT_LIGHT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR));
        table.setSelectionBackground(UIUtils.BLUE_LIGHT);
        table.setSelectionForeground(UIUtils.TEXT_DARK);
    }

    private void centerAlignAllColumns(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public void refreshData() {
        int currentYear = YearMonth.now().getYear();
        List<YearSummary> yearlyData = new ArrayList<>();
        double grandTotalIncome = 0, grandTotalExpense = 0;

        for (int year = currentYear - 3; year <= currentYear; year++) {
            double yearIncome = 0, yearExpense = 0;
            for (int month = 1; month <= 12; month++) {
                YearMonth ym = YearMonth.of(year, month);
                yearIncome += transactionDAO.getTotalIncome(currentUser.getId(), ym);
                yearExpense += transactionDAO.getTotalExpense(currentUser.getId(), ym);
            }
            double net = yearIncome - yearExpense;
            double rate = yearIncome > 0 ? (net / yearIncome) * 100 : 0;
            yearlyData.add(new YearSummary(year, yearIncome, yearExpense, net, rate));

            if (year == currentYear) {
                grandTotalIncome = yearIncome;
                grandTotalExpense = yearExpense;
            }
        }

        double net = grandTotalIncome - grandTotalExpense;
        double rate = grandTotalIncome > 0 ? (net / grandTotalIncome) * 100 : 0;
        totalIncomeLabel.setText(CurrencyFormatter.format(grandTotalIncome));
        totalExpenseLabel.setText(CurrencyFormatter.format(grandTotalExpense));
        netSavingsLabel.setText(CurrencyFormatter.format(net));
        netSavingsLabel.setForeground(net >= 0 ? UIUtils.GREEN : UIUtils.RED);
        savingsRateLabel.setText(String.format("%.1f%%", rate));
        savingsRateLabel.setForeground(rate >= 0 ? UIUtils.GREEN : UIUtils.RED);

        monthlyModel.setRowCount(0);
        YearMonth current = YearMonth.now();
        for (int i = 0; i <= 5; i++) {
            YearMonth ym = current.minusMonths(i);
            double inc = transactionDAO.getTotalIncome(currentUser.getId(), ym);
            double exp = transactionDAO.getTotalExpense(currentUser.getId(), ym);
            double sav = inc - exp;
            double savRate = inc > 0 ? (sav / inc) * 100 : 0;
            monthlyModel.addRow(new Object[]{
                    ym.format(MONTH_FMT),
                    CurrencyFormatter.format(inc),
                    CurrencyFormatter.format(exp),
                    CurrencyFormatter.format(sav),
                    String.format("%.1f%%", savRate)
            });
        }

        yearlyModel.setRowCount(0);
        Collections.reverse(yearlyData);
        for (YearSummary ys : yearlyData) {
            yearlyModel.addRow(new Object[]{
                    String.valueOf(ys.year),
                    CurrencyFormatter.format(ys.income),
                    CurrencyFormatter.format(ys.expense),
                    CurrencyFormatter.format(ys.net),
                    String.format("%.1f%%", ys.rate)
            });
        }
    }

    private static class YearSummary {
        int year;
        double income, expense, net, rate;
        YearSummary(int y, double i, double e, double n, double r) {
            year = y; income = i; expense = e; net = n; rate = r;
        }
    }
}