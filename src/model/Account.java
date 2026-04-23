package model;
import java.sql.*;
public class Account {
    private int id;
    private int userId;
    private String accountName;
    private String accountType; // "Bank", "Wallet", "Cash", "Other"
    private double balance;
    private String currency;
    private Timestamp createdAt;

    public Account() {}

    public Account(int userId, String accountName, String accountType, double balance) {
        this.userId = userId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}