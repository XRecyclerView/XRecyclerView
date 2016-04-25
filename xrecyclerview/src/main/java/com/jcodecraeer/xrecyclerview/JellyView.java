package com.jcodecraeer.xrecyclerview;

/**
 * Created by jianghejie on 15/11/22.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class JellyView extends View  implements BaseRefreshHeader{
    Path path;

    Paint paint;

    private int minimumHeight = 0;

    private int jellyHeight =0;

    public JellyView(Context context) {
        super(context);
        init();
    }

    public JellyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JellyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JellyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        path = new Path();
        paint = new Paint();
        paint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        paint.setAntiAlias(true);
    }

    public void setJellyColor(int jellyColor) {
        paint.setColor(jellyColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.lineTo(0, minimumHeight);
        path.quadTo(getMeasuredWidth() / 2, minimumHeight + jellyHeight, getMeasuredWidth(), minimumHeight);
        path.lineTo(getMeasuredWidth(), 0);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setMinimumHeight(int minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public void setJellyHeight(int ribbonHeight) {
        this.jellyHeight = ribbonHeight;
    }

    @Override
    public int getMinimumHeight() {
        return minimumHeight;
    }

    public int getJellyHeight() {
        return jellyHeight;
    }


    @Override
    public void refreshComplete(){

    }

    @Override
    public void onMove(float delta) {
        jellyHeight = jellyHeight + (int)delta;
        Log.i("jellyHeight", "delta = " + delta);
        this.invalidate();
    }

    @Override
    public boolean releaseAction() {
       return false;
    }
}
