package com.jihoonyoon.ledger;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ledger.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class StatisticsFragment extends Fragment {

    // 통계 fragment

    public StatisticsFragment() {
    }

    Realm realm;

    private String TAG = "***StatisticsFragment";
    MultiStateToggleButton dmy_btn, ie_btn, classify_btn;

    int chart_year, chart_month, chart_day;

    PieChart chart;

    RecyclerView recyclerView;
    StatisticsAdapter statisticsAdapter;
    ArrayList<StatisticsItem> arrayList;
    LinearLayout linearLayout;
    CardView c1, c2;

    // 사용 api
    //https://github.com/premkumarroyal/MonthAndYearPicker datepicker
    //https://github.com/jlhonora/multistatetogglebutton#colors multi toggle
    //https://github.com/PhilJay/MPAndroidChart mp chart

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        //현재 년도, 월, 일
        Calendar cal = Calendar.getInstance();
        chart_year = cal.get ( cal.YEAR );
        chart_month = cal.get ( cal.MONTH );
        chart_day = cal.get ( cal.DATE ) ;

        Realm.init(root.getContext());


        chart = root.findViewById(R.id.statistics_piechart);
        linearLayout = root.findViewById(R.id.statistics_linearlayout);
        c1 = root.findViewById(R.id.statistiscs_card1);
        c2 = root.findViewById(R.id.statistiscs_card2);

        recyclerView = root.findViewById(R.id.statistics_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 일간 월간 년간 토글 버튼
        dmy_btn = root.findViewById(R.id.statistics_dmy_btn);
        dmy_btn.setValue(0);
        dmy_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0: //day picker
                        DatePickerDialog dialogFragment = new DatePickerDialog(getContext(),dateListener, chart_year, chart_month, chart_day);
                        dialogFragment.show();
                        break;

                    case 1: // month picker
                        final Calendar today = Calendar.getInstance();
                        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // month 를 선택했을때
                                Log.d(TAG, selectedYear + "년 " + (selectedMonth+1) + "월 선택");
                                chart_month = selectedMonth;
                                chart_year = selectedYear;
                                showChart(1, ie_btn.getValue());

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

                        break;

                    case 2: // year picker

                        MonthPickerDialog.Builder year_builder = new MonthPickerDialog.Builder(getContext(), new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // year 를 선택했을때
                                Log.d(TAG, selectedYear + "년 선택");
                                chart_month = selectedMonth;
                                chart_year = selectedYear;
                                showChart(2, ie_btn.getValue());

                            }
                        }, chart_year, 0);

                        year_builder.showYearOnly()
                                .setYearRange(2000, 2040)
                                .build()
                                .show();
                        break;


                }
            }
        });

        // 수입 지출 토글 버튼
        ie_btn = root.findViewById(R.id.statistics_ie_btn);
        ie_btn.setValue(0);
        ie_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0: //income
                        showChart(dmy_btn.getValue(), 0);
                        break;
                    case 1: //excome
                        showChart(dmy_btn.getValue(), 1);
                        break;


                }
            }
        });

        // 대분류 소분류 토글 버튼
        classify_btn = root.findViewById(R.id.statistics_classify_btn);
        classify_btn.setValue(0);
        classify_btn.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                switch (value){
                    case 0: //소분류
                        break;
                    case 1: //대분류
                        break;


                }
            }
        });

        showChart(dmy_btn.getValue(), ie_btn.getValue());

        return root;
    }

    // 일간 차트
    private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Log.d(TAG, year + "" + monthOfYear+ ""+ dayOfMonth + "선택");
            chart_year = year;
            chart_month = monthOfYear;
            chart_day = dayOfMonth;
            showChart(0, ie_btn.getValue());
        }
    };

    public void showChart(int dmy, int ie){ // dmy -> 0 = day, 1 = month, 2 = year / ie -> 0 = income, 1 = expend;
        if(dmy==0 & ie==0){
            Log.d(TAG, "일간 수입");
            realm = Realm.getDefaultInstance();
            String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
            String day_format = chart_day < 10 ? "0" + chart_day : ""+chart_day;
            String date_format = chart_year + month_format + day_format;
            int date = Integer.parseInt(date_format);
            long count =  realm.where(IncomeItem.class).equalTo("date", date).count();
            int center_cost = 0;
            center_cost += realm.where(IncomeItem.class).equalTo("date", date).sum("cost").intValue();
            String center_text = chart_year+"년 "+(chart_month+1)+"월 "+chart_day +"일 수입"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, date + "해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).equalTo("date", date).findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(IncomeItem i : incomeItemRealmResults){
                    pieEntries.add(new PieEntry(i.getCost(), i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), i.getCost()));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);

            }


        }else if(dmy == 0 && ie ==1){
            Log.d(TAG, "일간 지출");
            realm = Realm.getDefaultInstance();
            String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
            String day_format = chart_day < 10 ? "0" + chart_day : ""+chart_day;
            String date_format = chart_year + month_format + day_format;
            int date = Integer.parseInt(date_format);
            long count =  realm.where(ExpendItem.class).equalTo("date", date).count();
            int center_cost = 0;
            center_cost += realm.where(ExpendItem.class).equalTo("date", date).sum("cost").intValue();
            String center_text = chart_year+"년 "+(chart_month+1)+"월 "+chart_day +"일 지출"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, date + "해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).equalTo("date", date).sort("cost", Sort.DESCENDING).findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(ExpendItem i : expendItemRealmResults){
                    pieEntries.add(new PieEntry(i.getCost(), i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), i.getCost()));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);
            }

        }else if(dmy==1 & ie==0){
            Log.d(TAG, "월간 수입");
            realm = Realm.getDefaultInstance();
            String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
            int datefrom = Integer.parseInt(chart_year+""+month_format+"0"+1);
            int dateto = Integer.parseInt(chart_year+""+month_format+""+31);

            long count =  realm.where(IncomeItem.class).between("date", datefrom, dateto).count();
            int center_cost = 0;
            center_cost += realm.where(IncomeItem.class).between("date", datefrom, dateto).sum("cost").intValue();
            String center_text = chart_year+"년 "+(chart_month+1)+"월 수입"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, datefrom + " ~ " + dateto + " 해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).between("date", datefrom, dateto).distinct("title").findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(IncomeItem i : incomeItemRealmResults){
                    int sum = realm.where(IncomeItem.class).between("date", datefrom, dateto).equalTo("title",i.getTitle()).sum("cost").intValue();

                    pieEntries.add(new PieEntry(sum, i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), sum));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);
            }

        }else if(dmy == 1 && ie ==1){
            Log.d(TAG, "월간 지출");
            realm = Realm.getDefaultInstance();
            String month_format = (chart_month+1) < 10 ? "0" + (chart_month+1) : ""+(chart_month+1);
            int datefrom = Integer.parseInt(chart_year+""+month_format+"0"+1);
            int dateto = Integer.parseInt(chart_year+""+month_format+""+31);
            long count =  realm.where(ExpendItem.class).between("date", datefrom, dateto).count();
            int center_cost = 0;
            center_cost += realm.where(ExpendItem.class).between("date", datefrom, dateto).sum("cost").intValue();
            String center_text = chart_year+"년 "+(chart_month+1)+"월 지출"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, datefrom + " ~ " + dateto + " 해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).between("date", datefrom, dateto).distinct("title").findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(ExpendItem i : expendItemRealmResults){
                    int sum = realm.where(ExpendItem.class).between("date", datefrom, dateto).equalTo("title",i.getTitle()).sum("cost").intValue();

                    pieEntries.add(new PieEntry(sum, i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), sum));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);
            }

        }else if(dmy==2 & ie==0){
            Log.d(TAG, "연간 수입");
            realm = Realm.getDefaultInstance();
            int datefrom = Integer.parseInt(chart_year+"0"+1+"0"+1);
            int dateto = Integer.parseInt(chart_year+""+12+""+31);
            long count =  realm.where(IncomeItem.class).between("date", datefrom, dateto).count();
            int center_cost = 0;
            center_cost += realm.where(IncomeItem.class).between("date", datefrom, dateto).sum("cost").intValue();
            String center_text = chart_year+"년 수입"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, datefrom + " ~ " + dateto + " 해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<IncomeItem> incomeItemRealmResults = realm.where(IncomeItem.class).between("date", datefrom, dateto).distinct("title").findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(IncomeItem i : incomeItemRealmResults){
                    int sum = realm.where(IncomeItem.class).between("date", datefrom, dateto).equalTo("title",i.getTitle()).sum("cost").intValue();

                    pieEntries.add(new PieEntry(sum, i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), sum));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);
            }

        }else if(dmy == 2 && ie ==1){
            Log.d(TAG, "연간 지출");
            realm = Realm.getDefaultInstance();
            int datefrom = Integer.parseInt(chart_year+"0"+1+"0"+1);
            int dateto = Integer.parseInt(chart_year+""+12+""+31);
            long count =  realm.where(ExpendItem.class).between("date", datefrom, dateto).count();
            int center_cost = 0;
            center_cost += realm.where(ExpendItem.class).between("date", datefrom, dateto).sum("cost").intValue();
            String center_text = chart_year+"년 지출"
                    +"\n" + center_cost + " 원";
            if(count == 0){
                Log.d(TAG, datefrom + " ~ " + dateto + " 해당 정산내역 없음!");
                Toast.makeText(getContext(), "해당 정산내역 없음!", Toast.LENGTH_SHORT).show();
            }else{
                RealmResults<ExpendItem> expendItemRealmResults = realm.where(ExpendItem.class).between("date", datefrom, dateto).distinct("title").findAll();
                arrayList = new ArrayList<>();

                List<PieEntry> pieEntries = new ArrayList<>();
                for(ExpendItem i : expendItemRealmResults){
                    int sum = realm.where(ExpendItem.class).between("date", datefrom, dateto).equalTo("title",i.getTitle()).sum("cost").intValue();

                    pieEntries.add(new PieEntry(sum, i.getTitle()));
                    arrayList.add(new StatisticsItem(i.getTitle(), sum));
                }

                drawChart(pieEntries, center_text);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                statisticsAdapter = new StatisticsAdapter(arrayList);
                recyclerView.setAdapter(statisticsAdapter);
            }

        }
    }

    public void drawChart(List<PieEntry> pieEntries, String center_text){

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterText(center_text);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(12f);

        PieDataSet dataSet = new PieDataSet(pieEntries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);



        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);



        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());



        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();



    }


    @Override
    public void onStop() {
        realm.close();
        super.onStop();
    }
}