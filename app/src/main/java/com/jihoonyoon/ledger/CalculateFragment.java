package com.jihoonyoon.ledger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ledger.R;

import java.text.DecimalFormat;

import io.realm.Realm;
import io.realm.RealmResults;


public class CalculateFragment extends Fragment {

    // 정산 fragment

    TextView result_text, date_text, income_text, expend_text;
    Realm realm;
    String expend, income;
    int sum;
    int date;
    String TAG = "***CalculateFragment";
    Button cal_btn, qna_btn;
    int expend_cost, income_cost;

    RecyclerView income_recyclerview, expend_recyclerview;
    CalculateIncomeAdapter calculateIncomeAdapter;
    CalculateExpendAdapter calculateExpendAdapter;
    ImageView alert_imageview;
    LinearLayout linearLayout;
    ConstraintLayout constraintLayout;

    public CalculateFragment(int date) {
        this.date = date;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calculate, container, false);

        result_text = root.findViewById(R.id.calculate_result_text);
        cal_btn = root.findViewById(R.id.calculate_cal_btn);
        date_text = root.findViewById(R.id.calculate_date_text);
        income_text = root.findViewById(R.id.calculate_income_text);
        expend_text = root.findViewById(R.id.calculate_expend_text);
        alert_imageview = root.findViewById(R.id.calculate_alert_imageview);
        linearLayout = root.findViewById(R.id.calculate_linear);
        constraintLayout = root.findViewById(R.id.calculate_back);
        qna_btn = root.findViewById(R.id.qna_btn);

        // 오늘 날짜 출력
        String date_str = Integer.toString(date);
        String date_text_str = "    " + date_str.substring(0, 4) + "년 " + date_str.substring(4, 6) + "월 " + date_str.substring(6) + "일";
        date_text.setText(date_text_str);

        // 정산하기 버튼 select activity dialog 로 intent
        cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectActivity.class);
                intent.putExtra("date", date);
                v.getContext().startActivity(intent);
            }
        });

        // qna 버튼 qna activity dialog 로 intent
        qna_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(getActivity(), QnAActivity.class));
            }
        });

        return root;
    }


    // 천 단위 콤마
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    // 수입/ 지출 / 합계 및 recycerview refresh 를 위한 onResume
    @Override
    public void onResume() {
        super.onResume();

        reset();
    }


    @Override
    public void onStop() {
        if (!realm.isClosed()) {
            realm.close();
        }
        super.onStop();
    }

    public void reset(){

        Log.d(TAG, "reset: ");

        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).equalTo("date", date).findAll();
        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).notEqualTo("cost", 0).equalTo("date", date).findAll();

        // 당일 수입 recyclerview
        income_recyclerview = getView().findViewById(R.id.calculate_income_recyclerview);
        income_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        calculateIncomeAdapter = new CalculateIncomeAdapter(incomeItemRealmResults, true);
        income_recyclerview.setAdapter(calculateIncomeAdapter);
        income_recyclerview.addOnItemTouchListener(mScrollTouchListener);
        income_recyclerview.setNestedScrollingEnabled(false);

        // 당일 지출 recyclerview
        expend_recyclerview = getView().findViewById(R.id.calculate_expend_recyclerview);
        expend_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        calculateExpendAdapter = new CalculateExpendAdapter(expendItemRealmResults, true);
        expend_recyclerview.setAdapter(calculateExpendAdapter);
        expend_recyclerview.addOnItemTouchListener(mScrollTouchListener);
        expend_recyclerview.setNestedScrollingEnabled(false);

        if(expendItemRealmResults.size() == 0 && incomeItemRealmResults.size() == 0){
            alert_imageview.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
        }else{
            alert_imageview.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        }

        sum = 0;
        expend = "";
        income = "";
        income_cost = 0;
        expend_cost = 0;

        for (ExpendItem i : expendItemRealmResults) {
            expend += i.getTitle() + "      -" + toNumFormat(i.getCost()) + "\n";
            sum -= i.getCost();
            expend_cost += i.getCost();
        }

        for (IncomeItem i : incomeItemRealmResults) {
            income += "\n" + i.getTitle() + "      +" + toNumFormat(i.getCost());
            sum += i.getCost();
            income_cost += i.getCost();
        }

        // 수입/ 지출/ 합계 textview reset
        income_text.setText(toNumFormat(income_cost));
        expend_text.setText("-"+toNumFormat(expend_cost));

        result_text.setText(toNumFormat(sum));
    }

    // 내부 리사이클러뷰 잘 돌아가게
    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };
}
