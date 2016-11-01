package com.example.xrecyclerview.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xrecyclerview.R;
import com.jcodecraeer.xrecyclerview.XBaseRefreshHeader;

import java.util.Date;

/**
 * 自定义下拉刷新头部样式
 * Created by lxy on 2016/10/31 16:30.
 */
public class CustomRefreshHeader extends XBaseRefreshHeader{

    private ImageView iv_anim;
    private TextView tv_refresh_time;
    private AnimationDrawable animation;

    public CustomRefreshHeader(Context context) {
        this(context,null);
    }

    public CustomRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getHeaderLayout() {
        return R.layout.custom_refresh_header;
    }

    @Override
    protected void onHeaderCreated(View header) {

        iv_anim = (ImageView)findViewById(R.id.iv_anim);
        tv_refresh_time = (TextView)findViewById(R.id.tv_refresh_time);
        animation = (AnimationDrawable) iv_anim.getDrawable();
    }

    @Override
    protected void onStartAnimation() {
        animation.start();
    }

    @Override
    protected void onStopAnimation() {
        animation.stop();
    }

    @Override
    protected void onNormal() {
        tv_refresh_time.setText(com.jcodecraeer.xrecyclerview.R.string.listview_header_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        tv_refresh_time.setText(com.jcodecraeer.xrecyclerview.R.string.listview_header_hint_release);
    }

    @Override
    protected void onRefreshing() {
        tv_refresh_time.setText(com.jcodecraeer.xrecyclerview.R.string.refreshing);
    }

    @Override
    protected void onDone() {
        tv_refresh_time.setText(com.jcodecraeer.xrecyclerview.R.string.refresh_done);
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
    protected void onHeaderScrolling(int percent) {
        //do nothing
    }

    @Override
    public void refreshComplete() {
        tv_refresh_time.setText(friendlyTime(new Date()));
        super.refreshComplete();
    }
}
