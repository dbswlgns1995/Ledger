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

public class ExpendActivity extends AppCompatActivity {

    // 지출 액티비티

    Button add_btn;
    RecyclerView recyclerView;
    Realm realm;
    MainClassExpendAdapter mainClassExpendAdapter;
    ImageView back_imageview;

    int date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expend);

        Intent intent = getIntent();
        date = intent.getIntExtra("date", 0);

        back_imageview = findViewById(R.id.expend_back_imageview);
        back_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // 대분류 추가
        add_btn = (Button) findViewById(R.id.expend_add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtra("add", "expend");
                startActivity(intent);
            }
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        RealmResults<MainClass_expend> realmResults = realm.where(MainClass_expend.class).findAll();
        recyclerView = findViewById(R.id.expend_recyclerview);
        mainClassExpendAdapter = new MainClassExpendAdapter(realmResults, true, this, date);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mainClassExpendAdapter);


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
