package com.example.financehelp;

public class Transaction {
    private String category;
    private double amount;
    private String date;
    private String budgetId;
    public Transaction() {

    }

    public Transaction(String category, double amount, String date, String budgetId) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.budgetId = budgetId;
    }

    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }
}
