package view;

import database.UserProfileDAO;
import model.User;
import model.UserProfile;
import utils.CurrencyFormatter;
import utils.ExchangeRate;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EditProfileDialog extends JDialog {

    private User user;
    private UserProfile profile;
    private UserProfileDAO profileDAO;
    private boolean saved = false;

    private JFormattedTextField txtMonthlyIncome;
    private JTextField txtOccupation, txtFinancialGoal, txtCountry;

    public EditProfileDialog(JFrame parent, User user, UserProfile profile) {
        super(parent, "Edit Profile", true);
        this.user = user;
        this.profile = profile;
        this.profileDAO = new UserProfileDAO();

        setSize(550, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel("Edit Profile Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String currencyCode = CurrencyFormatter.getCurrencyCode();
        double displayIncome = profile.getMonthlyIncome();
        double convertedIncome = ExchangeRate.convert(displayIncome, currencyCode);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblIncome = new JLabel("Monthly Income (" + currencyCode + "):");
        lblIncome.setFont(UIUtils.F_BODY);
        lblIncome.setForeground(UIUtils.TEXT_DARK);
        form.add(lblIncome, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        NumberFormat format = DecimalFormat.getInstance();
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        txtMonthlyIncome = new JFormattedTextField(format);
        txtMonthlyIncome.setValue(convertedIncome > 0 ? convertedIncome : null);
        txtMonthlyIncome.setFont(UIUtils.F_BODY);
        txtMonthlyIncome.setBorder(createFieldBorder());
        txtMonthlyIncome.setPreferredSize(new Dimension(200, 36));
        form.add(txtMonthlyIncome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblOccupation = new JLabel("Occupation:");
        lblOccupation.setFont(UIUtils.F_BODY);
        lblOccupation.setForeground(UIUtils.TEXT_DARK);
        form.add(lblOccupation, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtOccupation = new JTextField(profile.getOccupation() != null ? profile.getOccupation() : "");
        txtOccupation.setFont(UIUtils.F_BODY);
        txtOccupation.setBorder(createFieldBorder());
        txtOccupation.setPreferredSize(new Dimension(200, 36));
        form.add(txtOccupation, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblGoal = new JLabel("Financial Goal:");
        lblGoal.setFont(UIUtils.F_BODY);
        lblGoal.setForeground(UIUtils.TEXT_DARK);
        form.add(lblGoal, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFinancialGoal = new JTextField(profile.getFinancialGoal() != null ? profile.getFinancialGoal() : "");
        txtFinancialGoal.setFont(UIUtils.F_BODY);
        txtFinancialGoal.setBorder(createFieldBorder());
        txtFinancialGoal.setPreferredSize(new Dimension(200, 36));
        form.add(txtFinancialGoal, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblCountry = new JLabel("Country:");
        lblCountry.setFont(UIUtils.F_BODY);
        lblCountry.setForeground(UIUtils.TEXT_DARK);
        form.add(lblCountry, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCountry = new JTextField(profile.getCountry() != null ? profile.getCountry() : "");
        txtCountry.setFont(UIUtils.F_BODY);
        txtCountry.setBorder(createFieldBorder());
        txtCountry.setPreferredSize(new Dimension(200, 36));
        form.add(txtCountry, gbc);

        contentPane.add(form, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(UIUtils.F_SMALL);
        saveBtn.setBackground(UIUtils.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveProfile());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtMonthlyIncome.requestFocusInWindow();
            }
        });
    }

    private Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private void saveProfile() {
        double enteredAmount = 0;
        Object value = txtMonthlyIncome.getValue();
        if (value != null) {
            if (value instanceof Number) {
                enteredAmount = ((Number) value).doubleValue();
            } else {
                try {
                    enteredAmount = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid income amount.");
                    return;
                }
            }
            if (enteredAmount < 0) {
                JOptionPane.showMessageDialog(this, "Monthly income cannot be negative.");
                return;
            }
        }


        String selectedCurrency = CurrencyFormatter.getCurrencyCode();
        double amountInINR = ExchangeRate.convertToINR(enteredAmount, selectedCurrency);

        profile.setMonthlyIncome(amountInINR);
        profile.setOccupation(txtOccupation.getText().trim());
        profile.setFinancialGoal(txtFinancialGoal.getText().trim());
        profile.setCountry(txtCountry.getText().trim());

        if (profileDAO.save(profile)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save profile.");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}