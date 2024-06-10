package com.example.financehelp;

public class Budget {

    String budgetId;
    double netIncome, needs, entertainment, savings;
    String userId;
    int numDependents;
    public Budget() {
    }

    public Budget(String budgetId, double netIncome, double needs, double entertainment, double savings, String userId, int numDependents) {
        this.budgetId = budgetId;
        this.netIncome = netIncome;
        this.needs = needs;
        this.entertainment = entertainment;
        this.savings = savings;
        this.userId = userId;
        this.numDependents = numDependents;
    }

    public int getNumDependents() {
        return numDependents;
    }

    public void setNumDependents(int numDependents) {
        this.numDependents = numDependents;
    }


    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public double getNeeds() {
        return needs;
    }

    public void setNeeds(double needs) {
        this.needs = needs;
    }

    public double getEntertainment() {
        return entertainment;
    }

    public void setEntertainment(double entertainment) {
        this.entertainment = entertainment;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
