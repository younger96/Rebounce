package com.example.a47420.rebounce.vertical;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.a47420.rebounce.Util;
import com.example.a47420.rebounce.cubic.CubicBezier;
import com.example.a47420.rebounce.cubic.RecorInterpolator;

/**
 * Created by Sick on 2016/8/8.
 * 自定义可滚动组件的弹性容器，仿IOS回弹效果
 */
public class RVScrollLayout extends LinearLayout {
    private static final String TAG = "RVScrollLayout";
    private static final int MAX_BOUNCE_TOP = 120;//最大的弹出距离
    private static final int MAX_DRAG_TOP = MAX_BOUNCE_TOP*5;//最大的拉出距离

    private int MY_SCROLL_TYPE = 0;

    private CubicBezier cubcBezier;

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
    }
 
    public RVScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cubcBezier = new CubicBezier(new PointF(0.16f,0.68f),new PointF(0.16f,0.79f));
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
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            Log.i(TAG, "dispatchTouchEvent: down");
            if (animatorStart != null){
                animatorStart.cancel();
            }
        }else if (ev.getAction() == MotionEvent.ACTION_MOVE && disAllow){
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
                mStart = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: MOVE  "+ " "+(mLastY - y));
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();  //终止动画
                }

                if (MY_SCROLL_TYPE == MyScrollType.TOP){
                    if (mLastY - y < 0 ){
                        int scrollLength = Math.abs(mLastY - y) >MAX_DRAG_TOP?MAX_DRAG_TOP:Math.abs(mLastY-y);
                        scrollTo(0, (int) (-scrollLength * 0.4 + savePreY));
                    }else {
                        Log.i(TAG, "onTouchEvent: MOVE disallow");
                       disAllow =true;
                        scrollTo(0,0);
                    }
                }else {
                    if (mLastY - y > 0){
                        int scrollLength = Math.abs(mLastY - y) >800?800:Math.abs(mLastY-y);
                        scrollTo(0, (int) (scrollLength * 0.4+savePreY));
                    }else {
                        Log.i(TAG, "onTouchEvent: MOVE disallow");
                    disAllow =true;
                        scrollTo(0,0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
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
    public void startTBScroll(int leaveY){
        int absY = Math.abs(leaveY);
        float x = (float)(absY>MAX_BOUNCE_TOP?MAX_BOUNCE_TOP:absY)/MAX_BOUNCE_TOP;
        int dy = (int) (MAX_BOUNCE_TOP*cubcBezier.getY(x));
        final int finalDy = leaveY > 0 ?dy:-dy;
        checkStartAni(finalDy);
    }

    private int preY;
    private int savePreY;//用于存储preX的值
    private ValueAnimator animatorStart;
    private boolean isCancel;
    private void checkStartAni(final int finalDy) {
        animatorStart = ValueAnimator.ofFloat(0,1,0);
        animatorStart.setDuration(600);
        animatorStart.setInterpolator(new RecorInterpolator(0.3f,0.82f,0.7f,0.18f));
        animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "onAnimationUpdate: "+animation.getAnimatedValue());
                int value = (int)(finalDy*(float)animation.getAnimatedValue());
                mScroller.startScroll(0,preY,0, -value);
                preY = value;
                postInvalidate();
            }
        });
        animatorStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isCancel =false;
                preY = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: ");
                if (!isCancel){
                    preY = 0;
                    mScroller.startScroll(0,0,0, 0);
                    postInvalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i(TAG, "onAnimationCancel: "+ preY);
                isCancel = true;
                mScroller.startScroll(0,preY,0,0);
                postInvalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorStart.start();
    }

 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getY();
        Log.i(TAG, "相对于组件滑过的距离==getY()：" + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                savePreY = preY;
                mLastY = y;
                preY = 0;
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