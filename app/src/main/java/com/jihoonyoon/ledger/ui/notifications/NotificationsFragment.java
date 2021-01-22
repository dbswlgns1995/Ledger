package com.jihoonyoon.ledger.ui.notifications;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.jihoonyoon.ledger.AnalysisFragment;
import com.example.ledger.R;
import com.jihoonyoon.ledger.StatisticsFragment;

public class NotificationsFragment extends Fragment implements View.OnClickListener,PopupMenu.OnMenuItemClickListener{

    private static String TAG = "***chartfragment";

    TextView chart_text;

    FrameLayout chart_framelayout;
    StatisticsFragment statisticsFragment;
    AnalysisFragment analysisFragment;

    ImageView menu_imageview;

    PopupMenu popupMenu;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        chart_framelayout = root.findViewById(R.id.chart_framelayout);
        chart_text = root.findViewById(R.id.chart_text);
        menu_imageview = root.findViewById(R.id.chart_menu_imageview);

        statisticsFragment = new StatisticsFragment();
        analysisFragment = new AnalysisFragment();
        getFragmentManager().beginTransaction().add(R.id.chart_framelayout, statisticsFragment).commit(); // 기본 통계 fragment

        menu_imageview.setOnClickListener(this);

        // menu 통계 / 소득분석 fragment 가져오기
        popupMenu = new PopupMenu(getContext(), menu_imageview, Gravity.END, R.attr.actionOverflowMenuStyle, 0 );
        popupMenu.getMenu( ).add( 0, 0, 0, "통계" );
        popupMenu.getMenu( ).add( 0, 1, 0, "소득 분석" );
        popupMenu.setOnMenuItemClickListener(this);


        return root;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_option, menu);
        Log.d(TAG, "통계");
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch ( item.getItemId( ) ) {
            case 0:
                //통계 fragment 가져오기
                Log.d(TAG, "통계");
                chart_text.setText("  통계");
                getFragmentManager().beginTransaction().replace(R.id.chart_framelayout, statisticsFragment).commit();
                break;
            case 1:
                //분석 fragment 가져오기
                Log.d(TAG, "소득 분석");
                chart_text.setText("  소득 분석");
                getFragmentManager().beginTransaction().replace(R.id.chart_framelayout, analysisFragment).commit();
                break;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if(v==menu_imageview){
            popupMenu.show();
        }
    }
}