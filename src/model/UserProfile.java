package model;

public class UserProfile {
    private int id;
    private int userId;
    private double monthlyIncome;
    private String occupation;
    private String financialGoal;
    private String country;

    public UserProfile() {}

    public UserProfile(int userId, double monthlyIncome, String occupation, String financialGoal, String country) {
        this.userId = userId;
        this.monthlyIncome = monthlyIncome;
        this.occupation = occupation;
        this.financialGoal = financialGoal;
        this.country = country;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getFinancialGoal() { return financialGoal; }
    public void setFinancialGoal(String financialGoal) { this.financialGoal = financialGoal; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}