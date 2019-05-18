package com.example.musicapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoisePlayingIcon extends View {
    private Paint paint;
    private List<Pointer> pointers;
    private int pointerNum;
    private float basePointX;
    private float basePointY;
    private float pointerPadding;
    private float pointerWidth;
    private int pointerColor =Color.rgb(156,39,176);
    private boolean isPlaying = false;
    private Thread myThread;
    private int pointerSpeed;
    public VoisePlayingIcon(Context context) {
        super(context);
        init();
    }
    public VoisePlayingIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.voisePlayingIconAttr);
        pointerColor = ta.getColor(R.styleable.voisePlayingIconAttr_pointer_color, Color.rgb(156,39,176));
        pointerNum = ta.getInt(R.styleable.voisePlayingIconAttr_pointer_num, 4);
        pointerWidth = DensityUtils.dp2px(getContext(), ta.getFloat(R.styleable.voisePlayingIconAttr_pointer_width, 5f));
        pointerSpeed = ta.getInt(R.styleable.voisePlayingIconAttr_pointer_speed, 40);
        init();
    }
    public VoisePlayingIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.voisePlayingIconAttr);
        pointerColor = ta.getColor(R.styleable.voisePlayingIconAttr_pointer_color, Color.rgb(156,39,176));
        pointerNum = ta.getInt(R.styleable.voisePlayingIconAttr_pointer_num, 4);
        pointerWidth = DensityUtils.dp2px(getContext(), ta.getFloat(R.styleable.voisePlayingIconAttr_pointer_width, 5f));
        pointerSpeed = ta.getInt(R.styleable.voisePlayingIconAttr_pointer_speed, 40);
        init();
    }
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(pointerColor);
        pointers = new ArrayList<>();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        basePointY = getHeight() - getPaddingBottom();
        Random random = new Random();
        if (pointers != null)
            pointers.clear();
        for (int i = 0; i < pointerNum; i++) {
            pointers.add(new Pointer((float) (0.1 * (random.nextInt(10) + 1) * (getHeight() - getPaddingBottom() - getPaddingTop()))));
        }
        pointerPadding = (getWidth() - getPaddingLeft() - getPaddingRight() - pointerWidth * pointerNum) / (pointerNum - 1);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        basePointX = 0f + getPaddingLeft();
        for (int i = 0; i < pointers.size(); i++) {
            canvas.drawRect(basePointX,
                    basePointY - pointers.get(i).getHeight(),
                    basePointX + pointerWidth,
                    basePointY,
                    paint);
            basePointX += (pointerPadding + pointerWidth);
        }
    }
    public void start() {
        if (!isPlaying) {
            if (myThread == null) {
                myThread = new Thread(new MyRunnable());
                myThread.start();
            }
            isPlaying = true;
        }
    }
    public void stop() {
        isPlaying = false;
        invalidate();
    }
    private Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            invalidate();
        }
    };
    public class MyRunnable implements Runnable {
        @Override
        public void run() {

            for (float i = 0; i < Integer.MAX_VALUE; ) {
                try {
                    for (int j = 0; j < pointers.size(); j++) {
                        float rate = (float) Math.abs(Math.sin(i + j));
                        pointers.get(j).setHeight((basePointY - getPaddingTop()) * rate);
                    }
                    Thread.sleep(pointerSpeed);
                    if (isPlaying) {
                        myHandler.sendEmptyMessage(0);
                        i += 0.1;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public class Pointer {
        private float height;
        public Pointer(float height) {
            this.height = height;
        }
        public float getHeight() {
            return height;
        }
        public void setHeight(float height) {
            this.height = height;
        }
    }
}