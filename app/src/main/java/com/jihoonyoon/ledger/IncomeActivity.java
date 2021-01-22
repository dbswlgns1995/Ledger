package com.jihoonyoon.ledger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ledger.R;

import io.realm.Realm;
import io.realm.RealmResults;

public class IncomeActivity extends AppCompatActivity {

    // 수입 액티비티

    Button add_btn;
    RecyclerView recyclerView;
    Realm realm;
    MainClassIncomeAdapter mainClassIncomeAdapter;
    ImageView back_imageview;

    int date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Intent intent = getIntent();
        date = intent.getIntExtra("date",0);

        back_imageview = findViewById(R.id.income_back_imageview);
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 대분류 추가
        add_btn = (Button) findViewById(R.id.income_add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("add", "income");
                startActivity(intent);
            }
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<MainClass_income> realmResults = realm.where(MainClass_income.class).findAll();
        recyclerView = findViewById(R.id.income_recyclerview);
        mainClassIncomeAdapter = new MainClassIncomeAdapter(realmResults, true, this, date);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mainClassIncomeAdapter);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
