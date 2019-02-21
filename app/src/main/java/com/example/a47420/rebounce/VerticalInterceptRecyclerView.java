package com.example.a47420.rebounce;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class VerticalInterceptRecyclerView extends RecyclerView {
    private static final String TAG = "VerticalInterceptRecycl";

    OnVerticalScrollListener onVerticalScrollListener;
    public VerticalInterceptRecyclerView(Context context) {
        this(context,null);
    }

    public VerticalInterceptRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onVerticalScrollListener = new OnVerticalScrollListener();
//        this.setOnScrollListener(onVerticalScrollListener);

    }

    float downX, downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canScrollVertically(-1)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                downX = ev.getX();
                downY = ev.getY();
            case MotionEvent.ACTION_MOVE:
                if (!canScrollVertically(-1)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (isHorizontalMoved(ev.getX() - downX, ev.getY() - downY)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isHorizontalMoved(float offsetX, float offsetY) {
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        return Math.abs(offsetY) < touchSlop * 5 && Math.abs(offsetX) > touchSlop * 8;
    }
}