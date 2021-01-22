package com.jihoonyoon.ledger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledger.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

class SubIncomeItem {
    public String sub_income_title;
    public int sub_income_cost;

    public SubIncomeItem(String sub_income_title, int sub_income_cost) {
        this.sub_income_title = sub_income_title;
        this.sub_income_cost = sub_income_cost;
    }
}

public class SubClassIncomeAdapter extends RealmRecyclerViewAdapter<SubClass_income, SubClassIncomeAdapter.ItemViewHolder> {

    private Realm realm;
    String main_title;
    Context context;
    IncomeItem incomeItem;
    String TAG = "****SubClassIncomeAdapter****";
    int cost;

    private List<SubIncomeItem> subIncomeItemArrayList;
    RealmResults<SubClass_income> realmResults;

    public SubClassIncomeAdapter(RealmResults<SubClass_income> realmResults, boolean autoUpdate, String main_title, Context context) {
        super(realmResults, autoUpdate);
        this.main_title = main_title;
        this.context = context;
        this.realmResults = realmResults;
    }

    @NonNull
    @Override
    public SubClassIncomeAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        subIncomeItemArrayList = new ArrayList<SubIncomeItem>(realmResults.size());
        for(SubClass_income i : realmResults){
            subIncomeItemArrayList.add(new SubIncomeItem("", 0));
        }
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subclass_item, parent, false));

    }


    @Override
    public int getItemCount() {
        realm = Realm.getDefaultInstance();
        return realm.where(SubClass_income.class).equalTo("main_title", main_title).findAll().size();

    }

    @Override
    public void onBindViewHolder(@NonNull final SubClassIncomeAdapter.ItemViewHolder holder, final int position) {
        final SubClass_income subClass = getItem(position);
        final String sub_title = subClass.getSub_title();

        // 당일 날자 가져오기
        Intent intent = ((Activity) context).getIntent();
        final int date = intent.getIntExtra("date", 0);

        realm = Realm.getDefaultInstance();
        final IncomeItem realm_income_item = realm.where(IncomeItem.class).equalTo("title", sub_title).equalTo("date", date).findFirst();

        subIncomeItemArrayList.get(position).sub_income_title = sub_title;

        Log.d(TAG, "시발" + subIncomeItemArrayList.size());

        holder.subclass_item_title_text.setText(sub_title);

        if (realm_income_item != null) {
            cost = realm_income_item.getCost();
            holder.subclass_item_edit.setText(String.valueOf(cost));
            subIncomeItemArrayList.get(position).sub_income_cost = cost;

        }

        holder.subclass_item_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("")){
                    subIncomeItemArrayList.get(position).sub_income_cost = Integer.parseInt(s.toString());
                }
                subIncomeItemArrayList.get(position).sub_income_title = sub_title;
                Log.d(TAG, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.subclass_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("삭제하시겠습니까?").setMessage("모든 기록들도 함께 삭제됩니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<SubClass_income> results = realm.where(SubClass_income.class).equalTo("sub_title", sub_title).findAll();
                                        results.deleteAllFromRealm();
                                        RealmResults<IncomeItem> income_results = realm.where(IncomeItem.class).equalTo("title", sub_title).findAll();
                                        income_results.deleteAllFromRealm();
                                    }
                                });

                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();

            }
        });


    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        Button subclass_remove_btn;
        TextView subclass_item_title_text;
        EditText subclass_item_edit;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subclass_remove_btn = itemView.findViewById(R.id.subclass_item_remove_btn);
            subclass_item_title_text = itemView.findViewById(R.id.subclass_item_title_text);
            subclass_item_edit = itemView.findViewById(R.id.subclass_item_edit);

        }
    }

    public List<SubIncomeItem> pass_list(){
        return subIncomeItemArrayList;

    }

}
