package view;

import database.BudgetDAO;
import database.CategoryDAO;
import model.Budget;
import model.Category;
import model.User;
import utils.CurrencyFormatter;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;

public class AddEditBudgetDialog extends JDialog {

    private final User user;
    private final Budget existing;
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private boolean saved = false;

    private JComboBox<String> categoryCombo;
    private List<Category> categoryList;
    private JTextField amountField;
    private JComboBox<String> periodCombo;
    private JSpinner monthYearSpinner;

    public AddEditBudgetDialog(JFrame parent, User user, Budget existing) {
        super(parent, existing == null ? "Add Budget" : "Edit Budget", true);
        this.user = user;
        this.existing = existing;

        setSize(450, 500);
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel(existing == null ? "New Budget" : "Edit Budget");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        addFieldRow(form, gbc, "Category:");
        categoryCombo = new JComboBox<>();
        loadCategories();
        if (existing != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == existing.getCategoryId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        categoryCombo.setFont(UIUtils.F_BODY);
        categoryCombo.setBackground(UIUtils.WHITE);
        categoryCombo.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(categoryCombo, gbc);
        gbc.gridy++;


        String currencySymbol = Currency.getInstance(CurrencyFormatter.getCurrencyCode()).getSymbol();
        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(form, gbc, "Budget Amount (" + currencySymbol + "):");
        amountField = new JTextField(existing != null ? String.valueOf((int)existing.getAmount()) : "");
        amountField.setFont(UIUtils.F_BODY);
        amountField.setBorder(createFieldBorder());
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(amountField, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(form, gbc, "Period:");
        periodCombo = new JComboBox<>(new String[]{"Monthly", "Yearly"});
        periodCombo.setFont(UIUtils.F_BODY);
        periodCombo.setBackground(UIUtils.WHITE);
        periodCombo.setBorder(createFieldBorder());
        if (existing != null) {
            periodCombo.setSelectedItem(existing.getPeriod());
        }
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(periodCombo, gbc);
        gbc.gridy++;


        gbc.gridx = 0; gbc.weightx = 0;
        addFieldRow(form, gbc, "Select:");
        monthYearSpinner = new JSpinner();
        updateSpinnerModel();
        periodCombo.addActionListener(e -> updateSpinnerModel());
        styleSpinner(monthYearSpinner);
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(monthYearSpinner, gbc);

        contentPane.add(form, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);

        JButton saveBtn = new JButton("Save Budget");
        saveBtn.setFont(UIUtils.F_SMALL);
        saveBtn.setBackground(UIUtils.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveBudget());
        buttonPanel.add(saveBtn);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
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

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(UIUtils.F_BODY);
        spinner.setBackground(UIUtils.WHITE);
        spinner.setForeground(UIUtils.TEXT_DARK);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            defaultEditor.getTextField().setBorder(createFieldBorder());
            defaultEditor.getTextField().setFont(UIUtils.F_BODY);
        }
    }

    private void loadCategories() {
        categoryList = categoryDAO.getAllCategories(user.getId());
        for (Category c : categoryList) {
            categoryCombo.addItem(c.getName());
        }
    }

    private void updateSpinnerModel() {
        if ("Monthly".equals(periodCombo.getSelectedItem())) {
            SpinnerDateModel model = new SpinnerDateModel();
            monthYearSpinner.setModel(model);
            JSpinner.DateEditor editor = new JSpinner.DateEditor(monthYearSpinner, "yyyy-MM");
            monthYearSpinner.setEditor(editor);
            if (existing != null && existing.getMonthYear() != null) {
                monthYearSpinner.setValue(existing.getMonthYear());
            }
        } else {
            int currentYear = YearMonth.now().getYear();
            SpinnerNumberModel model = new SpinnerNumberModel(currentYear, 2000, 2100, 1);
            monthYearSpinner.setModel(model);
            if (existing != null && existing.getMonthYear() != null) {
                monthYearSpinner.setValue(existing.getMonthYear().toLocalDate().getYear());
            }
        }
    }

    private void saveBudget() {
        int catIdx = categoryCombo.getSelectedIndex();
        if (catIdx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category.");
            return;
        }
        int categoryId = categoryList.get(catIdx).getId();

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
            return;
        }

        Date monthYear;
        if ("Monthly".equals(periodCombo.getSelectedItem())) {
            java.util.Date date = (java.util.Date) monthYearSpinner.getValue();
            monthYear = new Date(date.getTime());
        } else {
            int year = (int) monthYearSpinner.getValue();
            monthYear = Date.valueOf(year + "-01-01");
        }

        Budget b = existing != null ? existing : new Budget();
        b.setUserId(user.getId());
        b.setCategoryId(categoryId);
        b.setAmount(amount);
        b.setMonthYear(monthYear);
        b.setPeriod((String) periodCombo.getSelectedItem());

        boolean ok = existing == null ? budgetDAO.add(b) : budgetDAO.update(b);
        if (ok) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save budget.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}