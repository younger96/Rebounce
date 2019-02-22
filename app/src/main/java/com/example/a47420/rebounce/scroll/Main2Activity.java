package com.example.a47420.rebounce.scroll;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a47420.rebounce.R;

import java.util.ArrayList;

/**
 * 2019/2/21
 * from 陈秋阳
 * 功能描述：
 */
public class Main2Activity extends Activity {
    private RecyclerView interceptRecyclerView;
    private ArrayList<String> data;
    private RVAdapter adapter;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initData();
        initView();
    }




    private void initData() {
        data = new ArrayList<String>();
        for (int i = 0; i <100; i++) {
            data.add("测试"+i);
        }
    }

    private void initView() {
        interceptRecyclerView = findViewById(R.id.rv_custom_list);
        interceptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter();
        interceptRecyclerView.setAdapter(adapter);
    }


    public class RVAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
             RVAdapter.MyHolderView holder = new  RVAdapter.MyHolderView(LayoutInflater.from(Main2Activity.this).inflate(R.layout.item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof  RVAdapter.MyHolderView){
                (( RVAdapter.MyHolderView) holder).tvData.setText(data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        private class MyHolderView extends RecyclerView.ViewHolder{
            TextView tvData;
            public MyHolderView(View itemView) {
                super(itemView);
                tvData = (TextView) itemView.findViewById(R.id.tv_data);
            }
        }
    }
}
