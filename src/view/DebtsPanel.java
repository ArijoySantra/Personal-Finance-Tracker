package view;

import database.CategoryDAO;
import database.DebtDAO;
import database.TransactionDAO;
import model.Category;
import model.Debt;
import model.Transaction;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DebtsPanel extends JPanel {

    private User currentUser;
    private DebtDAO debtDAO = new DebtDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Debt> debtList;

    private JLabel totalDebtLabel, paidThisMonthLabel, interestMonthLabel, debtFreeEstLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public DebtsPanel(User user) {
        this.currentUser = user;
        setLayout(null);
        setBackground(UIUtils.BG_PAGE);
        setPreferredSize(new Dimension(1110, 650));

        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        tb.setBounds(20, 14, 600, 50);
        tb.setOpaque(false);
        JButton addBtn = UIUtils.accentButton("+ Add Debt", UIUtils.RED);
        addBtn.addActionListener(e -> {
            AddEditDebtDialog dialog = new AddEditDebtDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshData();
        });
        tb.add(addBtn);
        add(tb);

        totalDebtLabel = buildModernStatCard(20, 70, 250, 100, "Total Debt", CurrencyFormatter.format(0), UIUtils.RED);
        paidThisMonthLabel = buildModernStatCard(290, 70, 250, 100, "Paid This Month", CurrencyFormatter.format(0), UIUtils.GREEN);
        interestMonthLabel = buildModernStatCard(560, 70, 250, 100, "Interest/Month", CurrencyFormatter.format(0), UIUtils.ORANGE);
        debtFreeEstLabel = buildModernStatCard(830, 70, 250, 100, "Debt-Free Est.", "—", UIUtils.BLUE);

        String[] cols = {"Debt Name", "Type", "Total", "Paid", "Remaining", "Interest%", "EMI", "Next Due", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Actions column editable
            }
        };
        table = new JTable(tableModel);
        styleTableModern();

        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(65);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        table.getColumnModel().getColumn(7).setPreferredWidth(85);
        table.getColumnModel().getColumn(8).setPreferredWidth(70);
        table.getColumnModel().getColumn(9).setPreferredWidth(160);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getColumn("Total").setCellRenderer(new AmountRenderer());
        table.getColumn("Paid").setCellRenderer(new AmountRenderer());
        table.getColumn("Remaining").setCellRenderer(new AmountRenderer());
        table.getColumn("EMI").setCellRenderer(new AmountRenderer());

        table.getColumnModel().getColumn(9).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new ActionsEditor());

        JPanel tblCard = UIUtils.card(20, 188, 1070, 430);
        tblCard.setLayout(null);
        add(tblCard);

        UIUtils.label(tblCard, "Debt Details", UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 14);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 46, 1050, 374);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.WHITE);
        tblCard.add(scrollPane);

        refreshData();
    }

    private JLabel buildModernStatCard(int x, int y, int w, int h, String title, String value, Color accent) {
        JPanel pn = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                g2.setColor(UIUtils.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        pn.setBounds(x, y, w, h);
        pn.setBackground(UIUtils.WHITE);
        pn.setOpaque(true);
        add(pn);

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

        return val;
    }

    private void styleTableModern() {
        table.setFont(UIUtils.F_BODY);
        table.setRowHeight(42);
        table.setBackground(UIUtils.WHITE);
        table.setForeground(UIUtils.TEXT_DARK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(8, 4));
        table.setSelectionBackground(UIUtils.BLUE_LIGHT);
        table.setSelectionForeground(UIUtils.TEXT_DARK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 248, 248));
        table.getTableHeader().setForeground(UIUtils.TEXT_DARK);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UIUtils.BORDER_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void refreshData() {
        debtList = debtDAO.getAllByUser(currentUser.getId());
        tableModel.setRowCount(0);

        double totalRemaining = 0;
        double totalMonthlyInterest = 0;
        double totalPaidThisMonth = 0;
        LocalDate now = LocalDate.now();

        for (Debt d : debtList) {
            totalRemaining += d.getRemaining();
            double monthlyInterest = (d.getRemaining() * d.getInterestRate() / 100) / 12;
            totalMonthlyInterest += monthlyInterest;

            double paid = d.getAmount() - d.getRemaining();

            String emiDisplay = (d.getEmiAmount() > 0) ? CurrencyFormatter.format(d.getEmiAmount()) : "—";

            String nextDue = (d.getDueDate() != null) ?
                    d.getDueDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM")) : "—";

            String status = d.getRemaining() <= 0 ? "Cleared" : "Active";

            double paidThisMonth = getPaymentsForDebtThisMonth(d.getId());
            totalPaidThisMonth += paidThisMonth;

            tableModel.addRow(new Object[]{
                    d.getName(),
                    d.getType(),
                    d.getAmount(),
                    paid,
                    d.getRemaining(),
                    d.getInterestRate(),
                    emiDisplay,
                    nextDue,
                    status,
                    "ACTIONS"
            });
        }

        // Update stat cards
        totalDebtLabel.setText(CurrencyFormatter.format(totalRemaining));
        interestMonthLabel.setText(CurrencyFormatter.format(totalMonthlyInterest));
        paidThisMonthLabel.setText(CurrencyFormatter.format(totalPaidThisMonth));

        if (!debtList.isEmpty() && totalRemaining > 0) {
            double avgMonthlyPayment = getAverageMonthlyPayment();
            if (avgMonthlyPayment > 0) {
                int months = (int) Math.ceil(totalRemaining / avgMonthlyPayment);
                LocalDate est = now.plusMonths(months);
                debtFreeEstLabel.setText(est.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            } else {
                debtFreeEstLabel.setText("—");
            }
        } else {
            debtFreeEstLabel.setText("—");
        }

        revalidate();
        repaint();
    }

    private double getPaymentsForDebtThisMonth(int debtId) {
        return 0.0;
    }

    private double getAverageMonthlyPayment() {
        return 0.0;
    }

    private void payDebt(Debt debt) {
        double defaultAmount = debt.getEmiAmount() > 0 ? debt.getEmiAmount() : debt.getRemaining();
        String input = JOptionPane.showInputDialog(this,
                "Enter payment amount for '" + debt.getName() + "':",
                String.format("%.2f", defaultAmount));
        if (input == null || input.trim().isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
                return;
            }
            if (amount > debt.getRemaining()) {
                JOptionPane.showMessageDialog(this, "Cannot pay more than remaining balance.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Pay %s towards '%s'?", CurrencyFormatter.format(amount), debt.getName()),
                "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Transaction t = new Transaction();
        t.setUserId(currentUser.getId());
        t.setAccountId(null); // Optionally let user choose account
        t.setCardId(null);

        int debtCategoryId = categoryDAO.getOrCreateDebtPaymentCategory(currentUser.getId());
        if (debtCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Could not find or create 'Debt Payment' category.");
            return;
        }
        t.setCategoryId(debtCategoryId);
        t.setAmount(amount);
        t.setType("EXPENSE");
        t.setDescription("Payment: " + debt.getName());
        t.setTransactionDate(Date.valueOf(LocalDate.now()));

        boolean txSaved = transactionDAO.add(t);
        if (!txSaved) {
            JOptionPane.showMessageDialog(this, "Failed to record transaction.");
            return;
        }

        debt.setRemaining(debt.getRemaining() - amount);
        boolean debtUpdated = debtDAO.update(debt);
        if (!debtUpdated) {
            JOptionPane.showMessageDialog(this, "Failed to update debt balance.");
            return;
        }

        refreshData();
        JOptionPane.showMessageDialog(this, "Payment recorded successfully.");
    }

    // ------------------ Renderers & Editors ------------------
    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                double val = ((Number) value).doubleValue();
                setText(CurrencyFormatter.format(val));
                setHorizontalAlignment(SwingConstants.CENTER);
                if (val < 0) setForeground(UIUtils.RED);
                else if (val > 0) setForeground(UIUtils.GREEN);
                else setForeground(UIUtils.TEXT_DARK);
            } else if (value instanceof String && ((String) value).startsWith("₹")) {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
            return c;
        }
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton payBtn = new JButton("Pay");
        private final JButton editBtn = new JButton("Edit");
        private final JButton deleteBtn = new JButton("Delete");

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 4));
            setOpaque(true);
            styleActionButton(payBtn, UIUtils.BLUE);
            styleActionButton(editBtn, UIUtils.TEXT_DARK);
            styleActionButton(deleteBtn, UIUtils.RED);
            add(payBtn);
            add(editBtn);
            add(deleteBtn);
        }

        private void styleActionButton(JButton btn, Color color) {
            btn.setFont(UIUtils.F_SMALL);
            btn.setForeground(color);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setForeground(color.darker()); }
                @Override public void mouseExited(MouseEvent e)  { btn.setForeground(color); }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? UIUtils.BLUE_LIGHT : UIUtils.WHITE);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton payBtn, editBtn, deleteBtn;
        private int currentRow;

        public ActionsEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
            payBtn = new JButton("Pay");
            editBtn = new JButton("Edit");
            deleteBtn = new JButton("Delete");
            styleActionButton(payBtn, UIUtils.BLUE);
            styleActionButton(editBtn, UIUtils.TEXT_DARK);
            styleActionButton(deleteBtn, UIUtils.RED);

            payBtn.addActionListener(e -> { payDebt(currentRow); fireEditingStopped(); });
            editBtn.addActionListener(e -> { editDebt(currentRow); fireEditingStopped(); });
            deleteBtn.addActionListener(e -> { deleteDebt(currentRow); fireEditingStopped(); });

            panel.add(payBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private void styleActionButton(JButton btn, Color color) {
            btn.setFont(UIUtils.F_SMALL);
            btn.setForeground(color);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { btn.setForeground(color.darker()); }
                @Override public void mouseExited(MouseEvent e)  { btn.setForeground(color); }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(isSelected ? UIUtils.BLUE_LIGHT : UIUtils.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "ACTIONS";
        }
    }

    private void payDebt(int row) {
        Debt d = debtList.get(row);
        if (d.getRemaining() <= 0) {
            JOptionPane.showMessageDialog(this, "This debt is already cleared.");
            return;
        }
        payDebt(d);
    }

    private void editDebt(int row) {
        Debt d = debtList.get(row);
        AddEditDebtDialog dialog = new AddEditDebtDialog((JFrame) SwingUtilities.getWindowAncestor(this), currentUser, d);
        dialog.setVisible(true);
        if (dialog.isSaved()) refreshData();
    }

    private void deleteDebt(int row) {
        Debt d = debtList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete debt '" + d.getName() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && debtDAO.delete(d.getId())) {
            refreshData();
        }
    }
}