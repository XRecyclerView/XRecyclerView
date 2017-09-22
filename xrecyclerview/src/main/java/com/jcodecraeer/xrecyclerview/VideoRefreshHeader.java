package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Author: YuJunKui
 * Time:2017/9/22 11:56
 * Tips:
 */

public class VideoRefreshHeader extends ArrowRefreshHeader {

    private ImageView imageView;

    public VideoRefreshHeader(Context context) {
        super(context);

    }

    public VideoRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void initView() {

        mContainer = (ViewGroup) LayoutInflater.from(getContext()).inflate(
                R.layout.x_video_listview_header, null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();

        imageView = (ImageView) findViewById(R.id.x_iv_video_listview_header);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();
    }

    @Override
    public void setState(int state) {
        if (state == mState) return;
        if (state == STATE_REFRESHING) {
            smoothScrollTo(mMeasuredHeight);
        }
        mState = state;
    }


    @Override
    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    @Override
    public void setArrowImageView(int resid) {
    }

    @Override
    public void setProgressStyle(int style) {
    }

}
