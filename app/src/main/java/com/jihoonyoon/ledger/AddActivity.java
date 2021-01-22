package com.jihoonyoon.ledger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ledger.R;

import io.realm.Realm;

public class AddActivity extends AppCompatActivity {

    TextView title_text;
    EditText title_edit;
    Button save_btn;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        title_text = findViewById(R.id.add_title_text);
        title_edit = findViewById(R.id.add_title_edit);
        save_btn = findViewById(R.id.add_save_btn);

        Realm.init(this); //Realm.init(getContext());
        realm = Realm.getDefaultInstance();

        // 수입, 수출 intent 얻기
        Intent intent = getIntent();
        final String select = intent.getStringExtra("add");
        final String main_title = intent.getStringExtra("mainclass");
        final Boolean ex_or_in = intent.getBooleanExtra("subclass", true);

        if(select!=null && select.equals("expend")){ // 지출
            title_text.setText("지출 대분류 추가");
        }else if(select!=null && select.equals("income")){ // 수입
            title_text.setText("수입 대분류 추가");
        }else if(select==null && ex_or_in == true){
            title_text.setText("지출 소분류 추가");
        }else if(select==null && ex_or_in == false){
            title_text.setText("수입 소분류 추가");
        }


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title_edit.getText().toString().equals("")) {
                    if (select != null && select.equals("expend")) { // 지출 대분류
                        if (realm.where(MainClass_expend.class).equalTo("title", title_edit.getText().toString()).count() == 0) {
                            MainClass_expend mainClass = new MainClass_expend(title_edit.getText().toString());
                            realm.beginTransaction();
                            MainClass_expend realm_main = realm.copyToRealm(mainClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "지출 대분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 대분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }


                    } else if (select != null && select.equals("income")) { // 수입 소분류

                        if (realm.where(MainClass_income.class).equalTo("title", title_edit.getText().toString()).count() == 0) {
                            MainClass_income mainClass = new MainClass_income(title_edit.getText().toString());
                            realm.beginTransaction();
                            MainClass_income realm_main = realm.copyToRealm(mainClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "수입 대분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 대분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }


                    } else if (select == null && ex_or_in == true) {  // 지출 소분류

                        if (realm.where(SubClass_expend.class).equalTo("sub_title", title_edit.getText().toString()).count() == 0) {
                            SubClass_expend subClass = new SubClass_expend(main_title, title_edit.getText().toString());
                            realm.beginTransaction();
                            SubClass_expend realm_sub = realm.copyToRealm(subClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "지출 소분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 소분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }

                    } else if (select == null && ex_or_in == false) {

                        if (realm.where(SubClass_income.class).equalTo("sub_title", title_edit.getText().toString()).count() == 0) {
                            SubClass_income subClass = new SubClass_income(main_title, title_edit.getText().toString());
                            realm.beginTransaction();
                            SubClass_income realm_sub = realm.copyToRealm(subClass);
                            realm.commitTransaction();
                            Log.d("AddActivity", "수입 소분류 " + title_edit.getText().toString() + "추가");
                            finish();
                        } else {
                            Toast.makeText(AddActivity.this, "이미 사용중인 소분류 이름 입니다!", Toast.LENGTH_LONG).show();
                        }

                    }
                }else{
                    Toast.makeText(AddActivity.this, "공백은 저장할 수 없습니다!", Toast.LENGTH_LONG).show();
                }


            }
        });






    }
}
