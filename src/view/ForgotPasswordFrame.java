package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ForgotPasswordFrame extends JFrame {

	private static final long serialVersionUID = 1L;


	private static final long OTP_EXPIRY_SECONDS = 300;

	private static final int MAX_ATTEMPTS = 3;

	private static final long RESEND_COOLDOWN_SECONDS = 30;


	private final ConcurrentHashMap<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

	private final SecureRandom secureRandom = new SecureRandom();

	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private JPanel mainPanel, cardPanel, leftPanel, rightPanel;
	private JTextField txtEmail, txtOtp;
	private JButton btnSendOtp, btnVerifyOtp, btnBackToLogin;
	private JLabel lblTimer;

	private String currentEmail = "";
	private long resendAvailableTime = 0;
	private Timer cooldownTimer;

	public ForgotPasswordFrame() {
		setTitle("Personal Finance System - Forgot Password");
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
		ImageIcon icon = new ImageIcon("src/resources/start_image.png");
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

		JLabel lblTitle = new JLabel("Recover your account securely.");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitle.setForeground(new Color(112, 23, 79));
		lblTitle.setBounds(35, 395, 380, 35);
		leftPanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("We'll send a 6‑digit code to your email");
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

		JLabel lblHeading = new JLabel("Forgot Password");
		lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblHeading.setForeground(new Color(70, 70, 70));
		lblHeading.setBounds(52, 90, 230, 35);
		rightPanel.add(lblHeading);

		JLabel lblDesc = new JLabel("Enter your email to receive a code");
		lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblDesc.setForeground(new Color(130, 130, 130));
		lblDesc.setBounds(52, 125, 240, 20);
		rightPanel.add(lblDesc);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblEmail.setForeground(new Color(120, 120, 120));
		lblEmail.setBounds(42, 180, 80, 20);
		rightPanel.add(lblEmail);

		txtEmail = new JTextField();
		txtEmail.setBounds(42, 203, 235, 32);
		txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtEmail.setBackground(Color.WHITE);
		txtEmail.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtEmail);

		btnSendOtp = new JButton("Send Code");
		btnSendOtp.setBounds(42, 248, 235, 34);
		btnSendOtp.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnSendOtp.setBackground(new Color(134, 33, 95));
		btnSendOtp.setForeground(Color.WHITE);
		btnSendOtp.setFocusPainted(false);
		btnSendOtp.setBorderPainted(false);
		rightPanel.add(btnSendOtp);

		// 倒计时标签（初始隐藏）
		lblTimer = new JLabel("");
		lblTimer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTimer.setForeground(new Color(180, 0, 0));
		lblTimer.setBounds(200, 285, 80, 20);
		lblTimer.setHorizontalAlignment(SwingConstants.RIGHT);
		rightPanel.add(lblTimer);

		JLabel lblOtp = new JLabel("Enter Code");
		lblOtp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblOtp.setForeground(new Color(120, 120, 120));
		lblOtp.setBounds(42, 305, 80, 20);
		rightPanel.add(lblOtp);

		txtOtp = new JTextField();
		txtOtp.setBounds(42, 328, 235, 32);
		txtOtp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtOtp.setBackground(Color.WHITE);
		txtOtp.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
		rightPanel.add(txtOtp);

		btnVerifyOtp = new JButton("Verify Code");
		btnVerifyOtp.setBounds(42, 380, 235, 34);
		btnVerifyOtp.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnVerifyOtp.setBackground(new Color(134, 33, 95));
		btnVerifyOtp.setForeground(Color.WHITE);
		btnVerifyOtp.setFocusPainted(false);
		btnVerifyOtp.setBorderPainted(false);
		rightPanel.add(btnVerifyOtp);

		btnBackToLogin = new JButton("Back to Login");
		btnBackToLogin.setBounds(92, 438, 140, 28);
		btnBackToLogin.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnBackToLogin.setForeground(new Color(134, 33, 95));
		btnBackToLogin.setBackground(new Color(245, 245, 245));
		btnBackToLogin.setBorderPainted(false);
		btnBackToLogin.setFocusPainted(false);
		rightPanel.add(btnBackToLogin);
	}

	private void addActions() {
		btnSendOtp.addActionListener(e -> sendOtp());
		btnVerifyOtp.addActionListener(e -> verifyOtp());

		btnBackToLogin.addActionListener(e -> {
			scheduler.shutdownNow();
			if (cooldownTimer != null) cooldownTimer.stop();
			dispose();
			new LoginFrame().setVisible(true);
		});
	}

	private void sendOtp() {
		String email = txtEmail.getText().trim().toLowerCase();
		if (email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter your email address.");
			return;
		}

		long now = System.currentTimeMillis();
		if (now < resendAvailableTime) {
			long waitSec = (resendAvailableTime - now) / 1000;
			JOptionPane.showMessageDialog(this,
					"Please wait " + waitSec + " seconds before requesting a new code.");
			return;
		}

		String otp = String.format("%06d", secureRandom.nextInt(1_000_000));
		long expiryTime = System.currentTimeMillis() + OTP_EXPIRY_SECONDS * 1000;


		otpStore.put(email, new OtpRecord(otp, expiryTime, 0));
		currentEmail = email;
		System.out.println("=========================================");
		System.out.println("To: " + email);
		System.out.println("Subject: Your FinTrack Verification Code");
		System.out.println("Your OTP is: " + otp);
		System.out.println("This code will expire in 5 minutes.");
		System.out.println("=========================================");


		resendAvailableTime = now + RESEND_COOLDOWN_SECONDS * 1000;
		btnSendOtp.setEnabled(false);
		startCooldownTimer();

		JOptionPane.showMessageDialog(this,
				"A verification code has been sent to:\n" + email + "\n\nPlease check your email (or console output).",
				"Code Sent", JOptionPane.INFORMATION_MESSAGE);
	}


	private void verifyOtp() {
		String email = txtEmail.getText().trim().toLowerCase();
		String enteredOtp = txtOtp.getText().trim();

		if (email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter your email address.");
			return;
		}
		if (enteredOtp.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter the verification code.");
			return;
		}

		OtpRecord record = otpStore.get(email);
		if (record == null) {
			JOptionPane.showMessageDialog(this,
					"No active verification code found for this email.\nPlease request a new code.");
			return;
		}


		if (System.currentTimeMillis() > record.expiryTime) {
			otpStore.remove(email);
			JOptionPane.showMessageDialog(this,
					"The verification code has expired.\nPlease request a new one.");
			return;
		}


		if (record.attempts >= MAX_ATTEMPTS) {
			otpStore.remove(email);
			JOptionPane.showMessageDialog(this,
					"Too many failed attempts.\nPlease request a new verification code.");
			return;
		}

		if (!record.otp.equals(enteredOtp)) {
			record.attempts++;
			otpStore.put(email, record);
			int remaining = MAX_ATTEMPTS - record.attempts;
			JOptionPane.showMessageDialog(this,
					"Invalid verification code.\n" + remaining + " attempt(s) remaining.");
			return;
		}


		otpStore.remove(email);
		JOptionPane.showMessageDialog(this, "Verification successful!");
		dispose();
		scheduler.shutdownNow();
		if (cooldownTimer != null) cooldownTimer.stop();
		new ResetPasswordFrame(email).setVisible(true);
	}

	private void startCooldownTimer() {
		if (cooldownTimer != null && cooldownTimer.isRunning()) {
			cooldownTimer.stop();
		}
		cooldownTimer = new Timer(200, e -> {
			long now = System.currentTimeMillis();
			long remaining = resendAvailableTime - now;
			if (remaining <= 0) {
				btnSendOtp.setEnabled(true);
				btnSendOtp.setText("Resend Code");
				lblTimer.setText("");
				((Timer) e.getSource()).stop();
			} else {
				long seconds = remaining / 1000;
				lblTimer.setText("Resend in " + seconds + "s");
			}
		});
		cooldownTimer.start();
		lblTimer.setText("Resend in " + RESEND_COOLDOWN_SECONDS + "s");
	}

	private static class OtpRecord {
		final String otp;
		final long expiryTime;
		int attempts;

		OtpRecord(String otp, long expiryTime, int attempts) {
			this.otp = otp;
			this.expiryTime = expiryTime;
			this.attempts = attempts;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new ForgotPasswordFrame().setVisible(true));
	}
}