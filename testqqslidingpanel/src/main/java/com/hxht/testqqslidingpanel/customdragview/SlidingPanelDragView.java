package com.hxht.testqqslidingpanel.customdragview;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

public class SlidingPanelDragView extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private ViewGroup leftView;
    private ViewGroup mainView;
    private int mWidth;
    private int mHeight;
    private int range;

    public enum Status {
        OPEN,
        CLOSE,
        DRAGING
    }

    public interface OnUpdateStatusListener {
        void onOpen();

        void onClose();

        void onDraging(float percent);
    }

    private Status status = Status.CLOSE;

    private OnUpdateStatusListener onUpdateStatusListener;

    public OnUpdateStatusListener getOnUpdateStatusListener() {
        return onUpdateStatusListener;
    }

    public void setOnUpdateStatusListener(OnUpdateStatusListener onUpdateStatusListener) {
        this.onUpdateStatusListener = onUpdateStatusListener;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        //第三步:处理监听

        /**
         * 试图去捕捉的View
         * 返回true则表示该控件的所有子控件都能被聚焦
         * 返回false则表示该控件的所有子控件都不能被聚焦
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (child == mainView) {
                left = fixLeft(left);
            }

            return left;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            int newLeft = mainView.getLeft();
            if (changedView == mainView) {
                newLeft = left;
            } else {
                newLeft += dx;
            }

            newLeft = fixLeft(newLeft);

            if (changedView == leftView) {
                leftView.layout(0, 0, mWidth, mHeight);
                mainView.layout(newLeft, 0, newLeft + mWidth, mHeight);
            }

            dispatchDragEvent(newLeft);

            invalidate();//为了兼容低版本，在此处需要重绘
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (mainView.getLeft() > range / 2 && xvel == 0) {
                open();
            } else if (xvel > 0) {
                open();
            } else {
                close();
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }
    };

    /**
     * 更新状态
     * @param newLeft
     */
    private void dispatchDragEvent(int newLeft) {
        //在位置变化的同时,执行伴随动画等等
        float percent = newLeft * 1.0f / range;

        animViews(percent);

        if (onUpdateStatusListener != null ){
            onUpdateStatusListener.onDraging(percent);
        }

        Status lastStatus = status ;
        status = updateStatus(percent);

        if (lastStatus != status){
            if (onUpdateStatusListener != null){
                if (status == Status.CLOSE){
                    onUpdateStatusListener.onClose();
                }else if (status == Status.OPEN){
                    onUpdateStatusListener.onOpen();
                }
            }
        }
    }

    /**
     * 更新状态
     * @param percent
     * @return
     */
    private Status updateStatus(float percent) {
        if (percent == 0){
            return Status.CLOSE;
        }else if (percent == 1){
            return Status.OPEN;
        }else{
            return Status.DRAGING;
        }
    }

    private void animViews(float percent) {
        //percent的变化范围为 0.1 ---> 1.0
        //在位置变化时让控件做伴随动画

        //首先让主控件随着percent做缩放动画即从1.0--->0.8
        //float mainPercent = 0.8f + (1 - percent) * 0.2f ;
        //float leftPercent = 0.5f + (1 - percent) * 0.5f ;
        //第一种方式：
        //mainView.setScaleX(mainPercent);
        //mainView.setScaleY(mainPercent);
        //leftView.setScaleX(leftPercent);
        //leftView.setScaleY(leftPercent);

        //第二种写法，适用类型估值器
        FloatEvaluator floatEvaluator = new FloatEvaluator();
        Float mainPercent = floatEvaluator.evaluate(percent, 1.0f, 0.8f);
        Float leftPercent = floatEvaluator.evaluate(percent, 0.5f, 1.0f);
        Float leftTranslationPercent = floatEvaluator.evaluate(percent, -mWidth / 2f, 0);
        ViewHelper.setScaleX(mainView, mainPercent);
        ViewHelper.setScaleY(mainView, mainPercent);
        ViewHelper.setScaleX(leftView, leftPercent);
        ViewHelper.setScaleY(leftView, leftPercent);
        ViewHelper.setTranslationX(leftView, leftTranslationPercent);

        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        Integer color = (Integer) argbEvaluator.evaluate(percent, Color.BLACK, Color.TRANSPARENT);
        getBackground().setColorFilter(color, PorterDuff.Mode.SRC_OVER);
    }

    private int finalLeft;

    /**
     * 被postInvalidateOnAnimation催动执行，重绘
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        //触发一个平滑的滚动事件，若返回true则表示没有滚动到指定位置，则需要重绘
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 定义关闭侧滑面板的方法
     */
    public void close() {
        toSmoothClose(true);
    }

    /**
     * 定义方法去平滑的关闭侧滑面板
     *
     * @param isSmooth
     */
    private void toSmoothClose(boolean isSmooth) {
        finalLeft = 0;

        toSmoothOpenOrClose(isSmooth);
    }

    /**
     * 定义打开侧滑面板的方法
     */
    private void open() {
        toSmoothOpen(true);
    }

    /**
     * 定义方法去平滑的打开侧滑面板
     *
     * @param isSmooth
     */
    private void toSmoothOpen(boolean isSmooth) {
        finalLeft = range;

        toSmoothOpenOrClose(isSmooth);
    }

    /**
     * 定义方法平滑的关闭或打开侧滑面板
     *
     * @param isSmooth
     */
    private void toSmoothOpenOrClose(boolean isSmooth) {
        if (isSmooth) {
            //触发一个平滑的滚动事件，若返回true则表示没有滚动到指定位置，则需要重绘
            if (mViewDragHelper.smoothSlideViewTo(mainView, finalLeft, 0)) {
                //引发重绘
                //第一种写法:
                //invalidate();

                //第二种写法：
                //该方法调用computeScroll方法的执行
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            mainView.layout(finalLeft, 0, finalLeft + mWidth, mHeight);
        }
    }

    /**
     * 修正Left的值
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left < 0) {
            return 0;
        } else if (left > range) {
            return range;
        } else {
            return left;
        }
    }

    public SlidingPanelDragView(Context context) {
        this(context, null);
    }

    public SlidingPanelDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingPanelDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //第一步:初始化ViewDragHelper对象，由于ViewDragHelper的构造方法被私有化，所以采用create方法实例化对象
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    //第二步:托管触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mViewDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 在xml加载完成后调用此方法，故可在此方法中拿到该控件的子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() < 2) {
            throw new IllegalArgumentException("This view must hava two children at least!");
        }

        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("The children must be instanceof ViewGroup");
        }

        leftView = (ViewGroup) getChildAt(0);
        mainView = (ViewGroup) getChildAt(1);
    }

    /**
     * 该方法的执行会催动onMesture方法的执行，故可在此方法中拿到控件的高度和宽度
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        range = (int) (mWidth * 0.6f);
    }
}
