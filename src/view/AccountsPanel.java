package view;

import database.AccountDAO;
import database.CardDAO;
import database.DebtDAO;
import model.Account;
import model.Card;
import model.Debt;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AccountsPanel extends JPanel {

    private User currentUser;
    private AccountDAO accountDAO;
    private CardDAO cardDAO;
    private DebtDAO debtDAO;
    private JPanel contentPanel;
    private JLabel totalAssetsLabel;
    private JLabel totalLiabilitiesLabel;

    private static final int CARD_WIDTH = 330;
    private static final int CARD_HEIGHT = 150;
    private static final int COLUMNS = 3;
    private static final int START_X = 20;
    private static final int HORIZONTAL_GAP = 20;
    private static final int VERTICAL_GAP = 20;
    private static final int BOTTOM_PADDING = 40;

    public AccountsPanel(User user) {
        this.currentUser = user;
        this.accountDAO = new AccountDAO();
        this.cardDAO = new CardDAO();
        this.debtDAO = new DebtDAO();

        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_PAGE);

        contentPanel = new JPanel(null);
        contentPanel.setBackground(UIUtils.BG_PAGE);
        rebuildUI();

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.BG_PAGE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        rebuildUI();
    }

    private void rebuildUI() {
        contentPanel.removeAll();


        JPanel tb = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        tb.setBounds(20, 14, 600, 50);
        tb.setOpaque(false);
        JButton addBtn = UIUtils.accentButton("+ Add Account", UIUtils.BLUE);
        addBtn.addActionListener(e -> {
            AddEditAccountDialog dialog = new AddEditAccountDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                rebuildUI();
            }
        });
        tb.add(addBtn);
        contentPanel.add(tb);

        List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getId());
        Color[] colorPalette = {
                UIUtils.BLUE, UIUtils.GREEN, UIUtils.PURPLE, UIUtils.ORANGE,
                new Color(0, 150, 136), new Color(233, 30, 99)
        };

        double totalAssets = 0.0;
        int x = START_X;
        int y = 72;
        int col = 0;

        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            totalAssets += acc.getBalance();

            Color accent = colorPalette[i % colorPalette.length];
            String[] cardData = {
                    acc.getAccountName(),
                    acc.getAccountType(),
                    CurrencyFormatter.format(acc.getBalance()),   // ✅ 动态货币
                    acc.getAccountType()
            };

            JPanel card = buildAccountCard(x, y, CARD_WIDTH, CARD_HEIGHT, cardData, accent, acc);
            contentPanel.add(card);

            col++;
            if (col == COLUMNS) {
                col = 0;
                x = START_X;
                y += CARD_HEIGHT + VERTICAL_GAP;
            } else {
                x += CARD_WIDTH + HORIZONTAL_GAP;
            }
        }

        if (accounts.isEmpty()) {
            JLabel emptyLabel = new JLabel("No accounts yet. Click '+ Add Account' to get started.", SwingConstants.CENTER);
            emptyLabel.setFont(UIUtils.F_BODY);
            emptyLabel.setForeground(UIUtils.TEXT_LIGHT);
            emptyLabel.setBounds(20, 72, 1070, 100);
            contentPanel.add(emptyLabel);
            y = 72 + 100;
        } else if (col != 0) {
            y += CARD_HEIGHT + VERTICAL_GAP;
        }

        double totalDebtRemaining = 0.0;
        List<Debt> debts = debtDAO.getAllByUser(currentUser.getId());
        for (Debt d : debts) {
            totalDebtRemaining += d.getRemaining();
        }

        double totalCreditOutstanding = 0.0;
        List<Card> cards = cardDAO.getCardsByUser(currentUser.getId());
        for (Card c : cards) {
            if ("Credit".equalsIgnoreCase(c.getCardType())) {
                totalCreditOutstanding += c.getCurrentBalance();
            }
        }

        double totalLiabilities = totalDebtRemaining + totalCreditOutstanding;


        JPanel sum = UIUtils.card(20, y + 10, 1070, 130);
        sum.setLayout(null);
        contentPanel.add(sum);

        UIUtils.label(sum, "Summary", UIUtils.F_SECTION, UIUtils.TEXT_DARK, 20, 14);
        UIUtils.separator(sum, 20, 45, 1030);

        UIUtils.label(sum, "Total Assets:", UIUtils.F_BODY, UIUtils.TEXT_MID, 20, 62);
        totalAssetsLabel = new JLabel(CurrencyFormatter.format(totalAssets));   // ✅ 动态货币
        totalAssetsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalAssetsLabel.setForeground(UIUtils.GREEN);
        totalAssetsLabel.setBounds(800, 62, 200, 30);
        sum.add(totalAssetsLabel);

        UIUtils.label(sum, "Total Liabilities:", UIUtils.F_BODY, UIUtils.TEXT_MID, 20, 94);
        totalLiabilitiesLabel = new JLabel("-" + CurrencyFormatter.format(totalLiabilities));   // ✅ 动态货币 + 负号
        totalLiabilitiesLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLiabilitiesLabel.setForeground(UIUtils.RED);
        totalLiabilitiesLabel.setBounds(800, 94, 200, 30);
        sum.add(totalLiabilitiesLabel);

        int totalHeight = y + 130 + 20 + BOTTOM_PADDING;
        contentPanel.setPreferredSize(new Dimension(1110, totalHeight));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildAccountCard(int x, int y, int w, int h, String[] data, Color accent, Account acc) {
        JPanel pn = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(accent);
                g2.fillRect(0, 0, 6, h);
                g2.dispose();
            }
        };
        pn.setBounds(x, y, w, h);
        pn.setBackground(UIUtils.WHITE);
        pn.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR));


        JLabel name = new JLabel(data[0]);
        name.setFont(UIUtils.F_SECTION);
        name.setForeground(UIUtils.TEXT_DARK);
        name.setBounds(18, 14, w - 36, 26);
        pn.add(name);


        JLabel num = new JLabel(data[1]);
        num.setFont(UIUtils.F_SMALL);
        num.setForeground(UIUtils.TEXT_LIGHT);
        num.setBounds(18, 42, w - 36, 20);
        pn.add(num);


        JLabel type = new JLabel(data[3]);
        type.setFont(UIUtils.F_SMALL);
        type.setForeground(Color.WHITE);
        type.setOpaque(true);
        type.setBackground(accent);
        type.setBounds(18, 66, 80, 20);
        type.setHorizontalAlignment(SwingConstants.CENTER);
        pn.add(type);


        JLabel bal = new JLabel(data[2]);
        bal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bal.setForeground(accent);
        bal.setHorizontalAlignment(SwingConstants.RIGHT);
        bal.setBounds(18, 90, w - 36, 26);
        pn.add(bal);


        JButton editBtn = new JButton("Edit");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editBtn.setForeground(UIUtils.TEXT_DARK);
        editBtn.setBackground(UIUtils.WHITE);
        editBtn.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        editBtn.setFocusPainted(false);
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editBtn.setBounds(18, 115, 70, 24);
        editBtn.addActionListener(e -> {
            AddEditAccountDialog dialog = new AddEditAccountDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, acc);
            dialog.setVisible(true);
            if (dialog.isSaved()) rebuildUI();
        });
        pn.add(editBtn);


        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteBtn.setForeground(UIUtils.RED);
        deleteBtn.setBackground(UIUtils.WHITE);
        deleteBtn.setBorder(BorderFactory.createLineBorder(UIUtils.RED, 1));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.setBounds(95, 115, 70, 24);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Delete account '" + acc.getAccountName() + "'?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = accountDAO.deleteAccount(acc.getId());
                if (deleted) {
                    rebuildUI();
                } else {
                    JOptionPane.showMessageDialog(
                            SwingUtilities.getWindowAncestor(this),
                            "Failed to delete account. It may be referenced by existing transactions.",
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pn.add(deleteBtn);

        return pn;
    }
}