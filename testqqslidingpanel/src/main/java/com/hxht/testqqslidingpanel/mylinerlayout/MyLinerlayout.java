package com.hxht.testqqslidingpanel.mylinerlayout;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.hxht.testqqslidingpanel.customdragview.SlidingPanelDragView;

public class MyLinerlayout extends LinearLayout {

    private SlidingPanelDragView dragView ;

    public MyLinerlayout(Context context) {
        super(context);
    }

    public MyLinerlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinerlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragView(SlidingPanelDragView dragView) {
        this.dragView = dragView;
    }

    /**
     * 是否拦截事件
     * 返回true则拦截
     * 返回false则不拦截
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (dragView.getStatus() == SlidingPanelDragView.Status.CLOSE){
            return super.onInterceptTouchEvent(ev);
        }else{
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragView.getStatus() == SlidingPanelDragView.Status.CLOSE){
            return super.onTouchEvent(event);
        }else{
            //if (event.getAction() == MotionEvent.ACTION_UP){
            //    dragView.close();
            //}
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_UP){
                dragView.close();
            }
            return true;
        }
    }
}
