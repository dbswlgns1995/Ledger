package com.jihoonyoon.ledger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ledger.R;

public class SelectActivity extends AppCompatActivity{

    // 지출, 수입 선택 액티비티

    Button expend_btn, income_btn;
    ImageView cancel_imageview;
    int date;
    private static String TAG = "***selectActivty";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        // 윈도우 크기조절

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Resources.getSystem().getDisplayMetrics().widthPixels*3/4;
        params.height = Resources.getSystem().getDisplayMetrics().heightPixels/3;
        getWindow().setAttributes(params);

        Intent intent = getIntent();
        date = intent.getIntExtra("date",0);

        income_btn = (Button)findViewById(R.id.select_income_btn);
        expend_btn = (Button)findViewById(R.id.select_expend_btn);
        cancel_imageview = (ImageView)findViewById(R.id.select_cancel_imageview);

        income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), IncomeActivity.class);
                intent.putExtra("select", "income");
                intent.putExtra("date", date);
                Log.d(TAG, date+"");
                startActivity(intent);
                finish();
            }
        });
        expend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), ExpendActivity.class);
                intent.putExtra("select", "expend");
                intent.putExtra("date", date);
                Log.d(TAG, date+"");
                startActivity(intent);
                finish();
            }
        });

        cancel_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
