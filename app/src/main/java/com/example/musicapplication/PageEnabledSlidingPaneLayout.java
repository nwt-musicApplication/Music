package com.example.musicapplication;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.jar.Attributes;

public class PageEnabledSlidingPaneLayout extends SlidingPaneLayout {
    private float mInitialMotionx;
    private float mInitialMotiony;
    private float mEdgeSlop;
    public PageEnabledSlidingPaneLayout(Context context){
        this(context,null);
    }
    public PageEnabledSlidingPaneLayout(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public PageEnabledSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        ViewConfiguration config=ViewConfiguration.get(context);
        mEdgeSlop=config.getScaledEdgeSlop();
    }
    public boolean onIntercreptTouchEvevt(MotionEvent ev){
        switch (MotionEventCompat.getActionMasked(ev)){
            case MotionEvent.ACTION_DOWN:{
                mInitialMotionx=ev.getX();
                mInitialMotiony=ev.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final float x=ev.getX();
                final float y=ev.getY();
                if(mInitialMotionx>mEdgeSlop&&!isOpen()&&canScroll(this,false,Math.round(x-mInitialMotiony),Math.round(x),Math.round(y))){
                    MotionEvent cancelEvent=MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onInterceptTouchEvent(cancelEvent);
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
