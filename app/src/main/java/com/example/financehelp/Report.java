package com.example.financehelp;

public class Report {
    String budgetId;
    String userId;
    Double budgetAmount;

    public Report() {
    }

    public Report(String budgetId, String userId, Double budgetAmount) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.budgetAmount = budgetAmount;
    }

    public Double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(Double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
