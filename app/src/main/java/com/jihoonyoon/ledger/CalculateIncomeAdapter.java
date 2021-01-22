package com.jihoonyoon.ledger;

import android.annotation.SuppressLint;
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

public class CalculateIncomeAdapter extends RealmRecyclerViewAdapter<IncomeItem, CalculateIncomeAdapter.ItemViewHolder> {

    Realm realm;
    int date;

    public CalculateIncomeAdapter(@Nullable OrderedRealmCollection<IncomeItem> data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @NonNull
    @Override
    public CalculateIncomeAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calculate_item, parent, false));
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CalculateIncomeAdapter.ItemViewHolder holder, int position) {

        IncomeItem incomeItem = getItem(position);

        holder.cal_item_title.setText(incomeItem.getTitle());
        holder.cal_item_cost.setText("+"+CalculateFragment.toNumFormat(incomeItem.getCost()));

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
