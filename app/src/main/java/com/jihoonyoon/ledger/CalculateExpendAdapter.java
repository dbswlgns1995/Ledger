package com.jihoonyoon.ledger;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledger.R;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class CalculateExpendAdapter extends RealmRecyclerViewAdapter<ExpendItem, CalculateExpendAdapter.ItemViewHolder> {

    Realm realm;
    int count;

    public CalculateExpendAdapter(@Nullable OrderedRealmCollection<ExpendItem> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @NonNull
    @Override
    public CalculateExpendAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calculate_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull CalculateExpendAdapter.ItemViewHolder holder, int position) {

        ExpendItem expendItem = getItem(position);

        holder.cal_item_title.setText(expendItem.getTitle());
        holder.cal_item_cost.setText("-"+CalculateFragment.toNumFormat(expendItem.getCost()));
        holder.cal_item_cost.setTextColor(Color.RED);

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView cal_item_title, cal_item_cost;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cal_item_title  = itemView.findViewById(R.id.cal_item_title);
            cal_item_cost = itemView.findViewById(R.id.cal_item_cost);
        }
    }
}
