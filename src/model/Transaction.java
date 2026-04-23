package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Transaction {
    private int id;
    private int userId;
    private Integer accountId;
    private Integer cardId;
    private int categoryId;
    private double amount;
    private String type;
    private String description;
    private Date transactionDate;
    private Timestamp createdAt;
    private String accountName;
    private String cardName;
    private String categoryName;

    public Transaction() {}

    public Transaction(int userId, Integer accountId, Integer cardId, int categoryId, double amount, String type, String description, Date transactionDate) {
        this.userId = userId;
        this.accountId = accountId;
        this.cardId = cardId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.transactionDate = transactionDate;
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
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getSourceName() {
        if (accountName != null && !accountName.isEmpty()) return accountName;
        if (cardName != null && !cardName.isEmpty()) return cardName;
        return "Unknown";
    }


}