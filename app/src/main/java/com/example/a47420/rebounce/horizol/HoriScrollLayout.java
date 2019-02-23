package com.example.a47420.rebounce.horizol;

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
public class HoriScrollLayout extends LinearLayout {
    private static final String TAG = "RVScrollLayout";
    private static final int MAX_BOUNCE_TOP = 120;//最大的弹出距离
    private static final int MAX_DRAG_TOP = MAX_BOUNCE_TOP*5;//最大的拉出距离

//    private boolean isGetDown;//在down和顶部同时触发时,优先选择手指
//    private int upLength = 0;//在上滑到顶时剩余的高度
    private int MY_SCROLL_TYPE = 0;

    private CubicBezier cubcBezier;

    private interface MyScrollType{
        int LEFT = 0;
        int RIGHT =1;
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
    private int mLastX;
    /**
     * 滚动辅助类
     */
    private Scroller mScroller;

    private boolean disAllow = false;
 
    public HoriScrollLayout(Context context) {
        this(context, null);
    }
 
    public HoriScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        cubcBezier = new CubicBezier(new PointF(0.16f,0.68f),new PointF(0.16f,0.79f));
    }
 
    public HoriScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
//        this.setOrientation(HORIZONTAL);
    }
 
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 1) {
            throw new RuntimeException(HoriScrollLayout.class.getSimpleName() + "只能有一个子控件");
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
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "onTouchEvent: DOWN");
                mStart = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: MOVE  "+ " "+(mLastX - x));
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();  //终止动画
                }

                if (MY_SCROLL_TYPE == MyScrollType.LEFT){
                    if (mLastX - x < 0 ){
                        int scrollLength = Math.abs(mLastX - x) >MAX_DRAG_TOP?MAX_DRAG_TOP:Math.abs(mLastX -x);
                        scrollTo((int) (-scrollLength*0.4+savePreX),0);
                    }else {
                        disAllow =true;
                        scrollTo(0,0);
                    }
                }else {
                    if (mLastX - x > 0){
                        int scrollLength = Math.abs(mLastX - x) >MAX_DRAG_TOP?MAX_DRAG_TOP:Math.abs(mLastX -x);
                        scrollTo((int) (scrollLength*0.4+savePreX),0);
                    }else {
                        disAllow =true;
                        scrollTo(0,0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: UP");
                mEnd = getScrollX();
                int dScrollX = mEnd - mStart;
                mScroller.startScroll(mEnd, 0, -dScrollX, 0, 1000);
                break;
        }
        postInvalidate();
        return true;
    }


    //触发顶部/底部自动回弹
    public void startTBScroll(int leaveX){
        int absX = Math.abs(leaveX);
        float y = (float)(absX>MAX_BOUNCE_TOP?MAX_BOUNCE_TOP:absX)/MAX_BOUNCE_TOP;
        int dx = (int) (MAX_BOUNCE_TOP*cubcBezier.getY(y));
//        dy = dy>80?80:dy;
        Log.i(TAG, "startTBScroll: leftY"+absX);
        Log.i(TAG, "startTBScroll: cb"+cubcBezier.getY(y));
        Log.i(TAG, "startTBScroll: dy"+dx);
        final int finalDy = leaveX > 0 ?dx:-dx;
        checkStartAni(finalDy);
    }

    private int preX;
    private int savePreX;//用于存储preX的值
    private ValueAnimator animatorStart;
    private boolean isCancel;
    private void checkStartAni(final int finalDy) {
        animatorStart = ValueAnimator.ofFloat(0,1,0);
        animatorStart.setDuration(600);
        animatorStart.setInterpolator(new RecorInterpolator(0.29f,0.8f,0.64f,0.19f));
        animatorStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i(TAG, "onAnimationUpdate: "+animation.getAnimatedValue());
                int value = (int)(finalDy*(float)animation.getAnimatedValue());
                mScroller.startScroll(preX,0,-value, 0);
                preX = value;
                postInvalidate();
            }
        });
        animatorStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isCancel =false;
                preX = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "onAnimationEnd: ");
                if (!isCancel){
                    preX = 0;
                    mScroller.startScroll(0,0,0, 0);
                    postInvalidate();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i(TAG, "onAnimationCancel: "+preX);
                isCancel = true;
                mScroller.startScroll(preX,0,0,0);
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
        int x = (int) ev.getX();
        Log.i(TAG, "相对于组件滑过的距离==getY()：" + x);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                savePreX = preX;
                mLastX = x;
                preX = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 下面两个判断来自于 BGARefreshLayout 框架中的判断，github 上搜索 BGARefreshLayout
                 */
                if (convertView instanceof RecyclerView) {
                    Log.i(TAG, "onInterceptTouchEvent: ");
                    if (x - mLastX > 0) {
                        if (Util.isRecyclerViewToLeft(recyclerView)) {
                            MY_SCROLL_TYPE = MyScrollType.LEFT;
                            Log.i(TAG, "滑倒左边时事件拦截成功");
                            return true;
                        }
                    }

                    if (x - mLastX < 0) {
                        Log.i(TAG, "onInterceptTouchEvent: bottom");
                        if (Util.isRecyclerViewToRight(recyclerView)) {
                            MY_SCROLL_TYPE = MyScrollType.RIGHT;
                            Log.i(TAG, "滑倒右边时事件拦截成功");
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
            scrollTo(mScroller.getCurrX(),0);
            postInvalidate();
        }
    }
}