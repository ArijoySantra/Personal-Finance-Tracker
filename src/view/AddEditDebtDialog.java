package view;

import database.DebtDAO;
import model.Debt;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Currency;

public class AddEditDebtDialog extends JDialog {
    private final User user;
    private final Debt existing;
    private final DebtDAO debtDAO = new DebtDAO();
    private boolean saved = false;

    private JTextField nameField, amountField, remainingField, interestField, emiField;
    private JComboBox<String> typeCombo;
    private JSpinner dueDateSpinner;
    private JCheckBox emiCheckBox;

    private static final Color FIELD_BG = new Color(248, 250, 252);
    private static final Color FIELD_BORDER = new Color(203, 213, 225);
    private static final Color HOVER_BORDER = UIUtils.BLUE;

    public AddEditDebtDialog(JFrame parent, User user, Debt existing) {
        super(parent, existing == null ? "Add Debt" : "Edit Debt", true);
        this.user = user;
        this.existing = existing;

        setSize(480, 620);
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);


        JPanel titleBar = createTitleBar(existing == null ? "New Debt" : "Edit Debt");
        contentPane.add(titleBar, BorderLayout.NORTH);

        JPanel form = createFormPanel();
        contentPane.add(form, BorderLayout.CENTER);


        JPanel buttonPanel = createButtonPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);


        if (existing != null && existing.getEmiAmount() > 0) {
            emiCheckBox.setSelected(true);
            emiField.setText(String.valueOf(existing.getEmiAmount()));
            emiField.setEnabled(true);
        }
    }

    private JPanel createTitleBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIUtils.BLUE);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        bar.add(titleLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        bar.add(closeBtn, BorderLayout.EAST);

        return bar;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIUtils.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;

        addFormLabel(panel, gbc, "Name");
        nameField = createStyledTextField(existing != null ? existing.getName() : "");
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Type");
        typeCombo = createStyledComboBox(new String[]{"BORROWED", "LENT"});
        if (existing != null) typeCombo.setSelectedItem(existing.getType());
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Total Amount (" + currencySymbol + ")");
        amountField = createStyledTextField(existing != null ? String.valueOf(existing.getAmount()) : "");
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Remaining (" + currencySymbol + ")");
        remainingField = createStyledTextField(existing != null ? String.valueOf(existing.getRemaining()) : "");
        gbc.gridx = 1;
        panel.add(remainingField, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Interest Rate (%)");
        interestField = createStyledTextField(existing != null ? String.valueOf(existing.getInterestRate()) : "0");
        gbc.gridx = 1;
        panel.add(interestField, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Due Date");
        dueDateSpinner = createStyledDateSpinner();
        if (existing != null && existing.getDueDate() != null) {
            dueDateSpinner.setValue(existing.getDueDate());
        } else {
            dueDateSpinner.setValue(Date.valueOf(LocalDate.now()));
        }
        gbc.gridx = 1;
        panel.add(dueDateSpinner, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        emiCheckBox = new JCheckBox("This is an EMI / Installment plan");
        emiCheckBox.setFont(UIUtils.F_BODY);
        emiCheckBox.setForeground(UIUtils.TEXT_DARK);
        emiCheckBox.setBackground(UIUtils.WHITE);
        emiCheckBox.setFocusPainted(false);
        emiCheckBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(emiCheckBox, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        addFormLabel(panel, gbc, "EMI Amount (" + currencySymbol + ")");
        emiField = createStyledTextField("");
        emiField.setEnabled(false);
        gbc.gridx = 1;
        panel.add(emiField, gbc);
        gbc.gridy++;

        // Enable/disable EMI field based on checkbox
        emiCheckBox.addActionListener(e -> {
            emiField.setEnabled(emiCheckBox.isSelected());
            if (!emiCheckBox.isSelected()) {
                emiField.setText("");
            }
            emiField.requestFocusInWindow();
        });

        return panel;
    }

    private void addFormLabel(JPanel panel, GridBagConstraints gbc, String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIUtils.F_BODY);
        label.setForeground(UIUtils.TEXT_DARK);
        panel.add(label, gbc);
    }

    private JTextField createStyledTextField(String initialText) {
        JTextField field = new JTextField(initialText);
        field.setFont(UIUtils.F_BODY);
        field.setForeground(UIUtils.TEXT_DARK);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setCaretColor(UIUtils.BLUE);
        addFocusStyling(field);
        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UIUtils.F_BODY);
        combo.setBackground(FIELD_BG);
        combo.setForeground(UIUtils.TEXT_DARK);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        combo.setFocusable(true);
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = super.createArrowButton();
                btn.setBackground(FIELD_BG);
                btn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                return btn;
            }
        });
        return combo;
    }

    private JSpinner createStyledDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(UIUtils.F_BODY);
        spinner.setBackground(FIELD_BG);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        tf.setBackground(FIELD_BG);
        tf.setForeground(UIUtils.TEXT_DARK);
        tf.setCaretColor(UIUtils.BLUE);
        tf.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        addFocusStyling(tf);
        return spinner;
    }

    private void addFocusStyling(JTextField field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(HOVER_BORDER, 2, true),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 16));
        panel.setBackground(UIUtils.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 20, 12, 20)
        ));

        JButton cancelBtn = createPillButton("Cancel", UIUtils.WHITE, UIUtils.TEXT_DARK);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = createPillButton("Save Debt", UIUtils.BLUE, Color.WHITE);
        saveBtn.addActionListener(e -> saveDebt());

        panel.add(cancelBtn);
        panel.add(saveBtn);
        return panel;
    }

    private JButton createPillButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg == UIUtils.WHITE ? UIUtils.BORDER_COLOR : bg, 1, true),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void saveDebt() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter a name for this debt.");
            return;
        }

        double amount, remaining, interest;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            remaining = Double.parseDouble(remainingField.getText().trim());
            interest = Double.parseDouble(interestField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for amount, remaining, and interest.");
            return;
        }

        if (amount <= 0 || remaining < 0) {
            showError("Amount must be positive and remaining cannot be negative.");
            return;
        }

        double emiAmount = 0.0;
        if (emiCheckBox.isSelected()) {
            try {
                emiAmount = Double.parseDouble(emiField.getText().trim());
                if (emiAmount <= 0) {
                    showError("EMI amount must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid EMI amount.");
                return;
            }
        }

        Date due = new Date(((java.util.Date) dueDateSpinner.getValue()).getTime());

        Debt d = existing != null ? existing : new Debt();
        d.setUserId(user.getId());
        d.setName(name);
        d.setType((String) typeCombo.getSelectedItem());
        d.setAmount(amount);
        d.setRemaining(remaining);
        d.setInterestRate(interest);
        d.setDueDate(due);
        d.setEmiAmount(emiAmount);

        boolean ok = existing == null ? debtDAO.add(d) : debtDAO.update(d);
        if (ok) {
            saved = true;
            dispose();
        } else {
            showError("Failed to save debt. Please try again.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSaved() {
        return saved;
    }
}