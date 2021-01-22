package com.jihoonyoon.ledger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ledger.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class MainClassIncomeAdapter extends RealmRecyclerViewAdapter<MainClass_income, MainClassIncomeAdapter.ItemViewHolder> {

    private Realm realm;
    Context context;
    MainClass_income mainClass;
    private String TAG = "***MainClassIncomeAdapter";
    private List<SubIncomeItem> subIncomeItemArrayList;
    int date;

    public MainClassIncomeAdapter(RealmResults<MainClass_income> realmResults, boolean autoUpdate, Context context, int date) {
        super(realmResults, true);
        this.context = context;
        this.date = date;
    }

    @NonNull
    @Override
    public MainClassIncomeAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Realm.init(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mainclass_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainClassIncomeAdapter.ItemViewHolder holder, int position) {

        mainClass = getItem(position);
        final String title = mainClass.getTitle();

        realm = Realm.getDefaultInstance();
        RealmResults<SubClass_income> realmResults = realm.where(SubClass_income.class).equalTo("main_title", title).findAll();
        Log.d("**MainClassAdater log", realmResults.toString());
        final SubClassIncomeAdapter subClassincomeAdapter = new SubClassIncomeAdapter(realmResults, true, title, context);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(subClassincomeAdapter);
        //holder.recyclerView.addOnItemTouchListener(mScrollTouchListener);

        holder.item_title_text.setText(mainClass.getTitle());

        // 소분류 추가
        holder.subitem_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivity.class);
                intent.putExtra("subclass", false);
                intent.putExtra("mainclass", title);
                v.getContext().startActivity(intent);
            }
        });

        // 대분류와 하위 분류 제거
        holder.item_remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("삭제하시겠습니까?").setMessage("모든 하위 기록들도 함께 삭제됩니다.");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<MainClass_income> results = realm.where(MainClass_income.class).equalTo("title", title).findAll();
                                        results.deleteAllFromRealm();
                                        RealmResults<SubClass_income> results1 = realm.where(SubClass_income.class).equalTo("main_title", title).findAll();
                                        for(SubClass_income i : results1){
                                            RealmResults<IncomeItem> results2 = realm.where(IncomeItem.class).equalTo("title", i.getSub_title()).findAll();
                                            results2.deleteAllFromRealm();
                                        }
                                        results1.deleteAllFromRealm();
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

        // subclassadapter에서 textwatcher 받아서 전체 저장
        holder.item_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                subIncomeItemArrayList = subClassincomeAdapter.pass_list();
                for (final SubIncomeItem i : subIncomeItemArrayList) {
                    final IncomeItem incomeItem = realm.where(IncomeItem.class).equalTo("title", i.sub_income_title).equalTo("date", date).findFirst();
                    if(i.sub_income_cost != 0){
                        if (incomeItem==null){
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    IncomeItem incomeItem1 = realm.createObject(IncomeItem.class);
                                    incomeItem1.setTitle(i.sub_income_title);
                                    incomeItem1.setCost(i.sub_income_cost);
                                    incomeItem1.setDate(date);
                                }
                            });

                        }else{
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    IncomeItem item = realm.where(IncomeItem.class).equalTo("title", i.sub_income_title).equalTo("date", date).findFirst();
                                    item.setCost(i.sub_income_cost);

                                }
                            });

                        }Toast.makeText(v.getContext(), "저장완료!", Toast.LENGTH_SHORT).show();
                    }else{
                        if (incomeItem!=null){
                            realm = Realm.getDefaultInstance();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    IncomeItem item = realm.where(IncomeItem.class).equalTo("title", i.sub_income_title).equalTo("date", date).findFirst();
                                    item.deleteFromRealm();

                                }
                            });
                        }
                    }
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        realm = Realm.getDefaultInstance();
        if (realm.where(MainClass_income.class).findAll() == null) {
            return 0;
        } else {
            return realm.where(MainClass_income.class).findAll().size();
        }

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView item_title_text;
        Button item_remove_btn, subitem_add_btn, item_save_btn;
        RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            subitem_add_btn = itemView.findViewById(R.id.mainclass_subitem_add_btn);
            item_title_text = itemView.findViewById(R.id.mainclass_item_title_text);
            item_remove_btn = itemView.findViewById(R.id.mainclass_item_remove_btn);
            recyclerView = itemView.findViewById(R.id.mainclass_item_recyclerview);
            item_save_btn = itemView.findViewById(R.id.mainclass_item_save_btn);
        }
    }

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
