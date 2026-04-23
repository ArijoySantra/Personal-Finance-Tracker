package view;

import database.AccountDAO;
import database.CardDAO;
import model.Account;
import model.Card;
import model.User;
import utils.CurrencyFormatter;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;

public class AddEditCardDialog extends JDialog {

    private final User currentUser;
    private final Card existingCard;
    private final CardDAO cardDAO = new CardDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private boolean saved = false;
    private JTextField nameField;
    private JComboBox<String> accountCombo;
    private List<Account> accountList;
    private JTextField limitField;
    private JTextField interestField;
    private JTextField balanceField;
    private JCheckBox paidOffCheck;
    private JComboBox<Integer> startDayCombo;
    private JComboBox<Integer> payDayCombo;
    private JCheckBox autoPayCheck;
    private JLabel periodLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AddEditCardDialog(JFrame parent, User user, Card card) {
        super(parent, card == null ? "Add Credit Card" : "Edit Credit Card", true);
        this.currentUser = user;
        this.existingCard = card;

        setSize(520, 630);
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel(card == null ? "New Credit Card" : "Edit Credit Card");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtils.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;


        addFieldRow(formPanel, gbc, "Name:");
        nameField = new JTextField(existingCard != null ? existingCard.getCardName() : "");
        nameField.setFont(UIUtils.F_BODY);
        nameField.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridy++;

        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Account:");
        accountCombo = new JComboBox<>();
        loadAccounts();
        if (existingCard != null && existingCard.getAssociatedAccountId() != null) {
            for (int i = 0; i < accountList.size(); i++) {
                if (accountList.get(i).getId() == existingCard.getAssociatedAccountId()) {
                    accountCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        accountCombo.setFont(UIUtils.F_BODY);
        accountCombo.setBackground(UIUtils.WHITE);
        accountCombo.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(accountCombo, gbc);
        gbc.gridy++;


        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();


        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Credit Limit (" + currencySymbol + "):");
        limitField = new JTextField(existingCard != null ? String.valueOf((int) existingCard.getCreditLimit()) : "0");
        limitField.setFont(UIUtils.F_BODY);
        limitField.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(limitField, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Outstanding Balance (" + currencySymbol + "):");
        balanceField = new JTextField(existingCard != null ? String.valueOf((int) existingCard.getCurrentBalance()) : "0");
        balanceField.setFont(UIUtils.F_BODY);
        balanceField.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(balanceField, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Interest Rate (%):");
        interestField = new JTextField(existingCard != null ? String.valueOf((int) existingCard.getInterestRate()) : "0");
        interestField.setFont(UIUtils.F_BODY);
        interestField.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(interestField, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1;
        paidOffCheck = new JCheckBox("Paid off each month");
        paidOffCheck.setFont(UIUtils.F_BODY);
        paidOffCheck.setOpaque(false);
        paidOffCheck.setSelected(existingCard != null && existingCard.isPaidOffMonthly());
        formPanel.add(paidOffCheck, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Starting day:");
        startDayCombo = createDayComboBox();
        startDayCombo.setSelectedItem(existingCard != null ? existingCard.getStartingDay() : 1);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(startDayCombo, gbc);
        gbc.gridy++;

        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(formPanel, gbc, "Payment day:");
        payDayCombo = createDayComboBox();
        payDayCombo.setSelectedItem(existingCard != null ? existingCard.getPaymentDay() : 5);
        gbc.gridx = 1; gbc.weightx = 1;
        formPanel.add(payDayCombo, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1;
        autoPayCheck = new JCheckBox("Enable automatic payments");
        autoPayCheck.setFont(UIUtils.F_BODY);
        autoPayCheck.setOpaque(false);
        autoPayCheck.setSelected(existingCard != null && existingCard.isAutomaticPayment());
        autoPayCheck.addActionListener(e -> updatePeriodLabel());
        formPanel.add(autoPayCheck, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;


        gbc.gridx = 0; gbc.gridwidth = 2;
        periodLabel = new JLabel();
        periodLabel.setFont(UIUtils.F_SMALL);
        periodLabel.setForeground(UIUtils.TEXT_MID);
        formPanel.add(periodLabel, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(UIUtils.F_SMALL);
        saveBtn.setBackground(UIUtils.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> onSave());
        buttonPanel.add(saveBtn);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        updatePeriodLabel();
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.F_BODY);
        label.setForeground(UIUtils.TEXT_DARK);
        panel.add(label, gbc);
    }

    private Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    private JComboBox<Integer> createDayComboBox() {
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) days[i] = i + 1;
        JComboBox<Integer> cb = new JComboBox<>(days);
        cb.setFont(UIUtils.F_BODY);
        cb.setBackground(UIUtils.WHITE);
        cb.setBorder(createFieldBorder());
        return cb;
    }

    private void loadAccounts() {
        accountList = accountDAO.getAccountsByUser(currentUser.getId());
        for (Account acc : accountList) {
            accountCombo.addItem(acc.getAccountName());
        }
    }

    private void updatePeriodLabel() {
        int startDay = (Integer) startDayCombo.getSelectedItem();
        int payDay = (Integer) payDayCombo.getSelectedItem();
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(Math.min(startDay, today.lengthOfMonth()));
        LocalDate end = start.plusMonths(1).minusDays(1);
        periodLabel.setText("Period: " + start.format(DATE_FMT) + " - " + end.format(DATE_FMT));
    }

    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Card name is required.");
            return;
        }

        double limit, balance, interest;
        try {
            limit = Double.parseDouble(limitField.getText().trim());
            balance = Double.parseDouble(balanceField.getText().trim());
            interest = Double.parseDouble(interestField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
            return;
        }

        int idx = accountCombo.getSelectedIndex();
        Integer accId = (idx >= 0 && !accountList.isEmpty()) ? accountList.get(idx).getId() : null;

        Card card = existingCard != null ? existingCard : new Card();
        card.setUserId(currentUser.getId());
        card.setCardName(name);
        card.setCardType("Credit");
        card.setCreditLimit(limit);
        card.setCurrentBalance(balance);
        card.setInterestRate(interest);
        card.setPaidOffMonthly(paidOffCheck.isSelected());
        card.setAutomaticPayment(autoPayCheck.isSelected());
        card.setStartingDay((Integer) startDayCombo.getSelectedItem());
        card.setPaymentDay((Integer) payDayCombo.getSelectedItem());
        card.setAssociatedAccountId(accId);
        if (card.getDueDate() == null) {
            card.setDueDate(Date.valueOf(LocalDate.now().plusMonths(1)));
        }

        boolean ok = existingCard == null ? cardDAO.addCard(card) : cardDAO.updateCard(card);
        if (ok) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Save failed.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}