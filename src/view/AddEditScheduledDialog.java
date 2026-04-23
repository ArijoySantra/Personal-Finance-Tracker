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

public class AddEditScheduledDialog extends JDialog {

    private final User currentUser;
    private final Scheduled existing;
    private final ScheduledDAO scheduledDAO = new ScheduledDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private boolean saved = false;

    private JTextField descField, amountField;
    private JComboBox<String> frequencyCombo, statusCombo, categoryCombo;
    private JSpinner dateSpinner;
    private List<Category> categoryList;

    private static final Color FIELD_BG = new Color(248, 250, 252);
    private static final Color FIELD_BORDER = new Color(203, 213, 225);
    private static final Color HOVER_BORDER = UIUtils.BLUE;

    public AddEditScheduledDialog(JFrame parent, User user, Scheduled st) {
        super(parent, st == null ? "Add Scheduled Transaction" : "Edit Scheduled Transaction", true);
        this.currentUser = user;
        this.existing = st;

        setSize(480, 520);
        setLocationRelativeTo(parent);
        setUndecorated(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = createTitleBar(st == null ? "New Schedule" : "Edit Schedule");
        contentPane.add(titleBar, BorderLayout.NORTH);

        JPanel form = createFormPanel();
        contentPane.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        loadCategories();
        if (existing != null) {
            preselectCategory();
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
        descField = createStyledTextField(existing != null ? existing.getDescription() : "");
        gbc.gridx = 1;
        panel.add(descField, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Category");
        categoryCombo = createStyledComboBox();
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Frequency");
        frequencyCombo = createStyledComboBox(new String[]{"DAILY", "WEEKLY", "MONTHLY", "YEARLY"});
        if (existing != null) frequencyCombo.setSelectedItem(existing.getFrequency());
        gbc.gridx = 1;
        panel.add(frequencyCombo, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Next Date");
        dateSpinner = createStyledDateSpinner();
        if (existing != null) dateSpinner.setValue(existing.getNextDate());
        else dateSpinner.setValue(Date.valueOf(LocalDate.now()));
        gbc.gridx = 1;
        panel.add(dateSpinner, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Amount (" + currencySymbol + ")");
        amountField = createStyledTextField(existing != null ? String.valueOf(existing.getAmount()) : "");
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        gbc.gridy++;


        gbc.gridx = 0;
        addFormLabel(panel, gbc, "Status");
        statusCombo = createStyledComboBox(new String[]{"Active", "Paused"});
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

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

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> combo = new JComboBox<>();
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

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = createStyledComboBox();
        for (String item : items) combo.addItem(item);
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

        JButton saveBtn = createPillButton("Save Schedule", UIUtils.BLUE, Color.WHITE);
        saveBtn.addActionListener(e -> save());

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

    private void loadCategories() {
        categoryList = categoryDAO.getAllCategories(currentUser.getId());
        for (Category c : categoryList) {
            categoryCombo.addItem(c.getName());
        }
    }

    private void preselectCategory() {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == existing.getCategoryId()) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void save() {
        String desc = descField.getText().trim();
        if (desc.isEmpty()) {
            showError("Please enter a name for this scheduled transaction.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showError("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
            return;
        }

        String freq = (String) frequencyCombo.getSelectedItem();
        Date nextDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());

        int catIdx = categoryCombo.getSelectedIndex();
        if (catIdx < 0 || categoryList.isEmpty()) {
            showError("Please select a category.");
            return;
        }
        int catId = categoryList.get(catIdx).getId();

        Scheduled s = existing != null ? existing : new Scheduled();
        s.setUserId(currentUser.getId());
        s.setDescription(desc);
        s.setAmount(amount);
        s.setType("EXPENSE"); // default
        s.setFrequency(freq);
        s.setNextDate(nextDate);
        s.setCategoryId(catId);

        boolean ok = existing == null ? scheduledDAO.add(s) : scheduledDAO.update(s);
        if (ok) {
            saved = true;
            dispose();
        } else {
            showError("Failed to save scheduled transaction. Please try again.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSaved() {
        return saved;
    }
}