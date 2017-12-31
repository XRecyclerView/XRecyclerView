package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * 作者：林冠宏
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 * <p>
 * on 2017/12/31.
 *
 * DES:
 *      A LinearLayout which can combine with XRecyclerView in a sticky scroll status.
 *
 * Demo: see it in gitHub.
 *
 * Read_me:
 *      Only support XRecyclerView for now,if you wanna to support other viewGroups which
 * has imp NestedScrollingChild interface,you can change my code,then it will be ok.
 *      When you use it to XR,you best close pull refresh model,because it may
 * cause some new problems that i never met.
 *                                                          ----LinGuanHong
 */

public class StickyScrollLinearLayout
        extends LinearLayout
        implements NestedScrollingParent
{

    private static final String TAG = "StickyScrollLayout";

    private View mTopView;
    private View mTabView;
    private View mContentView;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTopViewHeight;
    private RecyclerView.LayoutManager layoutManager = null;
    private int targetFirstVisiblePosition = 1;

    public interface StickyScrollInitInterface{
        View setTopView();
        View setTabView();
        View setContentView();
    }

    public StickyScrollLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public StickyScrollLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        setOrientation(LinearLayout.VERTICAL);
        mScroller = new OverScroller(context);
    }

    @SuppressWarnings("all")
    public void setInitInterface(@NonNull StickyScrollInitInterface initInterface){
        if(initInterface == null)
            throw new NullPointerException("initInterface can not be null!");
        this.mTopView = initInterface.setTopView();
        if(this.mTopView != null)
            getTopViewHeight();

        this.mTabView = initInterface.setTabView();

        this.mContentView = initInterface.setContentView();
        if(this.mContentView == null)
            return;
        setTotalHeight();
        requestLayout();
    }

    public View getContentView(){
        return this.mContentView;
    }

    // 设置，当 XR 里面显示的 item 第一个的位置是多少时，触发拦截
    // to set a position of XR's dataList to control when we should call over scroll
    public void setTargetFirstVisiblePosition(int targetFirstVisiblePosition) {
        this.targetFirstVisiblePosition = targetFirstVisiblePosition;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.e(TAG, "onStartNestedScroll "+child.toString()+"  "+target.toString());
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        Log.e(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.e(TAG, "onStopNestedScroll "+target.toString());
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.e(TAG, "onNestedScroll "+dyConsumed+"----"+dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if(!(target instanceof XRecyclerView))
            // todo 2017-12-31，make it more general
            throw new UnsupportedOperationException("insert your content must is XRecyclerView!");

        layoutManager = ((RecyclerView)target).getLayoutManager();

        int firstVisiblePosition;
        if (layoutManager instanceof GridLayoutManager) {
            firstVisiblePosition = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPositions(into);
            firstVisiblePosition = into[0];
        } else {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        }
        if(firstVisiblePosition < 0)
            return;

        int scrollY = getScrollY();
        boolean temp = dy > 0 && (scrollY < mTopViewHeight);
        Log.e(TAG,
                "mTopViewHeight == "+mTopViewHeight
                        +"\ndy == "+dy
                        +"\nscrollY == "+scrollY
                        +"\nhiddenTop && showTop "+temp);
        if(!temp){
            // judge
            temp = dy < 0
                    && (scrollY >= 0)
                    &&
                    (
                            !ViewCompat.canScrollVertically(target, -1)
                                    ||
                            firstVisiblePosition==targetFirstVisiblePosition
                    );
            Log.e(TAG,
                    "mTopViewHeight == "+mTopViewHeight
                            +"\ndy == "+dy
                            +"\nscrollY == "+scrollY
                            +"\nfirstVisiblePosition "+firstVisiblePosition);
        }
        if (temp) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "onNestedFling");
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.e(TAG, "onNestedPreFling");
        //down - //up+
        if (getScrollY() >= mTopViewHeight) return false;
        fling((int) velocityY);
        return true;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.e(TAG, "getNestedScrollAxes");
        return 0;
    }

    @Deprecated
    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
    }

    // call this when destroy
    public void destroy() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 你可以在 xml 文件内配置
        // you can init those views in your xml file

//        mTopView = findViewById(R.id.topContainer);
//        mTabView = findViewById(R.id.tabLayout);
//        mContentView = findViewById(R.id.vp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mTabView == null || mTopView == null || mContentView == null)
            return;
//        getChildAt(0).
//                measure(
//                        widthMeasureSpec,
//                        MeasureSpec.makeMeasureSpec(
//                                0,
//                                MeasureSpec.UNSPECIFIED
//                        )
//                );
        setTotalHeight();
    }

    private void setTotalHeight(){
        ViewGroup.LayoutParams params = mContentView.getLayoutParams();
        params.height = getMeasuredHeight() - mTabView.getMeasuredHeight();
        setMeasuredDimension(
                getMeasuredWidth(),
                mTopView.getMeasuredHeight()
                        + mTabView.getMeasuredHeight()
                        + mContentView.getMeasuredHeight()
        );
    }

    private void getTopViewHeight(){
        if(mTopView == null)
            return;
        mTopViewHeight = mTopView.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getTopViewHeight();
    }


    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0)
            y = 0;

        if (y > mTopViewHeight)
            // 边界限制，防止把 tabView 也挡住了
            // Prevent it from hiding the tabView,so we limit it
            y = mTopViewHeight;

        if (y != getScrollY())
            super.scrollTo(x, y);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }
}
