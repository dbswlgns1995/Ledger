package com.jihoonyoon.ledger.ui.dashboard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jihoonyoon.ledger.CalculateFragment;
import com.jihoonyoon.ledger.ExpendItem;
import com.jihoonyoon.ledger.IncomeItem;
import com.example.ledger.R;
import com.jihoonyoon.ledger.MainActivity;
import com.jihoonyoon.ledger.ui.dashboard.decorator.EventDecorator;
import com.jihoonyoon.ledger.ui.dashboard.decorator.SaturdayDecorator;
import com.jihoonyoon.ledger.ui.dashboard.decorator.SundayDecorator;
import com.jihoonyoon.ledger.ui.home.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;


import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmResults;


public class DashboardFragment extends Fragment implements View.OnClickListener, OnDateSelectedListener, OnMonthChangedListener {

    // Material CalendarView - https://github.com/prolificinteractive/material-calendarview

    MaterialCalendarView calendarView;
    private BottomSheetBehavior bottomSheetBehavior;

    ImageView cancel_imageview;
    CalculateFragment calculateFragment;
    FrameLayout calendar_framelayout;

    TextView income_text, expend_text, result_text;

    private String TAG = "***CalendarFragment";

    int income_cost=0, expend_cost=0, result_cost=0;
    Realm realm;
    int date_from, date_to;
    ArrayList<CalendarDay> dates;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        calendarView = root.findViewById(R.id.calendarView);
        View bottomSheet = root.findViewById(R.id.bottom_sheet);
        cancel_imageview = root.findViewById(R.id.calendar_cancel_imageview);
        calendar_framelayout = root.findViewById(R.id.calendar_framelayout);

        income_text = root.findViewById(R.id.calendar_income_text);
        expend_text = root.findViewById(R.id.calendar_expend_text);
        result_text = root.findViewById(R.id.calendar_result_text);

        // bottomSheet(끌수있는) 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // cancel button 숨기기
        cancel_imageview.setVisibility(View.INVISIBLE);

        // 오늘 날짜
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date today = new Date();
        final String today_str = simpleDateFormat.format(today);
        calculateFragment = new CalculateFragment(Integer.parseInt(today_str));
        // bottomsheet에 calculateFragment 붙이기
        getFragmentManager().beginTransaction().add(R.id.calendar_framelayout, calculateFragment).commit();

        Realm.init(root.getContext());
        realm = Realm.getDefaultInstance();

        // 월 수입, 지출, 합계 계산용 realm query
        date_from = Integer.parseInt(today_str.substring(0,6)+"01");
        date_to = Integer.parseInt(today_str.substring(0,6)+"31");

        int realm_income = realm.where(IncomeItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();
        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

        income_cost += realm_income;
        expend_cost += realm_expend;
        result_cost = income_cost - expend_cost;

        income_text.setText(CalculateFragment.toNumFormat(income_cost));
        expend_text.setText(CalculateFragment.toNumFormat(expend_cost));
        result_text.setText(CalculateFragment.toNumFormat(result_cost));
        //



        // Material CalendarView

        calendarView.setSelectedDate(today);
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2010, 0, 1))
                .setMaximumDate(CalendarDay.from(2040, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.addDecorators(new SundayDecorator(),
                new SaturdayDecorator()); // 일요일 빨간색, 토요일 파란색

        // 정산 완료된 날짜 표기용 realm
        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).notEqualTo("cost", 0).findAll();
        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).findAll();

        // 정산 완료 여부 표시하기
        dates = new ArrayList<>();
        for(IncomeItem i :incomeItemRealmResults){
            String date_str = Integer.toString(i.getDate());
            String date_year = date_str.substring(0,4);
            String date_month = date_str.substring(4,6);
            String date_day = date_str.substring(6,8);

            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
            dates.add(day);

            Log.d(TAG, day.toString());

        }

        for(ExpendItem i : expendItemRealmResults){
            String date_str = Integer.toString(i.getDate());
            String date_year = date_str.substring(0,4);
            String date_month = date_str.substring(4,6);
            String date_day = date_str.substring(6,8);

            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
            dates.add(day);
        }

        calendarView.addDecorator(new EventDecorator(dates));
        //

        // x 표시 누르면 onbackpressed
        cancel_imageview.setOnClickListener(this);

        // 확장했을때 cancel button visible 그 외에는 invisible
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        cancel_imageview.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // bottomsheet 내려가면 calendarfragment refresh
                        cancel_imageview.setVisibility(View.INVISIBLE);

                        // 정산 완료된 날짜 표기용 realm
                        RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).notEqualTo("cost", 0).findAll();
                        RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).notEqualTo("cost", 0).findAll();

                        // 정산 완료 여부 표시하기
                        dates = new ArrayList<>();
                        for(IncomeItem e :incomeItemRealmResults){
                            String date_str = Integer.toString(e.getDate());
                            String date_year = date_str.substring(0,4);
                            String date_month = date_str.substring(4,6);
                            String date_day = date_str.substring(6,8);

                            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
                            dates.add(day);

                            Log.d(TAG, day.toString());

                        }

                        for(ExpendItem e : expendItemRealmResults){
                            String date_str = Integer.toString(e.getDate());
                            String date_year = date_str.substring(0,4);
                            String date_month = date_str.substring(4,6);
                            String date_day = date_str.substring(6,8);

                            CalendarDay day = CalendarDay.from(Integer.parseInt(date_year), Integer.parseInt(date_month)-1, Integer.parseInt(date_day));
                            dates.add(day);
                        }
                        calendarView.addDecorator(new EventDecorator(dates));

                        // 월 수입, 지출, 합계 계산용 realm query
                        Log.d(TAG, "바텀시트 내려가면 수입, 지출, 합계 리셋");
                        int realm_income = realm.where(IncomeItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();
                        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

                        income_cost = 0;
                        expend_cost = 0;
                        result_cost = 0;

                        income_cost += realm_income;
                        expend_cost += realm_expend;
                        result_cost = income_cost - expend_cost;

                        income_text.setText(CalculateFragment.toNumFormat(income_cost));
                        expend_text.setText("-"+CalculateFragment.toNumFormat(expend_cost));
                        result_text.setText(CalculateFragment.toNumFormat(result_cost));

                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        cancel_imageview.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                cancel_imageview.setVisibility(View.INVISIBLE);
            }


        });


        return root;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_cancel_imageview:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    // calendarView 날짜 변경 시 bottomsheet 올라오게 하기왜요
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        calendar_framelayout.removeAllViews(); // framelayout 초기화
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        String month_str = (date.getMonth()+1) < 10 ? "0" + (date.getMonth()+1) : ""+ (date.getMonth()+1);
        String day_str = date.getDay() < 10 ? "0" + date.getDay() : ""+ date.getDay();;
        String date_str = date.getYear() +  month_str + day_str;
        Log.d(TAG, date_str);
        calculateFragment = new CalculateFragment(Integer.parseInt(date_str));
        getFragmentManager().beginTransaction().add(R.id.calendar_framelayout, calculateFragment).commit();
    }

    // calendarView 달 변경시 월수입, 지출, 합계 텍스트뷰 변경
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        String month_str = (date.getMonth()+1) < 10 ? "0" + (date.getMonth()+1) : ""+ (date.getMonth()+1);

        date_from = Integer.parseInt(date.getYear()+month_str+"01");
        date_to = Integer.parseInt(date.getYear()+month_str+"31");

        int realm_income = realm.where(IncomeItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();
        int realm_expend = realm.where(ExpendItem.class).notEqualTo("cost", 0).between("date", date_from, date_to).sum("cost").intValue();

        income_cost = 0;
        expend_cost = 0;
        result_cost = 0;

        income_cost += realm_income;
        expend_cost += realm_expend;
        result_cost = income_cost - expend_cost;

        income_text.setText(CalculateFragment.toNumFormat(income_cost));
        expend_text.setText("-"+CalculateFragment.toNumFormat(expend_cost));
        result_text.setText(CalculateFragment.toNumFormat(result_cost));

    }


}