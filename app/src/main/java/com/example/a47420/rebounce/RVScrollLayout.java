package com.example.a47420.rebounce;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.a47420.rebounce.cubic.CubcBezier;

/**
 * Created by Sick on 2016/8/8.
 * 自定义可滚动组件的弹性容器，仿IOS回弹效果
 */
public class RVScrollLayout extends LinearLayout {
    private static final String TAG = "RVScrollLayout";

//    private boolean isGetDown;//在down和顶部同时触发时,优先选择手指
//    private int upLength = 0;//在上滑到顶时剩余的高度
    private int MY_SCROLL_TYPE = 0;

    private CubcBezier cubcBezier;

    private interface MyScrollType{
        int TOP = 0;
        int BUTTOM =1;
    }

    /**
     * 容器中的组件
     */
    private View convertView;
    /**
     * 如果容器中的组件为RecyclerView
     */
    private RecyclerView recyclerView;
    /**
     * 滚动开始
     */
    private int mStart;
    /**
     * 滚动结束
     */
    private int mEnd;
    /**
     * 上一次滑动的坐标
     */
    private int mLastY;
    /**
     * 滚动辅助类
     */
    private Scroller mScroller;

    private boolean disAllow = false;
 
    public RVScrollLayout(Context context) {
        this(context, null);
    }
 
    public RVScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        cubcBezier = new CubcBezier(new PointF(0.08f,0.48f),new PointF(0.33f,1f));
    }
 
    public RVScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
 
    }
 
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new RuntimeException(RVScrollLayout.class.getSimpleName() + "只能有一个子控件");
        }
        convertView = getChildAt(0);
       //TODO 可以拓展ListView等可滑动的组件
        if (convertView instanceof RecyclerView) {
            recyclerView = (RecyclerView) convertView;
        }
    }
 
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            View view = getChildAt(0);
            view.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE && disAllow){
            Log.i(TAG, "dispatchTouchEvent: disallow");
            disAllow =false;
            requestDisallowInterceptTouchEvent(true);
            ev.setAction(MotionEvent.ACTION_DOWN);
            return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.i(TAG, "onTouchEvent: DOWN");
                mStart = getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: MOVE  "+ " "+(mLastY - y));
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();  //终止动画
                }

                if (MY_SCROLL_TYPE == MyScrollType.TOP){
                    if (mLastY - y < 0 ){
                        int scrollLength = Math.abs(mLastY - y) >800?800:Math.abs(mLastY-y);
                        scrollTo(0, (int) (-scrollLength * 0.4));
                    }else {
                        Log.i(TAG, "onTouchEvent: MOVE disallow");
                       disAllow =true;
                        scrollTo(0,0);
//                    requestDisallowInterceptTouchEvent(true);
                    }
                }else {
                    if (mLastY - y > 0){
                        int scrollLength = Math.abs(mLastY - y) >800?800:Math.abs(mLastY-y);
                        scrollTo(0, (int) (scrollLength * 0.4));
                    }else {
                        Log.i(TAG, "onTouchEvent: MOVE disallow");
                    disAllow =true;
                        scrollTo(0,0);
//                    requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: UP");
                mEnd = getScrollY();
                int dScrollY = mEnd - mStart;
                mScroller.startScroll(0, mEnd, 0, -dScrollY, 1000);
                break;
        }
        postInvalidate();
        return true;
    }


    //触发顶部/底部自动回弹
    public void startTBScroll(int leftY){
        int dy = Math.abs(leftY)*8;
        dy = dy>80?80:dy;
        final int finalDy = leftY > 0 ?dy:-dy;
        mScroller.startScroll(0,0,0,finalDy,400);
        postInvalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mScroller.startScroll(0,finalDy,0, -finalDy,400);
                postInvalidate();
            }
        },400);
    }

 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        Log.i(TAG, "相对于组件滑过的距离==getY()：" + y);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onInterceptTouchEvent: "+(y-mLastY));
                /**
                 * 下面两个判断来自于 BGARefreshLayout 框架中的判断，github 上搜索 BGARefreshLayout
                 */
                if (convertView instanceof RecyclerView) {
                    Log.i(TAG, "onInterceptTouchEvent: ");
                    if (y - mLastY > 0) {
                        if (Util.isRecyclerViewToTop(recyclerView)) {
                            MY_SCROLL_TYPE = MyScrollType.TOP;
                            Log.i(TAG, "滑倒顶部时事件拦截成功");
                            return true;
                        }
                    }

                    if (y - mLastY < 0) {
                        Log.i(TAG, "onInterceptTouchEvent: bottom");
                        if (Util.isRecyclerViewToBottom(recyclerView)) {
                            MY_SCROLL_TYPE = MyScrollType.BUTTOM;
                            Log.i(TAG, "滑倒底部时事件拦截成功");
                            return true;
                        }
                    }
                }
                break;
        }
        return false;
    }
 
 
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
//            Log.i(TAG, "computeScroll: "+mScroller.getCurrY());
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }
}