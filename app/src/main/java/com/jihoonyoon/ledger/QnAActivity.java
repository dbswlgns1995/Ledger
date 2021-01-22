package com.jihoonyoon.ledger;

import android.content.res.Resources;
import android.os.Bundle;

import com.example.ledger.R;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.honorato.multistatetogglebutton.ToggleButton;

public class QnAActivity extends AppCompatActivity {

    ImageView i1, i2, i3, cancel_imageview;
    ImageView t1, t2, t3;
    ToggleButton toggleButton;

    // qna activity dialog
    // 하드코딩

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qn_a);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Resources.getSystem().getDisplayMetrics().widthPixels;
        params.height = Resources.getSystem().getDisplayMetrics().heightPixels * 10/ 11;
        getWindow().setAttributes(params);

        i1 = findViewById(R.id.qna1_image);
        i2 = findViewById(R.id.qna2_image);
        i3 = findViewById(R.id.qna3_image);

        t1 = findViewById(R.id.qna1_text);
        t2 = findViewById(R.id.qna2_text);
        t3 = findViewById(R.id.qna3_text);

        cancel_imageview = findViewById(R.id.qna_cancel_imageview);
        cancel_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        i1.setImageResource(R.drawable.n1_1);
        i2.setImageResource(R.drawable.n1_2);
        i3.setImageResource(R.drawable.n1_3);

        t1.setImageResource(R.drawable.t1_1);
        t2.setImageResource(R.drawable.t1_2);
        t3.setImageResource(R.drawable.t1_3);

        toggleButton = findViewById(R.id.qna_togglebtn);
        toggleButton.setValue(0);
        toggleButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0:
                        i1.setImageResource(R.drawable.n1_1);
                        i2.setImageResource(R.drawable.n1_2);
                        i3.setImageResource(R.drawable.n1_3);
                        t1.setImageResource(R.drawable.t1_1);
                        t2.setImageResource(R.drawable.t1_2);
                        t3.setImageResource(R.drawable.t1_3);
                        break;
                    case 1:
                        i1.setImageResource(R.drawable.n2_1);
                        i2.setImageResource(R.drawable.n2_2);
                        i3.setImageResource(R.drawable.n2_3);
                        t1.setImageResource(R.drawable.t2_1);
                        t2.setImageResource(R.drawable.t2_2);
                        t3.setImageResource(R.drawable.t2_3);
                        break;
                    case 2:
                        i1.setImageResource(R.drawable.n3_1);
                        i2.setImageResource(R.drawable.n3_2);
                        i3.setImageResource(R.drawable.n3_3);
                        t1.setImageResource(R.drawable.t3_1);
                        t2.setImageResource(R.drawable.t3_2);
                        t3.setImageResource(R.drawable.t3_3);
                        break;
                    case 3:
                        i1.setImageResource(R.drawable.n4_1);
                        i2.setImageResource(R.drawable.n4_2);
                        i3.setImageResource(R.drawable.n4_3);
                        t1.setImageResource(R.drawable.t4_1);
                        t2.setImageResource(R.drawable.t4_2);
                        t3.setImageResource(R.drawable.t4_3);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}