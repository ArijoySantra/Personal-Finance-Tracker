package controller;

import database.UserDAO;
import model.User;

public class AuthController {

    private UserDAO userDAO;

    public AuthController() {
        userDAO = new UserDAO();
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter email");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter password");
        }
        return userDAO.loginUser(email, password);
    }

    public boolean register(User user) {
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userDAO.emailExists(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (userDAO.phoneExists(user.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }
        return userDAO.registerUser(user);
    }
}