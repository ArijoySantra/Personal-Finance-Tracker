package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Debt {
    private int id;
    private int userId;
    private String name;
    private String type;      // "LENT" or "BORROWED"
    private double amount;
    private double remaining;
    private Date dueDate;
    private double interestRate;
    private Timestamp createdAt;
    private double emiAmount;

    public Debt() {}

    public Debt(int userId, String name, String type, double amount, double remaining,
                Date dueDate, double interestRate, double emiAmount) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.remaining = remaining;
        this.dueDate = dueDate;
        this.interestRate = interestRate;
        this.emiAmount = emiAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getRemaining() { return remaining; }
    public void setRemaining(double remaining) { this.remaining = remaining; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public double getEmiAmount() { return emiAmount; }
    public void setEmiAmount(double emiAmount) { this.emiAmount = emiAmount; }
}