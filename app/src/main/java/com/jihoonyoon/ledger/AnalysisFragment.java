package com.jihoonyoon.ledger;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ledger.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.whiteelephant.monthpicker.MonthPickerDialog;


import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class AnalysisFragment extends Fragment {


    private String TAG = "***analysisfragment";

    Realm realm;

    MultiStateToggleButton date_btn, ie_btn;
    int chart_year, chart_month;
    BarChart chart;

    ArrayList<BarEntry> ylist;
    ArrayList<String> xlist, xyearlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);

        Calendar cal = Calendar.getInstance();
        chart_year = cal.get(cal.YEAR);
        chart_month = cal.get(cal.MONTH);

        chart = root.findViewById(R.id.analysis_barchart);

        Realm.init(root.getContext());
        realm = Realm.getDefaultInstance();

        // 수입, 수출 메인 타이틀만
        RealmResults<MainClass_income> incomeItemRealmResults = realm.where(MainClass_income.class).distinct("title").findAll();
        RealmResults<MainClass_expend> expendItemRealmResults = realm.where(MainClass_expend.class).distinct("title").findAll();

        date_btn = root.findViewById(R.id.analysis_date_btn);
        ie_btn = root.findViewById(R.id.analysis_ie_btn);

        date_btn.setValue(0);
        date_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value) {
                    case 0:
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth + 1) + "월 선택");
                                chart_month = selectedMonth;
                                chart_year = selectedYear;
                                //차트보여주기

                            }
                        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                        builder.setActivatedMonth(chart_month)
                                .setMinYear(2000)
                                .setActivatedYear(chart_year)
                                .setMaxYear(2040)
                                .setMinMonth(Calendar.JANUARY)
                                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                                .build()
                                .show();
                        showChart(0, ie_btn.getValue());
                        break;
                    case 1:
                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                chart_month = selectedMonth;
                                chart_year = selectedYear;
                                // 차트 보여주기

                            }
                        }, chart_year, 0);

                        year_builder.showYearOnly()
                                .setYearRange(2000, 2040)
                                .build()
                                .show();
                        showChart(1, ie_btn.getValue());
                        break;
                }
            }
        });

        ie_btn.setValue(0);
        ie_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value) {
                    case 0:
                        showChart(date_btn.getValue(), 0);
                        break;
                    case 1:
                        showChart(date_btn.getValue(), 1);
                        break;
                    case 2:
                        showChart(date_btn.getValue(), 2);
                        break;
                }
            }
        });

        showChart(date_btn.getValue(), ie_btn.getValue());

        return root;
    }

    public void showChart(int my, int ie) {
        if (my == 0 && ie == 0) {
            Log.d(TAG, "월간 소득");
            ylist = new ArrayList<>();
            xlist = new ArrayList<>();

            for (int i = 1; i <= 31; i++) {
                String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
                int date = Integer.parseInt(chart_year + "" + month_format + ((i < 10) ? "0" + i : "" + i));
                String date_Str = chart_year + "." + (chart_month + 1) + ((i < 10) ? ".0" + i : "." + i);
                // 일간 소득
                int cost = realm.where(IncomeItem.class).equalTo("date", date).sum("cost").intValue() - realm.where(ExpendItem.class).equalTo("date", date).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xlist.add(date_Str);

            }
            drawChart(xlist, ylist, chart_year + "년 " + (chart_month + 1)+"월" , 0);

        } else if (my == 0 && ie == 1) {
            Log.d(TAG, "월간 수입");
            ylist = new ArrayList<>();
            xlist = new ArrayList<>();
            for (int i = 1; i <= 31; i++) {
                String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
                int date = Integer.parseInt(chart_year + "" + month_format + ((i < 10) ? "0" + i : "" + i));
                String date_Str = chart_year + "." + (chart_month + 1) + ((i < 10) ? ".0" + i : "." + i);
                // 일간 소득
                int cost = realm.where(IncomeItem.class).equalTo("date", date).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xlist.add(date_Str);

            }
            drawChart(xlist, ylist, chart_year + "년 " + (chart_month + 1)+"월", 1);

        } else if (my == 0 && ie == 2) {
            Log.d(TAG, "월간 지출");
            ylist = new ArrayList<>();
            xlist = new ArrayList<>();
            for (int i = 1; i <= 31; i++) {
                String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
                int date = Integer.parseInt(chart_year + "" + month_format + ((i < 10) ? "0" + i : "" + i));
                String date_Str = chart_year + "." + (chart_month + 1) + ((i < 10) ? ".0" + i : "." + i);
                // 일간 지출
                int cost = realm.where(ExpendItem.class).equalTo("date", date).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xlist.add(date_Str);
            }
            drawChart(xlist, ylist, chart_year + "년 " + (chart_month + 1)+"월", 2);

        } else if (my == 1 && ie == 0) {
            Log.d(TAG, "년간 소득");
            ylist = new ArrayList<>();
            xyearlist = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                int date_from = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                int date_to = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                String date_Str = chart_year + "." + i;
                // 일간 지출
                int cost = realm.where(IncomeItem.class).between("date", date_from, date_to).sum("cost").intValue() - realm.where(ExpendItem.class).between("date", date_from, date_to).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xyearlist.add(date_Str);
            }
            drawChart(xlist, ylist, chart_year + "년", 0);

        } else if (my == 1 && ie == 1) {
            Log.d(TAG, "년간 수입");
            ylist = new ArrayList<>();
            xyearlist = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                int date_from = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                int date_to = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                String date_Str = chart_year + "." + i;
                // 일간 지출
                int cost = realm.where(IncomeItem.class).between("date", date_from, date_to).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xyearlist.add(date_Str);
            }
            drawChart(xlist, ylist, chart_year + "년", 1);

        } else if (my == 1 && ie == 2) {
            Log.d(TAG, "년간 지출");
            ylist = new ArrayList<>();
            xyearlist = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                int date_from = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "0" + 1);
                int date_to = Integer.parseInt(chart_year + "" + ((i < 10) ? "0" + i : "" + i) + "" + 31);
                String date_Str = chart_year + "." + i;
                // 일간 지출
                int cost = realm.where(ExpendItem.class).between("date", date_from, date_to).sum("cost").intValue();
                ylist.add(new BarEntry(i, cost));
                xyearlist.add(date_Str);

            }
            drawChart(xlist, ylist, chart_year + "년", 2);

        }
    }

    public void drawChart(final ArrayList<String> x, ArrayList<BarEntry> y, String label, int color) {

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setStartAtZero(false);

        /*
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

         */

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        BarDataSet barDataSet = new BarDataSet(y, label);
        switch (color){
            case 0:
                barDataSet.setColor(R.color.royalblue);
                break;
            case 1:
                barDataSet.setColor(Color.BLUE);
                break;
            case 2:
                barDataSet.setColor(Color.RED);
                break;
        }

        BarData data = new BarData(barDataSet);
        chart.animateY(1000);
        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate();


    }

}