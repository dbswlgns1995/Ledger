package com.jihoonyoon.ledger.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jihoonyoon.ledger.CalculateFragment;
import com.example.ledger.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    // 오늘 정산

    TextView date_text;
    FrameLayout frameLayout;
    CalculateFragment calculateFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // 오늘 날짜 구하기
        Date today = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");

        Calendar calendar = Calendar.getInstance();
        String dow_str = day_of_week(calendar.get(Calendar.DAY_OF_WEEK));
        final String date_str = dateFormat.format(today) + " " + dow_str + "요일";
        final String intent_str = dateFormat1.format(today);

        calculateFragment = new CalculateFragment(Integer.parseInt(dateFormat1.format(today)));
        // calculate fragment 띄우기
        getFragmentManager().beginTransaction().add(R.id.home_framelayout, calculateFragment).commit();


        return root;
    }


    //요일 계산
    public static String day_of_week(int num) {
        String day_of_week_arr[] = {"일", "월", "화", "수", "목", "금", "토"};
        return day_of_week_arr[num - 1];
    }



}