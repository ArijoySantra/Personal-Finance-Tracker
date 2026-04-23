package view;

import java.awt.*;
import javax.swing.*;

import controller.AuthController;
import model.User;
import utils.SessionManager;

public class LoginFrame extends JFrame {

	private JPanel mainPanel, cardPanel, leftPanel, rightPanel;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	private JButton btnLogin, btnRegister, btnForgotPassword, btnGoogle;
	private JCheckBox chkRemember;

	public LoginFrame() {
		setTitle("Personal Finance System - Login");
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
		cardPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		mainPanel.add(cardPanel);

		leftPanel = new JPanel(null);
		leftPanel.setBounds(0, 0, 460, 500);
		leftPanel.setBackground(new Color(240, 220, 194));
		cardPanel.add(leftPanel);

		rightPanel = new JPanel(null);
		rightPanel.setBounds(460, 0, 320, 500);
		rightPanel.setBackground(new Color(250, 250, 250));
		cardPanel.add(rightPanel);

		addLeftPanelComponents();
		addRightPanelComponents();
		addActions();
	}

	private void addLeftPanelComponents() {
		JLabel lblImage = new JLabel();
		lblImage.setBounds(35, 30, 390, 300);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);

		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/my_image.png"));
		Image img = icon.getImage().getScaledInstance(390, 300, Image.SCALE_SMOOTH);
		lblImage.setIcon(new ImageIcon(img));
		leftPanel.add(lblImage);

		JLabel lblTitle = new JLabel("Turn your ideas into reality.", SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(new Color(112, 23, 79));
		lblTitle.setBounds(45, 380, 360, 35);
		leftPanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Track your income and expenses smartly", SwingConstants.CENTER);
		lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSubtitle.setForeground(new Color(112, 23, 79));
		lblSubtitle.setBounds(60, 415, 330, 22);
		leftPanel.add(lblSubtitle);
	}

	private void addRightPanelComponents() {
		JLabel lblHeading = new JLabel("Login to your Account");
		lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblHeading.setForeground(new Color(60, 60, 60));
		lblHeading.setBounds(42, 80, 250, 35);
		rightPanel.add(lblHeading);

		JLabel lblDesc = new JLabel("Manage your finances efficiently");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(130, 130, 130));
		lblDesc.setBounds(42, 110, 240, 20);
		rightPanel.add(lblDesc);

		btnGoogle = new JButton("Continue with Google");
		btnGoogle.setBounds(42, 140, 235, 32);
		btnGoogle.setBackground(Color.WHITE);
		btnGoogle.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		btnGoogle.setFocusPainted(false);
		rightPanel.add(btnGoogle);

		JLabel lblOr = new JLabel("or sign in with email", SwingConstants.CENTER);
		lblOr.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		lblOr.setForeground(new Color(150, 150, 150));
		lblOr.setBounds(80, 180, 150, 15);
		rightPanel.add(lblOr);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(42, 210, 80, 20);
		rightPanel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setBounds(42, 230, 235, 34);
		txtEmail.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtEmail);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(42, 275, 80, 20);
		rightPanel.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(42, 295, 235, 34);
		txtPassword.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtPassword);

		chkRemember = new JCheckBox("Remember Me");
		chkRemember.setBounds(42, 335, 120, 20);
		chkRemember.setBackground(new Color(250, 250, 250));
		rightPanel.add(chkRemember);

		btnForgotPassword = new JButton("Forgot Password?");
		btnForgotPassword.setBounds(165, 335, 130, 20);
		btnForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		btnForgotPassword.setForeground(new Color(134, 33, 95));
		btnForgotPassword.setBorderPainted(false);
		btnForgotPassword.setContentAreaFilled(false);
		btnForgotPassword.setFocusPainted(false);
		rightPanel.add(btnForgotPassword);

		btnLogin = new JButton("Login");
		btnLogin.setBounds(42, 370, 235, 40);
		btnLogin.setBackground(new Color(134, 33, 95));
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnLogin.setFocusPainted(false);
		btnLogin.setBorderPainted(false);
		rightPanel.add(btnLogin);

		JLabel lblBottom = new JLabel("Not Registered Yet?");
		lblBottom.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblBottom.setForeground(new Color(120, 120, 120));
		lblBottom.setBounds(60, 425, 120, 20);
		rightPanel.add(lblBottom);

		btnRegister = new JButton("Create an account");
		btnRegister.setBounds(165, 423, 140, 24);
		btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnRegister.setForeground(new Color(134, 33, 95));
		btnRegister.setBorderPainted(false);
		btnRegister.setContentAreaFilled(false);
		btnRegister.setFocusPainted(false);
		rightPanel.add(btnRegister);
	}

	private void addActions() {
		btnLogin.addActionListener(e -> loginUser());

		btnRegister.addActionListener(e -> {
			dispose();
			new RegistrationFrame().setVisible(true);
		});

		btnForgotPassword.addActionListener(e -> {
			dispose();
			new ForgotPasswordFrame().setVisible(true);
		});

		btnGoogle.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "Google login coming soon");
		});
	}

	private void loginUser() {
		String email = txtEmail.getText().trim();
		String password = new String(txtPassword.getPassword()).trim();

		if (email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter email");
			return;
		}

		if (password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter password");
			return;
		}

		try {
			AuthController controller = new AuthController();
			User user = controller.login(email, password);

			if (user != null) {
				SessionManager.setCurrentUser(user);
				JOptionPane.showMessageDialog(this, "Login successful");
				dispose();
				new MainFrame(user).setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "Invalid credentials");
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Something went wrong");
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
	}
}