package com.example.myapplication.model;

public class BudgetModel {
    private int id;
    private String name;
    private int money;
    private String date;
    private String createdAt;

    public BudgetModel(int id, String name, int money, String date) {
        this.id = id;
        this.name = name;
        this.money = money;
        this.date = date;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public String getDate() {
        return date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
