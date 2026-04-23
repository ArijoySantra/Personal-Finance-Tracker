package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Scheduled {
    private int id;
    private int userId;
    private Integer accountId;
    private Integer cardId;
    private int categoryId;
    private double amount;
    private String type;         // "INCOME" or "EXPENSE"
    private String description;
    private String frequency;    // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    private Date nextDate;
    private Timestamp createdAt;

    // Joined fields for display
    private String accountName;
    private String cardName;
    private String categoryName;

    public Scheduled() {}

    public Scheduled(int userId, Integer accountId, Integer cardId, int categoryId,
                     double amount, String type, String description,
                     String frequency, Date nextDate) {
        this.userId = userId;
        this.accountId = accountId;
        this.cardId = cardId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.frequency = frequency;
        this.nextDate = nextDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Date getNextDate() { return nextDate; }
    public void setNextDate(Date nextDate) { this.nextDate = nextDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

}