package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;

public class LoadingMoreFooter extends LinearLayout {

    private SimpleViewSwitcher progressCon;
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NO_MORE = 2;
    private TextView mText;
    private String loadingHint;
    private String noMoreHint;
    private String loadingDoneHint;
    private View mNoMoreView;
    private TextView mNoMoreTextView;

	public LoadingMoreFooter(Context context) {
		super(context);
		initView();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public LoadingMoreFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

    public void setLoadingHint(String hint) {
        loadingHint = hint;
    }

    public void setNoMoreHint(String hint) {
        noMoreHint = hint;
    }

    public void setLoadingDoneHint(String hint) {
        loadingDoneHint = hint;
    }

    public void initView(){
        setGravity(Gravity.CENTER);
        setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressCon = new SimpleViewSwitcher(getContext());
        progressCon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(this.getContext());
        progressView.setIndicatorColor(0xffB5B5B5);
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
        progressCon.setView(progressView);
        addView(progressCon);

        mText = new TextView(getContext());
        mText.setText("正在加载...");
        loadingHint = (String)getContext().getText(R.string.listview_loading);
        noMoreHint = (String)getContext().getText(R.string.nomore_loading);
        loadingDoneHint = (String)getContext().getText(R.string.loading_done);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins( (int)getResources().getDimension(R.dimen.textandiconmargin),0,0,0 );
        mText.setLayoutParams(layoutParams);
        addView(mText);

        mNoMoreView = View.inflate(getContext(), R.layout.view_no_more, null);
        mNoMoreTextView = (TextView) mNoMoreView.findViewById(R.id.no_more_tv);
        mNoMoreView.setVisibility(GONE);
        addView(mNoMoreView);
    }

    public void setNoMoreView(View noMoreView) {
        if (noMoreView != null) {
            removeView(mNoMoreView);
            mNoMoreView = noMoreView;
            addView(mNoMoreView);
        }
    }

    public void setNoMoreText(String text) {
        if (mNoMoreTextView != null) {
            mNoMoreTextView.setText(text);
        }
    }

    public void setProgressStyle(int style) {
        if(style == ProgressStyle.SysProgress){
            progressCon.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
        }else{
            AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(this.getContext());
            progressView.setIndicatorColor(0xffB5B5B5);
            progressView.setIndicatorId(style);
            progressCon.setView(progressView);
        }
    }

    public void  setState(int state) {
        switch(state) {
            case STATE_LOADING:
                mText.setVisibility(VISIBLE);
                progressCon.setVisibility(View.VISIBLE);
                mText.setText(loadingHint);
                mNoMoreView.setVisibility(GONE);
                this.setVisibility(View.VISIBLE);
                    break;
            case STATE_COMPLETE:
                mText.setVisibility(VISIBLE);
                mText.setText(loadingDoneHint);
                mNoMoreView.setVisibility(GONE);
                this.setVisibility(View.GONE);
                break;
            case STATE_NO_MORE:
                mText.setVisibility(GONE);
                progressCon.setVisibility(View.GONE);
                mNoMoreView.setVisibility(VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }
}
