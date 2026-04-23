package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

import database.UserDAO;

public class ResetPasswordFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	private JPanel cardPanel;
	private JPanel leftPanel;
	private JPanel rightPanel;

	private JPasswordField txtNewPassword;
	private JPasswordField txtConfirmPassword;

	private JButton btnResetPassword;
	private JButton btnBackToLogin;

	private String userEmail;

	public ResetPasswordFrame(String email) {
		this.userEmail = email;

		setTitle("Personal Finance System - Reset Password");
		setSize(1100, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBackground(new Color(238, 217, 191));
		setContentPane(mainPanel);

		cardPanel = new JPanel();
		cardPanel.setLayout(null);
		cardPanel.setBounds(160, 80, 780, 500);
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2));
		mainPanel.add(cardPanel);

		leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setBounds(0, 0, 460, 500);
		leftPanel.setBackground(new Color(240, 220, 194));
		cardPanel.add(leftPanel);

		rightPanel = new JPanel();
		rightPanel.setLayout(null);
		rightPanel.setBounds(460, 0, 320, 500);
		rightPanel.setBackground(new Color(245, 245, 245));
		cardPanel.add(rightPanel);

		addLeftPanelComponents();
		addRightPanelComponents();
		addActions();
	}

	private void addLeftPanelComponents() {
		JLabel lblImage = new JLabel();
		lblImage.setBounds(35, 28, 390, 307);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);

		ImageIcon icon = new ImageIcon("src/resources/my_image.png");
		Image scaledImage = icon.getImage().getScaledInstance(390, 300, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(scaledImage));
		leftPanel.add(lblImage);

		JLabel lblCircle1 = new JLabel("●");
		lblCircle1.setFont(new Font("Dialog", Font.BOLD, 70));
		lblCircle1.setForeground(new Color(180, 70, 75));
		lblCircle1.setBounds(10, -10, 90, 90);
		leftPanel.add(lblCircle1);

		JLabel lblCircle2 = new JLabel("●");
		lblCircle2.setFont(new Font("Dialog", Font.BOLD, 25));
		lblCircle2.setForeground(new Color(160, 60, 110));
		lblCircle2.setBounds(355, 80, 40, 40);
		leftPanel.add(lblCircle2);

		JLabel lblCircle3 = new JLabel("●");
		lblCircle3.setFont(new Font("Dialog", Font.BOLD, 18));
		lblCircle3.setForeground(new Color(210, 130, 150));
		lblCircle3.setBounds(360, 295, 30, 30);
		leftPanel.add(lblCircle3);

		JLabel lblTitle = new JLabel("Set your new password.");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(new Color(112, 23, 79));
		lblTitle.setBounds(35, 395, 380, 35);
		leftPanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Secure your account with a new password");
		lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSubtitle.setForeground(new Color(112, 23, 79));
		lblSubtitle.setBounds(45, 432, 360, 22);
		leftPanel.add(lblSubtitle);
	}

	private void addRightPanelComponents() {
		JLabel lblLogo = new JLabel("✣");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
		lblLogo.setForeground(new Color(134, 33, 95));
		lblLogo.setBounds(135, 35, 50, 40);
		rightPanel.add(lblLogo);

		JLabel lblHeading = new JLabel("Reset Password");
		lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblHeading.setForeground(new Color(70, 70, 70));
		lblHeading.setBounds(62, 90, 220, 35);
		rightPanel.add(lblHeading);

		JLabel lblDesc = new JLabel("Create a new password for your account");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(130, 130, 130));
		lblDesc.setBounds(35, 125, 250, 20);
		rightPanel.add(lblDesc);

		JLabel lblNewPassword = new JLabel("New Password");
		lblNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblNewPassword.setForeground(new Color(120, 120, 120));
		lblNewPassword.setBounds(42, 175, 100, 20);   // moved up from 245
		rightPanel.add(lblNewPassword);

		txtNewPassword = new JPasswordField();
		txtNewPassword.setBounds(42, 198, 235, 32);   // moved up
		txtNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtNewPassword.setBackground(Color.WHITE);
		txtNewPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtNewPassword);

		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblConfirmPassword.setForeground(new Color(120, 120, 120));
		lblConfirmPassword.setBounds(42, 250, 120, 20);  // moved up
		rightPanel.add(lblConfirmPassword);

		txtConfirmPassword = new JPasswordField();
		txtConfirmPassword.setBounds(42, 273, 235, 32);  // moved up
		txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtConfirmPassword.setBackground(Color.WHITE);
		txtConfirmPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtConfirmPassword);

		btnResetPassword = new JButton("Reset Password");
		btnResetPassword.setBounds(42, 335, 235, 36);   // moved up
		btnResetPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnResetPassword.setBackground(new Color(134, 33, 95));
		btnResetPassword.setForeground(Color.WHITE);
		btnResetPassword.setFocusPainted(false);
		btnResetPassword.setBorderPainted(false);
		rightPanel.add(btnResetPassword);

		btnBackToLogin = new JButton("Back to Login");
		btnBackToLogin.setBounds(92, 390, 140, 28);    // moved up
		btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnBackToLogin.setForeground(new Color(134, 33, 95));
		btnBackToLogin.setBackground(new Color(245, 245, 245));
		btnBackToLogin.setBorderPainted(false);
		btnBackToLogin.setFocusPainted(false);
		rightPanel.add(btnBackToLogin);
	}

	private void addActions() {
		btnResetPassword.addActionListener(e -> resetPassword());

		btnBackToLogin.addActionListener(e -> {
			dispose();
			new LoginFrame().setVisible(true);
		});
	}

	private void resetPassword() {
		String newPassword = new String(txtNewPassword.getPassword()).trim();
		String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

		if (newPassword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter new password");
			txtNewPassword.requestFocus();
			return;
		}

		if (confirmPassword.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter confirm password");
			txtConfirmPassword.requestFocus();
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match");
			txtConfirmPassword.requestFocus();
			return;
		}

		try {
			UserDAO userDAO = new UserDAO();
			boolean updated = userDAO.updatePassword(userEmail, newPassword);

			if (updated) {
				JOptionPane.showMessageDialog(this, "Password reset successful!\nYou can now log in with your new password.");
				dispose();
				new LoginFrame().setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Password reset failed. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ResetPasswordFrame("demo@gmail.com").setVisible(true);
	}
}