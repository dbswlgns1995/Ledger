package com.jihoonyoon.ledger;

import io.realm.RealmObject;

public class MainClass_income extends RealmObject {
    private String title;

    public MainClass_income() {
    }

    public MainClass_income(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
