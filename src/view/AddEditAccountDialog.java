package view;

import database.AccountDAO;
import model.Account;
import model.User;
import utils.CurrencyFormatter;   // ✅ 使用 utils 包中的格式化工具
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Currency;

public class AddEditAccountDialog extends JDialog {
    private User user;
    private Account account;
    private boolean saved = false;
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField balanceField;
    private AccountDAO accountDAO;

    public AddEditAccountDialog(JFrame parent, User user, Account account) {
        super(parent, account == null ? "Add Account" : "Edit Account", true);
        this.user = user;
        this.account = account;
        this.accountDAO = new AccountDAO();

        setSize(400, 320);
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);


        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel(account == null ? "New Account" : "Edit Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Account Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(account != null ? account.getAccountName() : "");
        nameField.setFont(UIUtils.F_BODY);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Bank", "Wallet", "Cash", "Other"});
        typeCombo.setFont(UIUtils.F_BODY);
        if (account != null) typeCombo.setSelectedItem(account.getAccountType());
        form.add(typeCombo, gbc);

        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Initial Balance (" + currencySymbol + "):"), gbc);
        gbc.gridx = 1;
        balanceField = new JTextField(account != null ? String.valueOf(account.getBalance()) : "0.00");
        balanceField.setFont(UIUtils.F_BODY);
        balanceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(6, 10, 6, 10)));
        form.add(balanceField, gbc);

        contentPane.add(form, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                new EmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(UIUtils.F_SMALL);
        saveBtn.setBackground(UIUtils.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> save());
        buttonPanel.add(saveBtn);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account name is required.");
            return;
        }
        String type = (String) typeCombo.getSelectedItem();
        double balance;
        try {
            balance = Double.parseDouble(balanceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid balance amount.");
            return;
        }

        if (account == null) {
            account = new Account(user.getId(), name, type, balance);
            saved = accountDAO.addAccount(account);
        } else {
            account.setAccountName(name);
            account.setAccountType(type);
            account.setBalance(balance);
            saved = updateAccount(account);
        }
        if (saved) dispose();
        else JOptionPane.showMessageDialog(this, "Failed to save account.");
    }

    private boolean updateAccount(Account acc) {
        String sql = "UPDATE accounts SET account_name=?, account_type=?, balance=? WHERE id=?";
        try (java.sql.Connection con = database.DBConnection.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc.getAccountName());
            ps.setString(2, acc.getAccountType());
            ps.setDouble(3, acc.getBalance());
            ps.setInt(4, acc.getId());
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSaved() { return saved; }
}