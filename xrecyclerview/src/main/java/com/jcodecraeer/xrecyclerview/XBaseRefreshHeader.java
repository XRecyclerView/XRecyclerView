package com.jcodecraeer.xrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;

/**
 * 继承该类实现自定义下拉刷新头部样式
 * @see XRefreshHeader
 * @author lxy
 */
public abstract class XBaseRefreshHeader extends LinearLayout implements BaseRefreshHeader {

    protected View mContainer;
    protected int mState = STATE_NORMAL;
    public int mMeasuredHeight;

    @LayoutRes
    protected abstract int getHeaderLayout();

    protected abstract void onHeaderCreated(View header);

    public abstract void setProgressStyle(int style);

    public abstract void setArrowImageView(int resid);

    protected abstract void onStartAnimation();
    protected abstract void onStopAnimation();
    protected abstract void onNormal();
    protected abstract void onReleaseToRefresh();
    protected abstract void onRefreshing();
    protected abstract void onDone();

    protected abstract void onHeaderScrolling(int percent);

	public XBaseRefreshHeader(Context context) {
		this(context,null);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XBaseRefreshHeader(Context context, AttributeSet attrs) {
		super(context, attrs);

        mContainer = LayoutInflater.from(getContext()).inflate(getHeaderLayout(), null);
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        onHeaderCreated(mContainer);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
	}

    public int getState() {
        return mState;
    }

    public void setState(int state){
        if (state == mState) return ;

        switch(state){
            case STATE_NORMAL:
                onStopAnimation();
                onNormal();
                break;
            case STATE_RELEASE_TO_REFRESH:
                onStopAnimation();
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    onReleaseToRefresh();
                }

                break;
            case STATE_REFRESHING:
                onStartAnimation();
                onRefreshing();
                break;
            case STATE_DONE:
                onStopAnimation();
                onDone();
                break;
            default:
        }

        mState = state;

    }

    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    @Override
    public void onMove(float delta) {
        if(getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                }else {
                    setState(STATE_NORMAL);
                }
            }
        }

        onHeaderScrolling(getCurrentPercent());//更新百分比
    }

    @Override
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if(getVisibleHeight() > mMeasuredHeight &&  mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    @Override
	public void refreshComplete(){
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                reset();
            }
        }, 200);
	}

    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    protected void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    public int getCurrentPercent(){
        double molecule = getVisibleHeight();
        double denominator = mMeasuredHeight;
        if (molecule >=denominator){
            return 100;
        }
        double result = molecule/denominator;
        int percent = (int) (result * 100);

        String log = String.format("%s / %s = %s",molecule,denominator,percent);
        Log.i("percent",log);

        return percent;
    }

    public String friendlyTime(Date time) {
        //获取time距离当前的秒数
        int ct = (int)((System.currentTimeMillis() - time.getTime())/1000);

        if(ct == 0) {
            return "刚刚";
        }

        if(ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if(ct >= 60 && ct < 3600) {
            return Math.max(ct / 60,1) + "分钟前";
        }
        if(ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if(ct >= 86400 && ct < 2592000){ //86400 * 30
            int day = ct / 86400 ;
            return day + "天前";
        }
        if(ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

}