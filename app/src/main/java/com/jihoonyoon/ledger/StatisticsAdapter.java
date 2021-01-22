package com.jihoonyoon.ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledger.R;

import java.util.ArrayList;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private ArrayList<StatisticsItem> arrayList;

    public StatisticsAdapter(ArrayList<StatisticsItem> arrayList) {
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public StatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calculate_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.ViewHolder holder, int position) {
        StatisticsItem statisticsItem = arrayList.get(position);
        if(statisticsItem != null){
            holder.title_text.setText(statisticsItem.getTitle());
            holder.cost_text.setText(CalculateFragment.toNumFormat(statisticsItem.getCost())+"");
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title_text, cost_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title_text = itemView.findViewById(R.id.cal_item_title);
            cost_text = itemView.findViewById(R.id.cal_item_cost);
        }
    }
}
