package view;

import database.UserDAO;
import database.UserProfileDAO;
import model.User;
import model.UserProfile;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class UserProfilePanel extends JPanel {

    private User currentUser;
    private UserProfileDAO profileDAO;
    private JLabel lblFullName, lblEmail, lblPhone, lblIncome, lblOccupation, lblGoal, lblCountry;
    private UserProfile userProfile;

    public UserProfilePanel(User user) {
        this.currentUser = user;
        this.profileDAO = new UserProfileDAO();
        this.userProfile = profileDAO.getByUserId(user.getId());
        if (userProfile == null) {
            userProfile = new UserProfile(user.getId(), 0, "", "", "");
        }

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

        JLabel title = new JLabel("User Profile");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 20)));


        lblFullName = addProfileField(card, "Full Name",
                currentUser.getFullName() != null ? currentUser.getFullName() : "—");
        lblEmail = addProfileField(card, "Email",
                currentUser.getEmail() != null ? currentUser.getEmail() : "—");

        String phoneStr = currentUser.getPhone() > 0 ? String.valueOf(currentUser.getPhone()) : "—";
        lblPhone = addProfileField(card, "Phone", phoneStr);

        lblIncome = addProfileField(card, "Monthly Income",
                userProfile.getMonthlyIncome() > 0 ? CurrencyFormatter.format(userProfile.getMonthlyIncome()) : "—");
        lblOccupation = addProfileField(card, "Occupation",
                userProfile.getOccupation() != null && !userProfile.getOccupation().isEmpty() ? userProfile.getOccupation() : "—");
        lblGoal = addProfileField(card, "Financial Goal",
                userProfile.getFinancialGoal() != null && !userProfile.getFinancialGoal().isEmpty() ? userProfile.getFinancialGoal() : "—");
        lblCountry = addProfileField(card, "Country",
                userProfile.getCountry() != null && !userProfile.getCountry().isEmpty() ? userProfile.getCountry() : "—");

        card.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonRow.setBackground(UIUtils.WHITE);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnEdit = UIUtils.accentButton("Edit Profile", UIUtils.BLUE);
        btnEdit.setPreferredSize(new Dimension(140, 36));
        btnEdit.addActionListener(e -> openEditDialog());

        JButton btnChangePassword = UIUtils.accentButton("Change Password", UIUtils.ORANGE);
        btnChangePassword.addActionListener(e -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser);
            dialog.setVisible(true);
            if (dialog.isChanged()) {
                JOptionPane.showMessageDialog(this, "Your password has been updated.");
            }
        });

        buttonRow.add(btnEdit);
        buttonRow.add(btnChangePassword);
        card.add(buttonRow);

        add(card, BorderLayout.CENTER);
    }

    private JLabel addProfileField(JPanel parent, String labelText, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(UIUtils.WHITE);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.F_BODY);
        lbl.setForeground(UIUtils.TEXT_MID);
        lbl.setPreferredSize(new Dimension(140, 30));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        val.setForeground(UIUtils.TEXT_DARK);

        row.add(lbl);
        row.add(Box.createRigidArea(new Dimension(20, 0)));
        row.add(val);
        row.add(Box.createHorizontalGlue());

        parent.add(row);
        parent.add(Box.createRigidArea(new Dimension(0, 10)));

        return val;
    }

    private void openEditDialog() {
        EditProfileDialog dialog = new EditProfileDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, userProfile);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            userProfile = profileDAO.getByUserId(currentUser.getId());
            if (userProfile == null) {
                userProfile = new UserProfile(currentUser.getId(), 0, "", "", "");
            }
            refreshLabels();
        }
    }

    private void refreshLabels() {
        lblIncome.setText(userProfile.getMonthlyIncome() > 0 ? CurrencyFormatter.format(userProfile.getMonthlyIncome()) : "—");
        lblOccupation.setText(userProfile.getOccupation() != null && !userProfile.getOccupation().isEmpty() ? userProfile.getOccupation() : "—");
        lblGoal.setText(userProfile.getFinancialGoal() != null && !userProfile.getFinancialGoal().isEmpty() ? userProfile.getFinancialGoal() : "—");
        lblCountry.setText(userProfile.getCountry() != null && !userProfile.getCountry().isEmpty() ? userProfile.getCountry() : "—");
    }
}