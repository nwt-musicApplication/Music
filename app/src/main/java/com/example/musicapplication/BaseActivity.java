package com.example.musicapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

public class BaseActivity extends FragmentActivity implements PageEnabledSlidingPaneLayout.PanelSlideListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initSwipeBackFinish();
    }
    private void initSwipeBackFinish(){
        if(isSupportSwipeback()){
            PageEnabledSlidingPaneLayout slidingPaneLayout=new PageEnabledSlidingPaneLayout(this);
            try{
                Field f_overHang= SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                f_overHang.setAccessible(true);
                f_overHang.set(slidingPaneLayout,0);
            }catch (Exception e){
                e.printStackTrace();
            }
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(Color.GRAY);
            slidingPaneLayout.setCoveredFadeColor(Color.GRAY);
            View leftView=new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            slidingPaneLayout.addView(leftView,0);
            ViewGroup decor = (ViewGroup) getWindow().getDecorView();
            ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
            decorChild.setBackgroundColor(getResources().getColor(android.R.color.white));
            decor.removeView(decorChild);
            decor.addView(slidingPaneLayout);
            slidingPaneLayout.addView(decorChild, 1);
        }
    }
    protected boolean isSupportSwipeback(){
        return true;
    }
    @Override
    public void onPanelSlide(@NonNull View view, float v) {
    }

    @Override
    public void onPanelOpened(@NonNull View view) {
        finish();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onPanelClosed(@NonNull View view) {
    }

}
