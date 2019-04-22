package com.example.samplecompasstracker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

    private Handler handler = new Handler();
    private Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
            handler.removeCallbacks(this);
            handler.postDelayed(this, 1000 / 60);
        }
    };

    private Compass compass;
    private boolean isPortrait;

    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        compass = new Compass(context);
        isPortrait = (getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().widthPixels) > 1.4f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int mode2 = MeasureSpec.getMode(heightMeasureSpec);

        int rawWidth = MeasureSpec.getSize(widthMeasureSpec);
        int rawHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = (int) ((isPortrait ? 1.0f : 0.8f) * ((float) rawWidth));
        int height = (int) (((float) rawWidth) * 0.86f);
        if (mode == MeasureSpec.EXACTLY) {
            width = rawWidth;
        } else if (mode == Integer.MIN_VALUE) {
            width = Math.min(width, rawWidth);
        }
        if (mode2 == MeasureSpec.EXACTLY) {
            height = rawHeight;
        } else if (mode2 == Integer.MIN_VALUE) {
            height = Math.min(height, rawHeight);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        compass.drawCompass(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        compass.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(drawRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(drawRunnable);
        super.onDetachedFromWindow();
    }

    public void setAzimuth(float azimuth) {
        compass.setAzimuth(azimuth);
    }

    public void setDestinationLoaction(float destinationBearing) {
        compass.setDestinationLocation(destinationBearing);
    }

    public void setTrackingMode(boolean isTracking) {
        compass.setTracking(isTracking);
    }

    public boolean isTracking() {
        return compass.isTracking();
    }
}
