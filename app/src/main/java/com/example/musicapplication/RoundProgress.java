package com.example.musicapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgress extends View {
    private static final int DEFAULT_BG_COLOR = Color.GRAY;
    private static final int DEFAULT_ROUND_COLOR = Color.RED;
    private static final float DEFAULT_ROUND_WIDTH =10;
    private int mBgColor;
    private int mRoundColor;
    private float mRoundWidth;
    private Paint mPaint;
    private int mCenterY;
    private int mCenterX;
    private float mRadius;
    private RectF mRectF;
    private int mProgerss=0;

    public RoundProgress(Context context){
        this(context,null);
    }
    public RoundProgress(Context context, AttributeSet attributeSet){
        this(context,attributeSet,0);
    }
    public RoundProgress(Context context,AttributeSet attributeSet,int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
        TypedArray a = getResources().obtainAttributes(attributeSet, R.styleable.RoundProgress);
        mBgColor = a.getColor(R.styleable.RoundProgress_bgColor, DEFAULT_BG_COLOR);
        mRoundColor = a.getColor(R.styleable.RoundProgress_roundColor, DEFAULT_ROUND_COLOR);
        mRoundWidth = a.getDimension(R.styleable.RoundProgress_roundWidth, DEFAULT_ROUND_WIDTH);
        a.recycle();
        init();
    }
    private void init(){
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onSizeChanged(int w,int h,int oldw,int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        mCenterX=w/2;
        mCenterY=h/2;
        int min=Math.min(mCenterX,mCenterY);
        mRoundWidth=mRoundWidth/2;
        mRadius=min-mRoundWidth/2;
        mRectF=new RectF(mCenterX-mRadius,mCenterY-mRadius,mCenterX+mRadius,mCenterY+mRadius);
    }
    @Override
    protected void onDraw(Canvas canvas){
        mPaint.setColor(mBgColor);
        mPaint.setStrokeWidth(mRoundWidth);
        canvas.drawCircle(mCenterX,mCenterY,mRadius,mPaint);
        mPaint.setColor(mRoundColor);
        canvas.drawArc(mRectF,0,(float)(3.6*mProgerss),false,mPaint);
    }
    public synchronized void setProgress(int progerss){
        this.mProgerss=progerss;
        postInvalidate();
    }
}
