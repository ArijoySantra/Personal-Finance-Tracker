package model;

import java.sql.Date;

public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private Date monthYear;
    private String period;
    private Date createdAt;
    private String categoryName;
    private double spent;

    public Budget() {}

    public Budget(int userId, int categoryId, double amount, Date monthYear, String period) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.monthYear = monthYear;
        this.period = period;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getMonthYear() { return monthYear; }
    public void setMonthYear(Date monthYear) { this.monthYear = monthYear; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }

    public int getPercentage() {
        if (amount == 0) return 0;
        return (int) Math.min(100, Math.round((spent / amount) * 100));
    }
}