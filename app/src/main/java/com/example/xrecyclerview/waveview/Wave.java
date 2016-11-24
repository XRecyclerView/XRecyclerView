package com.example.xrecyclerview.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.xrecyclerview.R;


// y=Asin(ωx+φ)+k
class Wave extends View {
    private final int WAVE_HEIGHT_LARGE = 16;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;

    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final float WAVE_HZ_FAST = 0.13f;
    private final float WAVE_HZ_NORMAL = 0.09f;
    private final float WAVE_HZ_SLOW = 0.05f;

    public final int DEFAULT_ABOVE_WAVE_ALPHA = 50;
    public final int DEFAULT_BLOW_WAVE_ALPHA = 30;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    private int mAboveWaveColor;
    private int mBlowWaveColor;

    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    // wave animation
    private float mAboveOffset = 0.0f;
    private float mBlowOffset;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mBlowWavePath, mBlowWavePaint);
        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
    }

    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        mBlowOffset = mWaveHeight * 0.4f;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mWaveHeight * 2);
        setLayoutParams(params);
    }

    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case WaveView.MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HEIGHT_LARGE;
            case WaveView.MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case WaveView.LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case WaveView.LARGE:
                return WAVE_HZ_FAST;
            case WaveView.MIDDLE:
                return WAVE_HZ_NORMAL;
            case WaveView.LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveLength == 0) {
                startWave();
            }
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom() + 2;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

}
