package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Card {
    private int id;
    private int userId;
    private String cardName;
    private String cardType;
    private double creditLimit;
    private double currentBalance;
    private Date dueDate;
    private Timestamp createdAt;
    private Integer associatedAccountId;
    private double interestRate;
    private boolean paidOffMonthly;
    private int startingDay;
    private int paymentDay;
    private boolean automaticPayment;
    private String notes;

    public Card() {}

    public Card(int userId, String cardName, String cardType, double creditLimit) {
        this.userId = userId;
        this.cardName = cardName;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    public double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }


    public Integer getAssociatedAccountId() { return associatedAccountId; }
    public void setAssociatedAccountId(Integer associatedAccountId) { this.associatedAccountId = associatedAccountId; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public boolean isPaidOffMonthly() { return paidOffMonthly; }
    public void setPaidOffMonthly(boolean paidOffMonthly) { this.paidOffMonthly = paidOffMonthly; }

    public int getStartingDay() { return startingDay; }
    public void setStartingDay(int startingDay) { this.startingDay = startingDay; }

    public int getPaymentDay() { return paymentDay; }
    public void setPaymentDay(int paymentDay) { this.paymentDay = paymentDay; }

    public boolean isAutomaticPayment() { return automaticPayment; }
    public void setAutomaticPayment(boolean automaticPayment) { this.automaticPayment = automaticPayment; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}