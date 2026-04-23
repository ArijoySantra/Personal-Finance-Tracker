package view;

import database.*;
import model.*;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

public class AddTransactionDialog extends JDialog {
    private User user;
    private boolean saved = false;
    private JTextField txtDescription, txtAmount;
    private JComboBox<String> cmbType, cmbSource, cmbCategory;
    private JSpinner dateSpinner;
    private JButton btnSave;
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private CardDAO cardDAO;
    private CategoryDAO categoryDAO;

    private static final String NONE_SOURCE = "None (No account/card)";

    public AddTransactionDialog(JFrame parent, User user) {
        super(parent, "Add Transaction", true);
        this.user = user;
        this.transactionDAO = new TransactionDAO();
        this.accountDAO = new AccountDAO();
        this.cardDAO = new CardDAO();
        this.categoryDAO = new CategoryDAO();

        setSize(480, 540);
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout(0, 0));
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel("New Transaction");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridy = 0;


        addFormRow(formPanel, gbc, "Description");
        txtDescription = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtDescription, gbc);

        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();
        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        addFormRow(formPanel, gbc, "Amount (" + currencySymbol + ")");
        txtAmount = createStyledTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtAmount, gbc);


        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        addFormRow(formPanel, gbc, "Type");
        cmbType = createStyledComboBox(new String[]{"INCOME", "EXPENSE"});
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(cmbType, gbc);


        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        addFormRow(formPanel, gbc, "Source (optional)");
        cmbSource = createStyledComboBox(new String[0]);
        loadSources();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(cmbSource, gbc);


        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        addFormRow(formPanel, gbc, "Category");
        cmbCategory = createStyledComboBox(new String[0]);
        loadCategories();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(cmbCategory, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        addFormRow(formPanel, gbc, "Date");
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        dateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        styleSpinner(dateSpinner);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(dateSpinner, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(UIUtils.F_SMALL);
        btnCancel.setForeground(UIUtils.TEXT_DARK);
        btnCancel.setBackground(UIUtils.WHITE);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Save Transaction");
        btnSave.setFont(UIUtils.F_SMALL);
        btnSave.setForeground(Color.WHITE);
        btnSave.setBackground(UIUtils.BLUE);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> saveTransaction());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        loadSources();
        loadCategories();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.F_BODY);
        label.setForeground(UIUtils.TEXT_DARK);
        panel.add(label, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UIUtils.F_BODY);
        tf.setBackground(UIUtils.WHITE);
        tf.setForeground(UIUtils.TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return tf;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(UIUtils.F_BODY);
        cb.setBackground(UIUtils.WHITE);
        cb.setForeground(UIUtils.TEXT_DARK);
        cb.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        return cb;
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(UIUtils.F_BODY);
        spinner.setBackground(UIUtils.WHITE);
        spinner.setForeground(UIUtils.TEXT_DARK);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            defaultEditor.getTextField().setFont(UIUtils.F_BODY);
        }
    }

    private void loadSources() {
        cmbSource.removeAllItems();
        cmbSource.addItem(NONE_SOURCE);  // None option first
        List<Account> accounts = accountDAO.getAccountsByUser(user.getId());
        for (Account a : accounts) {
            cmbSource.addItem(a.getAccountName());
        }
        List<Card> cards = cardDAO.getCardsByUser(user.getId());
        for (Card c : cards) {
            cmbSource.addItem(c.getCardName());
        }
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        List<Category> cats = categoryDAO.getAllCategories(user.getId());
        cats.sort((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()));
        for (Category c : cats) {
            cmbCategory.addItem(c.getName() + "  (" + c.getType() + ")");
        }
    }

    private void saveTransaction() {
        String desc = txtDescription.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(txtAmount.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        String type = (String) cmbType.getSelectedItem();
        Date date = new Date(((java.util.Date) dateSpinner.getValue()).getTime());

        Integer accountId = null;
        Integer cardId = null;
        String selectedSource = (String) cmbSource.getSelectedItem();


        if (selectedSource != null && !selectedSource.equals(NONE_SOURCE)) {

            List<Account> accounts = accountDAO.getAccountsByUser(user.getId());
            for (Account a : accounts) {
                if (a.getAccountName().equals(selectedSource)) {
                    accountId = a.getId();
                    break;
                }
            }

            if (accountId == null) {
                List<Card> cards = cardDAO.getCardsByUser(user.getId());
                for (Card c : cards) {
                    if (c.getCardName().equals(selectedSource)) {
                        cardId = c.getId();
                        break;
                    }
                }
            }
        }

        String catStr = (String) cmbCategory.getSelectedItem();
        if (catStr == null) {
            JOptionPane.showMessageDialog(this, "Please select a category.");
            return;
        }
        String catName = catStr.split("  \\(")[0].trim();
        List<Category> cats = categoryDAO.getAllCategories(user.getId());
        int categoryId = -1;
        for (Category c : cats) {
            if (c.getName().equals(catName)) {
                categoryId = c.getId();
                break;
            }
        }

        Transaction t = new Transaction(user.getId(), accountId, cardId, categoryId, amount, type, desc, date);
        if (transactionDAO.add(t)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save transaction.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}