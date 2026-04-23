package view;

import database.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ChangePasswordDialog extends JDialog {

    private User user;
    private UserDAO userDAO;
    private boolean changed = false;

    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    public ChangePasswordDialog(JFrame parent, User user) {
        super(parent, "Change Password", true);
        this.user = user;
        this.userDAO = new UserDAO();

        setSize(450, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(UIUtils.WHITE);
        setContentPane(contentPane);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UIUtils.BLUE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.WEST);
        contentPane.add(titleBar, BorderLayout.NORTH);


        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblCurrent = new JLabel("Current Password:");
        lblCurrent.setFont(UIUtils.F_BODY);
        lblCurrent.setForeground(UIUtils.TEXT_DARK);
        form.add(lblCurrent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCurrentPassword = new JPasswordField();
        stylePasswordField(txtCurrentPassword);
        form.add(txtCurrentPassword, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblNew = new JLabel("New Password:");
        lblNew.setFont(UIUtils.F_BODY);
        lblNew.setForeground(UIUtils.TEXT_DARK);
        form.add(lblNew, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNewPassword = new JPasswordField();
        stylePasswordField(txtNewPassword);
        form.add(txtNewPassword, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel lblConfirm = new JLabel("Confirm Password:");
        lblConfirm.setFont(UIUtils.F_BODY);
        lblConfirm.setForeground(UIUtils.TEXT_DARK);
        form.add(lblConfirm, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtConfirmPassword = new JPasswordField();
        stylePasswordField(txtConfirmPassword);
        form.add(txtConfirmPassword, gbc);

        contentPane.add(form, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(UIUtils.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(UIUtils.F_SMALL);
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Change Password");
        saveBtn.setFont(UIUtils.F_SMALL);
        saveBtn.setBackground(UIUtils.BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> changePassword());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtCurrentPassword.requestFocusInWindow();
            }
        });
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(UIUtils.F_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        field.setPreferredSize(new Dimension(200, 50));
    }

    private void changePassword() {
        String currentPassword = new String(txtCurrentPassword.getPassword());
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

       if (currentPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your current password.");
            return;
        }
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.");
            return;
        }
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New password and confirmation do not match.");
            return;
        }


        if (!userDAO.verifyPassword(user.getId(), currentPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.");
            return;
        }


        if (userDAO.updatePassword(user.getId(), newPassword)) {
            changed = true;
            JOptionPane.showMessageDialog(this, "Password changed successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password. Please try again.");
        }
    }

    public boolean isChanged() {
        return changed;
    }
}