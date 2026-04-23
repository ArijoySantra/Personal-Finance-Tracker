package view;

import model.*;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import database.TransactionDAO;

public class TransactionsPanel extends JPanel {

    private User currentUser;
    private YearMonth currentYearMonth;
    private JLabel monthLabel;
    private JPanel transactionListPanel;
    private JLabel countLabel;
    private JLabel totalLabel;
    private TransactionDAO transactionDAO;
    private List<JCheckBox> rowCheckBoxes = new java.util.ArrayList<>();

    private Runnable onDataChangedCallback;

    public TransactionsPanel(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        this.currentYearMonth = YearMonth.now();

        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_PAGE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainPanel(), BorderLayout.CENTER);
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChangedCallback = callback;
    }

    private void notifyDataChanged() {
        if (onDataChangedCallback != null) {
            onDataChangedCallback.run();
        }
    }

    private JPanel buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Transactions");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton deleteSelectedBtn = new JButton("Delete Selected");
        deleteSelectedBtn.setFont(UIUtils.F_SMALL);
        deleteSelectedBtn.setForeground(UIUtils.RED);
        deleteSelectedBtn.setBackground(UIUtils.WHITE);
        deleteSelectedBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.RED, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        deleteSelectedBtn.setFocusPainted(false);
        deleteSelectedBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteSelectedBtn.addActionListener(e -> deleteSelectedTransactions());

        JButton addBtn = UIUtils.accentButton("+ Add Transaction", UIUtils.BLUE);
        addBtn.setPreferredSize(new Dimension(160, 36));
        addBtn.addActionListener(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshTransactions();
            }
        });

        rightPanel.add(deleteSelectedBtn);
        rightPanel.add(addBtn);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 8));
        main.setOpaque(false);
        main.add(buildMonthNav(), BorderLayout.NORTH);
        main.add(buildCardPanel(), BorderLayout.CENTER);
        return main;
    }

    private JPanel buildMonthNav() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UIUtils.WHITE);
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        monthLabel = new JLabel(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)),
                SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        monthLabel.setForeground(UIUtils.TEXT_DARK);

        JButton prev = navButton("‹");
        JButton next = navButton("›");

        prev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            monthLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)));
            refreshTransactions();
        });
        next.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            monthLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)));
            refreshTransactions();
        });

        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        return nav;
    }

    private JPanel buildCardPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIUtils.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));

        JPanel summary = new JPanel(new BorderLayout());
        summary.setBackground(UIUtils.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        countLabel = new JLabel();
        countLabel.setFont(UIUtils.F_SMALL);
        countLabel.setForeground(UIUtils.TEXT_LIGHT);

        totalLabel = new JLabel();
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(UIUtils.GREEN);

        summary.add(countLabel, BorderLayout.WEST);
        summary.add(totalLabel, BorderLayout.EAST);
        card.add(summary, BorderLayout.NORTH);

        transactionListPanel = new JPanel();
        transactionListPanel.setLayout(new BoxLayout(transactionListPanel, BoxLayout.Y_AXIS));
        transactionListPanel.setBackground(UIUtils.WHITE);
        transactionListPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JScrollPane scrollPane = new JScrollPane(transactionListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);

        refreshTransactions();
        return card;
    }

    public void refreshTransactions() {
        transactionListPanel.removeAll();
        rowCheckBoxes.clear();

        List<Transaction> transactions = transactionDAO.getByUserAndMonth(currentUser.getId(), currentYearMonth);

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (Transaction t : transactions) {
            if ("INCOME".equals(t.getType())) {
                totalIncome += t.getAmount();
            } else {
                totalExpense += t.getAmount();
            }
            JPanel rowPanel = buildRowFromTransaction(t);
            transactionListPanel.add(rowPanel);
        }

        double net = totalIncome - totalExpense;

        countLabel.setText("Transactions: " + transactions.size());
        totalLabel.setText("Net: " + CurrencyFormatter.format(net));
        totalLabel.setForeground(net >= 0 ? UIUtils.GREEN : UIUtils.RED);

        transactionListPanel.revalidate();
        transactionListPanel.repaint();

        notifyDataChanged();
    }

    private JPanel buildRowFromTransaction(Transaction t) {
        String emoji = getEmojiForCategory(t.getCategoryName());
        String name = t.getDescription();
        String source = t.getSourceName();
        double amount = t.getAmount();
        String date = t.getTransactionDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        boolean income = t.getType().equals("INCOME");

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(UIUtils.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.putClientProperty("transactionId", t.getId());
        rowCheckBoxes.add(checkBox);
        row.add(checkBox, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 0));
        centerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(emoji, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(income ? new Color(0xE6F9EE) : new Color(0xFDE8E8));
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setPreferredSize(new Dimension(42, 42));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(UIUtils.TEXT_DARK);
        JLabel sourceLabel = new JLabel(source);
        sourceLabel.setFont(UIUtils.F_SMALL);
        sourceLabel.setForeground(UIUtils.TEXT_LIGHT);
        textPanel.add(nameLabel);
        textPanel.add(sourceLabel);

        centerPanel.add(iconLabel, BorderLayout.WEST);
        centerPanel.add(textPanel, BorderLayout.CENTER);
        row.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        String amtStr = (income ? "+" : "-") + CurrencyFormatter.format(amount);
        JLabel amountLabel = new JLabel(amtStr);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amountLabel.setForeground(income ? UIUtils.GREEN : UIUtils.RED);
        rightPanel.add(amountLabel, gbc);

        gbc.gridy = 1;
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(UIUtils.F_SMALL);
        dateLabel.setForeground(UIUtils.TEXT_LIGHT);
        rightPanel.add(dateLabel, gbc);

        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deleteBtn.setForeground(UIUtils.RED);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> deleteSingleTransaction(t.getId()));
        rightPanel.add(deleteBtn, gbc);

        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(income ? UIUtils.GREEN : UIUtils.RED);
                g2.fillRoundRect(0, 2, 4, getHeight() - 4, 4, 4);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(4, 0));
        bar.setOpaque(false);

        JPanel rightWrapper = new JPanel(new BorderLayout(8, 0));
        rightWrapper.setOpaque(false);
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        rightWrapper.add(bar, BorderLayout.EAST);

        row.add(rightWrapper, BorderLayout.EAST);

        return row;
    }

    private void deleteSingleTransaction(int transactionId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this transaction permanently?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (transactionDAO.delete(transactionId)) {
                refreshTransactions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete transaction.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedTransactions() {
        java.util.List<Integer> selectedIds = new java.util.ArrayList<>();
        for (JCheckBox cb : rowCheckBoxes) {
            if (cb.isSelected()) {
                Integer id = (Integer) cb.getClientProperty("transactionId");
                if (id != null) {
                    selectedIds.add(id);
                }
            }
        }
        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No transactions selected.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + selectedIds.size() + " selected transaction(s)?",
                "Confirm Bulk Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int deleted = 0;
            for (int id : selectedIds) {
                if (transactionDAO.delete(id)) {
                    deleted++;
                }
            }
            JOptionPane.showMessageDialog(this, "Deleted " + deleted + " transaction(s).");
            refreshTransactions();
        }
    }

    private String getEmojiForCategory(String category) {
        if (category == null) return "💰";
        String cat = category.toLowerCase();
        if (cat.contains("food") || cat.contains("dining")) return "🍽";
        if (cat.contains("shop")) return "🛍";
        if (cat.contains("transport")) return "🚌";
        if (cat.contains("salary")) return "💼";
        if (cat.contains("health")) return "🏥";
        if (cat.contains("entertainment")) return "🎬";
        if (cat.contains("housing") || cat.contains("rent")) return "🏠";
        if (cat.contains("investment")) return "📈";
        return "💰";
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        btn.setForeground(UIUtils.TEXT_LIGHT);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}