package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;

import java.util.Date;

/**
 * XRecyclerView默认的头部样式
 *
 * @see ArrowRefreshHeader
 * @see BaseRefreshHeader
 */
public final class XRefreshHeader extends XBaseRefreshHeader {

    private ImageView mArrowImageView;
    private SimpleViewSwitcher mProgressBar;
    private TextView mStatusTextView;
    private TextView mHeaderTimeView;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private static final int ROTATE_ANIM_DURATION = 180;

    public XRefreshHeader(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getHeaderLayout() {
        return R.layout.listview_header;
    }

    @Override
    protected void onHeaderCreated(View header) {

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_textview);

        //init the progress view
        mProgressBar = (SimpleViewSwitcher) findViewById(R.id.listview_header_progressbar);
        AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        mProgressBar.setView(progressView);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mHeaderTimeView = (TextView) findViewById(R.id.last_refresh_time);

    }

    @Override
    public void setProgressStyle(int style) {
        if (style == ProgressStyle.SysProgress) {
            mProgressBar.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        } else {
            AVLoadingIndicatorView progressView = new AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            mProgressBar.setView(progressView);
        }
    }

    @Override
    public void setArrowImageView(int resid) {
        mArrowImageView.setImageResource(resid);
    }

    @Override
    protected void onStartAnimation() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStopAnimation() {
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNormal() {
        if (mState == STATE_RELEASE_TO_REFRESH) {
            mArrowImageView.startAnimation(mRotateDownAnim);
        }
        if (mState == STATE_REFRESHING) {
            mArrowImageView.clearAnimation();
        }
        mStatusTextView.setText(R.string.listview_header_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        mStatusTextView.setText(R.string.listview_header_hint_release);
    }

    @Override
    protected void onRefreshing() {
        mStatusTextView.setText(R.string.refreshing);
    }

    @Override
    protected void onDone() {
        mStatusTextView.setText(R.string.refresh_done);
    }

    @Override
    protected void onHeaderScrolling(int percent) {

    }

    @Override
    public void refreshComplete() {
        mHeaderTimeView.setText(friendlyTime(new Date()));
        super.refreshComplete();

    }
}