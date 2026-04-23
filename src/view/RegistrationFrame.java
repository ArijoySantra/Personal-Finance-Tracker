package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.*;

import controller.AuthController;
import model.User;

public class RegistrationFrame extends JFrame {

	private JPanel mainPanel, cardPanel, leftPanel, rightPanel;

	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtPhone;
	private JPasswordField txtPassword;
	private JPasswordField txtConfirmPassword;

	private JButton btnRegister;
	private JButton btnBackToLogin;

	public RegistrationFrame() {
		setTitle("Personal Finance System - Register");
		setSize(1100, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		mainPanel = new JPanel(null);
		mainPanel.setBackground(new Color(238, 217, 191));
		setContentPane(mainPanel);

		cardPanel = new JPanel(null);
		cardPanel.setBounds(160, 80, 780, 500);
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255), 2));
		mainPanel.add(cardPanel);

		leftPanel = new JPanel(null);
		leftPanel.setBounds(0, 0, 460, 500);
		leftPanel.setBackground(new Color(240, 220, 194));
		cardPanel.add(leftPanel);

		rightPanel = new JPanel(null);
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

		ImageIcon icon = new ImageIcon("src/resources/start_image.png");
		Image scaledImage = icon.getImage().getScaledInstance(350, 299, Image.SCALE_SMOOTH);
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

		JLabel lblTitle = new JLabel("Start your smart finance journey.");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(new Color(112, 23, 79));
		lblTitle.setBounds(35, 395, 380, 35);
		leftPanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Create your account and manage money easily");
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
		lblLogo.setBounds(135, 20, 50, 40);
		rightPanel.add(lblLogo);

		JLabel lblHeading = new JLabel("Create Account");
		lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblHeading.setForeground(new Color(70, 70, 70));
		lblHeading.setBounds(65, 60, 220, 35);
		rightPanel.add(lblHeading);

		JLabel lblDesc = new JLabel("Register to manage your finance system");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(130, 130, 130));
		lblDesc.setBounds(42, 95, 240, 20);
		rightPanel.add(lblDesc);

		// Full Name
		JLabel lblName = new JLabel("Full Name");
		lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblName.setForeground(new Color(120, 120, 120));
		lblName.setBounds(42, 130, 80, 20);
		rightPanel.add(lblName);

		txtName = new JTextField();
		txtName.setBounds(42, 152, 235, 30);
		txtName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtName.setBackground(Color.WHITE);
		txtName.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtName);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblEmail.setForeground(new Color(120, 120, 120));
		lblEmail.setBounds(42, 188, 80, 20);
		rightPanel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setBounds(42, 210, 235, 30);
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtEmail.setBackground(Color.WHITE);
		txtEmail.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtEmail);

		JLabel lblPhone = new JLabel("Phone");
		lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPhone.setForeground(new Color(120, 120, 120));
		lblPhone.setBounds(42, 246, 80, 20);
		rightPanel.add(lblPhone);

		txtPhone = new JTextField();
		txtPhone.setBounds(42, 268, 235, 30);
		txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtPhone.setBackground(Color.WHITE);
		txtPhone.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtPhone);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPassword.setForeground(new Color(120, 120, 120));
		lblPassword.setBounds(42, 304, 80, 20);
		rightPanel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(42, 326, 235, 30);
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtPassword.setBackground(Color.WHITE);
		txtPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtPassword);

		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblConfirmPassword.setForeground(new Color(120, 120, 120));
		lblConfirmPassword.setBounds(42, 362, 120, 20);
		rightPanel.add(lblConfirmPassword);

		txtConfirmPassword = new JPasswordField();
		txtConfirmPassword.setBounds(42, 384, 235, 30);
		txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtConfirmPassword.setBackground(Color.WHITE);
		txtConfirmPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtConfirmPassword);

		btnRegister = new JButton("Register");
		btnRegister.setBounds(42, 425, 235, 34);
		btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnRegister.setBackground(new Color(134, 33, 95));
		btnRegister.setForeground(Color.WHITE);
		btnRegister.setFocusPainted(false);
		btnRegister.setBorderPainted(false);
		rightPanel.add(btnRegister);

		JLabel lblBottom = new JLabel("Already have an account?");
		lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblBottom.setForeground(new Color(120, 120, 120));
		lblBottom.setBounds(52, 468, 130, 20);
		rightPanel.add(lblBottom);

		btnBackToLogin = new JButton("Login");
		btnBackToLogin.setBounds(185, 466, 75, 24);
		btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnBackToLogin.setForeground(new Color(134, 33, 95));
		btnBackToLogin.setBackground(new Color(245, 245, 245));
		btnBackToLogin.setBorderPainted(false);
		btnBackToLogin.setFocusPainted(false);
		rightPanel.add(btnBackToLogin);
	}

	private void addActions() {
		btnRegister.addActionListener(e -> {
			String fullName = txtName.getText().trim();
			String email = txtEmail.getText().trim();
			String phoneText = txtPhone.getText().trim();
			String password = new String(txtPassword.getPassword()).trim();
			String confirm = new String(txtConfirmPassword.getPassword()).trim();

			// Validation
			if (fullName.isEmpty() || email.isEmpty() || phoneText.isEmpty() ||
					password.isEmpty() || confirm.isEmpty()) {
				JOptionPane.showMessageDialog(this, "All fields are required.");
				return;
			}

			if (!password.equals(confirm)) {
				JOptionPane.showMessageDialog(this, "Passwords do not match.");
				return;
			}

			int phone;
			try {
				phone = Integer.parseInt(phoneText);
				if (phoneText.length() != 10) { // Example validation
					JOptionPane.showMessageDialog(this, "Phone number must be 10 digits.");
					return;
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Phone must be a valid number.");
				return;
			}

			try {
				User user = new User(fullName, email, phone, password);
				AuthController authController = new AuthController();
				boolean success = authController.register(user);

				if (success) {
					JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
					dispose();
					new LoginFrame().setVisible(true);
				} else {
					JOptionPane.showMessageDialog(this, "Registration failed. Email or phone may already exist.");
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
			}
		});

		btnBackToLogin.addActionListener(e -> {
			dispose();
			new LoginFrame().setVisible(true);
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new RegistrationFrame().setVisible(true));
	}
}