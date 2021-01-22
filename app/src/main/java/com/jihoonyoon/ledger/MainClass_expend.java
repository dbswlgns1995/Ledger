package com.jihoonyoon.ledger;

import io.realm.RealmObject;

public class MainClass_expend extends RealmObject {
    private String title;

    public MainClass_expend() {
    }

    public MainClass_expend(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
