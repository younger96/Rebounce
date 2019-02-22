package com.example.a47420.rebounce;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private VerticalInterceptRecyclerView interceptRecyclerView;
    private ArrayList<String> data;
    private RVAdapter adapter;
    private RVScrollLayout rvScrollLayout;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();


        interceptRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolledUP: "+dy);
                if (((!recyclerView.canScrollVertically(-1))||!recyclerView.canScrollVertically(1)) && interceptRecyclerView.isUp()) {//(不能向上滑动||不能向下滑动) && 放手了 触发
                    rvScrollLayout.startTBScroll(dy);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }




    private void initData() {
        data = new ArrayList<String>();
        for (int i = 0; i <150; i++) {
            data.add("测试"+i);
        }
    }

    private void initView() {
        rvScrollLayout = findViewById(R.id.rv_scroll);
        interceptRecyclerView = (VerticalInterceptRecyclerView) findViewById(R.id.rv_custom_list);
        interceptRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter();
        interceptRecyclerView.setAdapter(adapter);
    }


    public class RVAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolderView holder = new MyHolderView(LayoutInflater.from(MainActivity.this).inflate(R.layout.item,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolderView){
                ((MyHolderView) holder).tvData.setText(data.get(position));
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
