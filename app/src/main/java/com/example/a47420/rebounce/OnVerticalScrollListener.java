package com.example.a47420.rebounce;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class OnVerticalScrollListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "OnVerticalScrollListene";

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (!recyclerView.canScrollVertically(1)) {
            onScrolledToEnd();
        } else if (!recyclerView.canScrollVertically(-1)) {
            onScrolledToTop();
        } else if (dy < 0) {
            onScrolledUp();
        } else if (dy > 0) {
            onScrolledDown();
        }
    }


    public void onScrolledUp() {
        Log.i(TAG, "onScrolledUp: ");

    }

    public void onScrolledDown() {
        Log.i(TAG, "onScrolledDown: ");
    }

    public void onScrolledToEnd() {
        Log.i(TAG, "onScrolledToEnd: ");
    }

    public void onScrolledToTop() {
        Log.i(TAG, "onScrolledToTop: ");
    }
}