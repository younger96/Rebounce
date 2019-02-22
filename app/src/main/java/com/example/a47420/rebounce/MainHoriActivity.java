package com.example.a47420.rebounce;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a47420.rebounce.horizol.HoriScrollLayout;
import com.example.a47420.rebounce.horizol.HorizontalInterceptRecyclerView;
import com.example.a47420.rebounce.vertical.RVScrollLayout;
import com.example.a47420.rebounce.vertical.VerticalInterceptRecyclerView;

import java.util.ArrayList;

/**
 * 2019/2/22
 * from 陈秋阳
 * 功能描述：
 */
public class MainHoriActivity extends AppCompatActivity {
    private HorizontalInterceptRecyclerView interceptRecyclerView;
    private ArrayList<String> data;
    private RVAdapter adapter;
    private HoriScrollLayout rvScrollLayout;

    private static final String TAG = "MainHoriActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hori);
        initData();
        initView();


        interceptRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.i(TAG, "onScrolledUP: " + dx);
                if (((!recyclerView.canScrollHorizontally(-1)) || !recyclerView.canScrollHorizontally(1)) && interceptRecyclerView.isUp()) {//(不能向上滑动||不能向下滑动) && 放手了 触发
                    rvScrollLayout.startTBScroll(dx);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }


    private void initData() {
        data = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            data.add("测试" + i);
        }
    }

    private void initView() {
        rvScrollLayout = findViewById(R.id.rv_scroll);
        interceptRecyclerView = findViewById(R.id.rv_custom_list);
        interceptRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new RVAdapter();
        interceptRecyclerView.setAdapter(adapter);
    }


    public class RVAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolderView holder = new MyHolderView(LayoutInflater.from(MainHoriActivity.this).inflate(R.layout.item_horizon, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolderView) {
                ((MyHolderView) holder).tvData.setText(data.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class MyHolderView extends RecyclerView.ViewHolder {
            TextView tvData;

            public MyHolderView(View itemView) {
                super(itemView);
                tvData = (TextView) itemView.findViewById(R.id.tv_data);
            }
        }
    }
}

