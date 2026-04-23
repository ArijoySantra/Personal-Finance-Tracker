package view;

import model.Settings;
import model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PreferencesPanel extends JPanel {

    private User currentUser;
    private Runnable onPreferencesChanged;
    private JComboBox<String> cmbCurrency;
    private JComboBox<String> cmbDateFormat;
    private JToggleButton tglBudgetAlert;

    public PreferencesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(UIUtils.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setBackground(UIUtils.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Preferences");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel currencyRow = createRowPanel("Currency");
        cmbCurrency = new JComboBox<>(new String[]{"INR (₹)", "USD ($)", "EUR (€)", "GBP (£)"});
        styleComboBox(cmbCurrency);
        String currCode = Settings.getCurrencyCode();
        switch (currCode) {
            case "USD": cmbCurrency.setSelectedIndex(1); break;
            case "EUR": cmbCurrency.setSelectedIndex(2); break;
            case "GBP": cmbCurrency.setSelectedIndex(3); break;
            default: cmbCurrency.setSelectedIndex(0);
        }
        currencyRow.add(cmbCurrency);
        card.add(currencyRow);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel dateRow = createRowPanel("Date Format");
        cmbDateFormat = new JComboBox<>(new String[]{"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"});
        styleComboBox(cmbDateFormat);
        String savedFormat = Settings.getDateFormat();
        switch (savedFormat) {
            case "MM/dd/yyyy": cmbDateFormat.setSelectedIndex(1); break;
            case "yyyy-MM-dd": cmbDateFormat.setSelectedIndex(2); break;
            default: cmbDateFormat.setSelectedIndex(0);
        }
        dateRow.add(cmbDateFormat);
        card.add(dateRow);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel alertRow = createRowPanel("Budget Alert");
        tglBudgetAlert = createToggleSwitch();
        tglBudgetAlert.setSelected(Settings.isBudgetAlertEnabled());
        alertRow.add(tglBudgetAlert);
        alertRow.add(Box.createHorizontalGlue());
        card.add(alertRow);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnSave = UIUtils.accentButton("Save Changes", UIUtils.BLUE);
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(160, 40));
        btnSave.addActionListener(e -> savePreferences());
        card.add(btnSave);

        add(card, BorderLayout.CENTER);
    }

    private JPanel createRowPanel(String labelText) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(UIUtils.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel label = new JLabel(labelText);
        label.setFont(UIUtils.F_BODY);
        label.setForeground(UIUtils.TEXT_DARK);
        label.setPreferredSize(new Dimension(120, 30));
        row.add(label);
        row.add(Box.createRigidArea(new Dimension(20, 0)));
        return row;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(UIUtils.F_BODY);
        combo.setBackground(UIUtils.WHITE);
        combo.setForeground(UIUtils.TEXT_DARK);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        combo.setMaximumSize(new Dimension(200, 36));
    }

    private JToggleButton createToggleSwitch() {
        JToggleButton toggle = new JToggleButton("Off");
        toggle.setFont(UIUtils.F_SMALL);
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setContentAreaFilled(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setPreferredSize(new Dimension(70, 32));
        toggle.setMaximumSize(new Dimension(70, 32));

        toggle.addItemListener(e -> {
            if (toggle.isSelected()) {
                toggle.setText("On");
                toggle.setBackground(UIUtils.GREEN);
                toggle.setForeground(Color.WHITE);
                toggle.setOpaque(true);
                toggle.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            } else {
                toggle.setText("Off");
                toggle.setBackground(UIUtils.TEXT_LIGHT);
                toggle.setForeground(UIUtils.TEXT_DARK);
                toggle.setOpaque(true);
                toggle.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            }
        });

        toggle.setSelected(toggle.isSelected());
        return toggle;
    }

    public void setOnPreferencesChanged(Runnable callback) {
        this.onPreferencesChanged = callback;
    }

    private void savePreferences() {
        int currIdx = cmbCurrency.getSelectedIndex();
        String currCode;
        switch (currIdx) {
            case 1: currCode = "USD"; break;
            case 2: currCode = "EUR"; break;
            case 3: currCode = "GBP"; break;
            default: currCode = "INR";
        }
        Settings.setCurrencyCode(currCode);

        int dateIdx = cmbDateFormat.getSelectedIndex();
        String dateFormat;
        switch (dateIdx) {
            case 1: dateFormat = "MM/dd/yyyy"; break;
            case 2: dateFormat = "yyyy-MM-dd"; break;
            default: dateFormat = "dd/MM/yyyy";
        }
        Settings.setDateFormat(dateFormat);

        Settings.setBudgetAlertEnabled(tglBudgetAlert.isSelected());

        JOptionPane.showMessageDialog(this,
                "Your preferences have been saved.",
                "Preferences Saved", JOptionPane.INFORMATION_MESSAGE);

        if (onPreferencesChanged != null) {
            onPreferencesChanged.run();
        }
    }
}