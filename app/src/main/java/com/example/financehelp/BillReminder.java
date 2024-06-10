package com.example.financehelp;

public class BillReminder {

    String billId, description;
    int day;
    boolean flag;
    public BillReminder() {
    }

    public BillReminder(String billId, String description, int day, boolean flag) {
        this.billId = billId;
        this.description = description;
        this.day = day;
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
