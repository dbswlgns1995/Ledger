package com.jihoonyoon.ledger;

import io.realm.RealmObject;

public class IncomeItem extends RealmObject {
    private String title;
    private int cost, date;

    public IncomeItem() {
    }

    public IncomeItem(String title, int cost, int date) {
        this.title = title;
        this.cost = cost;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
