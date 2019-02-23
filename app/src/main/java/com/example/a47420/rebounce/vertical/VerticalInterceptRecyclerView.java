package com.example.a47420.rebounce.vertical;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class VerticalInterceptRecyclerView extends RecyclerView {
    private static final String TAG = "VerticalInterceptRecycl";
    private boolean isUp;

    public boolean isUp() {
        return isUp;
    }

    OnVerticalScrollListener onVerticalScrollListener;
    public VerticalInterceptRecyclerView(Context context) {
        this(context,null);
    }

    public VerticalInterceptRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onVerticalScrollListener = new OnVerticalScrollListener();
    }

    float downX, downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, "dispatchTouchEvent: test"+canScrollVertically(1));
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isUp = false;
                if (canScrollVertically(-1) || canScrollVertically(1)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            case MotionEvent.ACTION_MOVE:
                if (!canScrollVertically(-1) || !canScrollVertically(1)) {
                    Log.i(TAG, "dispatchTouchEvent: move false");
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    Log.i(TAG, "dispatchTouchEvent: move true");
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}