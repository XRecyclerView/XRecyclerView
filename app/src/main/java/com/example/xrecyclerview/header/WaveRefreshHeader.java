package com.example.xrecyclerview.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.xrecyclerview.R;
import com.example.xrecyclerview.waveview.WaveView;
import com.jcodecraeer.xrecyclerview.XBaseRefreshHeader;

/**
 * Created by lxy on 2016/11/1 12:09.
 */

public class WaveRefreshHeader extends XBaseRefreshHeader {

    private WaveView waveView;

    public WaveRefreshHeader(Context context) {
        super(context);
    }

    public WaveRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getHeaderLayout() {
        return R.layout.wave_refresh_header;
    }

    @Override
    protected void onHeaderCreated(View header) {
        waveView = (WaveView) findViewById(R.id.wave_view);
    }

    @Override
    public void setProgressStyle(int style) {
        //do nothing

    }

    @Override
    public void setArrowImageView(int resid) {
        //do nothing

    }

    @Override
    protected void onStartAnimation() {
        //do nothing
    }

    @Override
    protected void onStopAnimation() {
        //do nothing
    }

    @Override
    protected void onNormal() {
        //do nothing
    }

    @Override
    protected void onReleaseToRefresh() {
        //do nothing
    }

    @Override
    protected void onRefreshing() {
        waveView.setProgress(100);
    }

    @Override
    protected void onDone() {
        //do nothing
    }

    @Override
    protected void onHeaderScrolling(int percent) {
        waveView.setProgress(percent);
    }
}
