package com.jihoonyoon.ledger;

public class StatisticsItem {
    private String title;
    private int cost;

    public StatisticsItem() {
    }

    public StatisticsItem(String title, int cost) {
        this.title = title;
        this.cost = cost;
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
}
