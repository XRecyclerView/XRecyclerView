package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import static com.jcodecraeer.xrecyclerview.BaseRefreshHeader.STATE_DONE;

public class XRecyclerView extends RecyclerView {
    private boolean isLoadingData = false;
    private boolean isNoMore = false;
    private int mRefreshProgressStyle = ProgressStyle.SysProgress;
    private int mLoadingMoreProgressStyle = ProgressStyle.SysProgress;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private WrapAdapter mWrapAdapter;
    private float mLastY = -1;
    private float dragRate = 3;
    private CustomFooterViewCallBack footerViewCallBack;
    private LoadingListener mLoadingListener;
    private ArrowRefreshHeader mRefreshHeader;
    private boolean pullRefreshEnabled = true;
    private boolean loadingMoreEnabled = true;
    private boolean isWrappedByScrollView = false;
    //The following ItemViewType is a reserved value (ReservedItemViewType), which will be thrown if the user's adapter is repeated with them
    // However, for the sake of simplicity, we have detected that the prompt to the user when repeating is that the ItemViewType must be less than 10000.
    private static final int TYPE_REFRESH_HEADER = 10000;//Set a big number and avoid conflicts with the user's adapter as much as possible
    private static final int TYPE_FOOTER = 10001;
    private static final int HEADER_INIT_INDEX = 10002;
    private List<Integer> sHeaderTypes = new ArrayList<>();//Each header must have a different type, otherwise the order will change when scrolling

    //When the adapter has no data, it is similar to the emptyView of the listView.
    private View mEmptyView;
    private View mFootView;
    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;

    // limit number to call load more
    // Call onLoadMore when controlling how many extras
    private int limitNumberToCallLoadMore = 1;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (pullRefreshEnabled) {
            mRefreshHeader = new ArrowRefreshHeader(getContext());
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);
        }
        LoadingMoreFooter footView = new LoadingMoreFooter(getContext());
        footView.setProgressStyle(mLoadingMoreProgressStyle);
        mFootView = footView;
        mFootView.setVisibility(GONE);
    }

    /**
     * call it when you finish the activity,
     * when you call this,better don't call some kind of functions like
     * RefreshHeader,because the reference of mHeaderViews is NULL.
     */
    public void destroy() {
        if (mHeaderViews != null) {
            mHeaderViews.clear();
            mHeaderViews = null;
        }
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).destroy();
            mFootView = null;
        }
        if (mRefreshHeader != null) {
            mRefreshHeader.destroy();
            mRefreshHeader = null;
        }
    }

    public ArrowRefreshHeader getDefaultRefreshHeaderView() {
        if (mRefreshHeader == null) {
            return null;
        }
        return mRefreshHeader;
    }

    public LoadingMoreFooter getDefaultFootView() {
        if (mFootView == null) {
            return null;
        }
        if (mFootView instanceof LoadingMoreFooter) {
            return ((LoadingMoreFooter) mFootView);
        }
        return null;
    }

    // set the number to control call load more,see the demo on linearActivity
    public void setLimitNumberToCallLoadMore(int limitNumberToCallLoadMore) {
        this.limitNumberToCallLoadMore = limitNumberToCallLoadMore;
    }

    public View getFootView() {
        return mFootView;
    }

    public void setFootViewText(String loading, String noMore) {
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setLoadingHint(loading);
            ((LoadingMoreFooter) mFootView).setNoMoreHint(noMore);
        }
    }

    public void addHeaderView(View view) {
        if (mHeaderViews == null || sHeaderTypes == null)
            return;
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeHeaderView(@NonNull View v) {
        if (mHeaderViews == null || sHeaderTypes == null || v == null)
            return;
        for (View view : mHeaderViews) {
            if (view == v) {
                mHeaderViews.remove(view);
                break;
            }
        }
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    public void removeAllHeaderView() {
        if (mHeaderViews == null || sHeaderTypes == null)
            return;
        mHeaderViews.clear();
        if (mWrapAdapter != null) {
            mWrapAdapter.notifyDataSetChanged();
        }
    }

    //Determine which header is based on the ViewType of the header
    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        if (mHeaderViews == null)
            return null;
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    //Determine if a type is HeaderType
    private boolean isHeaderType(int itemViewType) {
        if (mHeaderViews == null || sHeaderTypes == null)
            return false;
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    //Determine if it is an itemViewType reserved by XRecyclerView
    private boolean isReservedItemViewType(int itemViewType) {
        if (itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER || sHeaderTypes.contains(itemViewType)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("all")
    public void setFootView(@NonNull final View view, @NonNull CustomFooterViewCallBack footerViewCallBack) {
        if (view == null || footerViewCallBack == null) {
            return;
        }
        mFootView = view;
        this.footerViewCallBack = footerViewCallBack;
    }

    // Fix issues (all refresh times are the same when using this control in multiple places #359)
    public void setRefreshTimeSpKeyName(String keyName) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setXrRefreshTimeKey(keyName);
        }
    }

    public void loadMoreComplete() {
        isLoadingData = false;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_COMPLETE);
        } else {
            if (footerViewCallBack != null) {
                footerViewCallBack.onLoadMoreComplete(mFootView);
            }
        }
    }

    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setState(isNoMore ? LoadingMoreFooter.STATE_NOMORE : LoadingMoreFooter.STATE_COMPLETE);
        } else {
            if (footerViewCallBack != null) {
                footerViewCallBack.onSetNoMore(mFootView, noMore);
            }
        }
    }

    public void refresh() {
        if (pullRefreshEnabled && mLoadingListener != null) {
            mRefreshHeader.setState(ArrowRefreshHeader.STATE_REFRESHING);
            mLoadingListener.onRefresh();
        }
    }

    public void reset() {
        setNoMore(false);
        loadMoreComplete();
        refreshComplete();
    }

    public void refreshComplete() {
        if (mRefreshHeader != null)
            mRefreshHeader.refreshComplete();
        setNoMore(false);
    }

    public void setRefreshHeader(ArrowRefreshHeader refreshHeader) {
        mRefreshHeader = refreshHeader;
    }

    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled) {
            if (mFootView instanceof LoadingMoreFooter) {
                ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_COMPLETE);
            }
        }
    }

    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setProgressStyle(style);
        }
    }

    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    /**
     * issues/303
     * The trigger distance of the pull-down refresh is too large, so that the user pull-down refresh operation often fails to reach the trigger point, and cannot be refreshed, and must be pulled down a certain distance.
     */
    // Set the offset measure factor when pulling down. y = deltaY/dragRate
    // The larger the dragRate, the more the user has to pull down and slide for a longer time to trigger a pulldown refresh. The smaller the opposite, the shorter the distance
    public void setDragRate(float rate) {
        if (rate <= 0.5) {
            return;
        }
        dragRate = rate;
    }

    // if you can't sure that you are 100% going to
    // have no data load back from server anymore,do not use this
    @Deprecated
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    @Deprecated
    public View getEmptyView() {
        return mEmptyView;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    //Avoid the ClassCastException caused by the user calling getAdapter()
    @Override
    public Adapter getAdapter() {
        if (mWrapAdapter != null)
            return mWrapAdapter.getOriginalAdapter();
        else
            return null;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mWrapAdapter != null) {
            if (layout instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) layout);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (mWrapAdapter.isHeader(position) || mWrapAdapter.isFooter(position) || mWrapAdapter.isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    /**
     * ===================== try to adjust the position for XR when you call those functions below ======================
     */
    // which cause "Called attach on a child which is not detached" exception info.
    // {reason analyze @link:http://www.cnblogs.com/linguanh/p/5348510.html}
    // by lgh on 2017-11-13 23:55

    // example: listData.remove(position); You can also see a demo on LinearActivity
    public <T> void notifyItemRemoved(List<T> listData, int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int headerSize = getHeaders_includingRefreshCount();
        int adjPos = position + headerSize;
        mWrapAdapter.adapter.notifyItemRemoved(adjPos);
        mWrapAdapter.adapter.notifyItemRangeChanged(headerSize, listData.size(), new Object());
    }

    public <T> void notifyItemInserted(List<T> listData, int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int headerSize = getHeaders_includingRefreshCount();
        int adjPos = position + headerSize;
        mWrapAdapter.adapter.notifyItemInserted(adjPos);
        mWrapAdapter.adapter.notifyItemRangeChanged(headerSize, listData.size(), new Object());
    }

    public void notifyItemChanged(int position) {
        if (mWrapAdapter.adapter == null)
            return;
        int adjPos = position + getHeaders_includingRefreshCount();
        mWrapAdapter.adapter.notifyItemChanged(adjPos);
    }

    public void notifyItemChanged(int position, Object o) {
        if (mWrapAdapter.adapter == null)
            return;
        int adjPos = position + getHeaders_includingRefreshCount();
        mWrapAdapter.adapter.notifyItemChanged(adjPos, o);
    }

    private int getHeaders_includingRefreshCount() {
        if (mWrapAdapter == null) return getRefreshHeaderCount();
        return mWrapAdapter.getHeadersCount() + getRefreshHeaderCount();
    }

    NestedScrollView.OnScrollChangeListener onScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    LayoutManager layoutManager = getLayoutManager();
                    int lastVisibleItemPosition;
                    int pastVisiblesItems;
                    if (layoutManager instanceof GridLayoutManager) {
                        lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                        pastVisiblesItems = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        int[] intoFirst = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                        ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(intoFirst);
                        lastVisibleItemPosition = findMax(into);
                        pastVisiblesItems = findMax(intoFirst);
                    } else {
                        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        pastVisiblesItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    }
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int status = STATE_DONE;

                    if (mRefreshHeader != null)
                        status = mRefreshHeader.getState();
                    if (layoutManager.getChildCount() > 0
                            && (visibleItemCount + pastVisiblesItems) >= totalItemCount
                            && !isNoMore
                            && status < ArrowRefreshHeader.STATE_REFRESHING) {
                        activeLoadMore();
                    }
                }
            }
        }
    };

    void activeLoadMore() {
        isLoadingData = true;
        if (mFootView instanceof LoadingMoreFooter) {
            ((LoadingMoreFooter) mFootView).setState(LoadingMoreFooter.STATE_LOADING);
        } else {
            if (footerViewCallBack != null) {
                footerViewCallBack.onLoadingMore(mFootView);
            }
        }
        mLoadingListener.onLoadMore();
    }

    /**
     * ======================================================= end =======================================================
     */

    @Override
    public void onScrollStateChanged(int state) {
//        Log.d(getClass().getSimpleName(), "onScrollStateChanged() called with: state = [" + state + "]");
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();

            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            int adjAdapterItemCount = layoutManager.getItemCount() + getHeaders_includingRefreshCount() + (pullRefreshEnabled ? 1 : 2);
            int status = STATE_DONE;
            if (mRefreshHeader != null)
                status = mRefreshHeader.getState();
            if (!isWrappedByScrollView
                    && layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= adjAdapterItemCount - limitNumberToCallLoadMore
                    && adjAdapterItemCount >= layoutManager.getChildCount()
                    && !isNoMore
                    && status < ArrowRefreshHeader.STATE_REFRESHING) {
                activeLoadMore();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader == null)
                        break;
                    mRefreshHeader.onMove(deltaY / dragRate);
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader != null && mRefreshHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private boolean isOnTop() {
        if (mRefreshHeader == null)
            return false;
        else
            return mRefreshHeader.getParent() != null;
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
            if (mWrapAdapter != null && mEmptyView != null) {
                int emptyCount = getRefreshHeaderCount() + mWrapAdapter.getHeadersCount();
                if (loadingMoreEnabled) {
                    emptyCount++;
                }
                if (mWrapAdapter.getItemCount() == emptyCount) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    XRecyclerView.this.setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    XRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public WrapAdapter getWrapAdapter() {
        return mWrapAdapter;
    }

    public int getRefreshHeaderCount() {
        return pullRefreshEnabled ? 1 : 0;
    }

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public RecyclerView.Adapter getOriginalAdapter() {
            return this.adapter;
        }

        public boolean isHeader(int position) {
            if (mHeaderViews == null)
                return false;
            return position >= getRefreshHeaderCount() && position < mHeaderViews.size() + getRefreshHeaderCount();
        }

        public boolean isFooter(int position) {
            if (loadingMoreEnabled) {
                return position == getItemCount() - 1;
            } else {
                return false;
            }
        }

        public boolean isRefreshHeader(int position) {
            if (pullRefreshEnabled)
                return position == 0;
            return false;
        }

        public int getHeadersCount() {
//            Log.d(getClass().getSimpleName(), "getHeadersCount() called");
            if (mHeaderViews == null)
                return 0;
            return mHeaderViews.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeader);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }
            int adjPosition = position - (getHeadersCount() + getRefreshHeaderCount());
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        // some times we need to override this
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (isHeader(position) || isRefreshHeader(position)) {
                return;
            }

            int adjPosition = position - (getHeadersCount() + getRefreshHeaderCount());
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    if (payloads.isEmpty()) {
                        adapter.onBindViewHolder(holder, adjPosition);
                    } else {
                        adapter.onBindViewHolder(holder, adjPosition, payloads);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            int adjLen = (loadingMoreEnabled ? getRefreshHeaderCount() + 1 : getRefreshHeaderCount());
            if (adapter != null) {
                return getHeadersCount() + adapter.getItemCount() + adjLen;
            } else {
                return getHeadersCount() + adjLen;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int adjPosition = position - (getHeadersCount() + getRefreshHeaderCount());
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                position = position - getRefreshHeaderCount();
                return sHeaderTypes.get(position);
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    int type = adapter.getItemViewType(adjPosition);
                    if (isReservedItemViewType(type)) {
                        throw new IllegalStateException("XRecyclerView require itemViewType in adapter should be less than 10000 ");
                    }
                    return type;
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount() + getRefreshHeaderCount()) {
                int adjPosition = position - (getHeadersCount() + getRefreshHeaderCount());
                if (adjPosition < adapter.getItemCount()) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            adapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }

    }

    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    public interface LoadingListener {

        void onRefresh();

        void onLoadMore();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Solve the problem of conflict with CollapsingToolbarLayout
        AppBarLayout appBarLayout = null;
        ViewParent p = getParent();
        CoordinatorLayout coordinatorLayout = null;
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                coordinatorLayout = (CoordinatorLayout) p;
//                break;
            } else if (p instanceof NestedScrollView) {
                if (loadingMoreEnabled) {
                    NestedScrollView nestedScrollView = (NestedScrollView) p;
                    isWrappedByScrollView = true;
                    nestedScrollView.setOnScrollChangeListener(onScrollChangeListener);
                }
            }
            p = p.getParent();
        }
        if (coordinatorLayout != null) {
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        appbarState = state;
                    }
                });
            }
        }
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private int mOrientation;

        /**
         * Sole constructor. Takes in a {@link Drawable} to be used as the interior
         * divider.
         *
         * @param divider A divider {@code Drawable} to be drawn on the RecyclerView
         */
        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        /**
         * Draws horizontal or vertical dividers onto the parent RecyclerView.
         *
         * @param canvas The {@link Canvas} onto which dividers will be drawn
         * @param parent The RecyclerView onto which dividers are being added
         * @param state  The current RecyclerView.State of the RecyclerView
         */
        @Override
        public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                drawHorizontalDividers(canvas, parent);
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVerticalDividers(canvas, parent);
            }
        }

        /**
         * Determines the size and location of offsets between items in the parent
         * RecyclerView.
         *
         * @param outRect The {@link Rect} of offsets to be added around the child
         *                view
         * @param view    The child view to be decorated with an offset
         * @param parent  The RecyclerView onto which dividers are being added
         * @param state   The current RecyclerView.State of the RecyclerView
         */
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) <= mWrapAdapter.getHeadersCount() + getRefreshHeaderCount()) {
                return;
            }
            mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.left = mDivider.getIntrinsicWidth();
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.getIntrinsicHeight();
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented horizontally.
         *
         * @param canvas The {@link Canvas} onto which horizontal dividers will be
         *               drawn
         * @param parent The RecyclerView onto which horizontal dividers are being
         *               added
         */
        private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
            int parentTop = parent.getPaddingTop();
            int parentBottom = parent.getHeight() - parent.getPaddingBottom();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int parentLeft = child.getRight() + params.rightMargin;
                int parentRight = parentLeft + mDivider.getIntrinsicWidth();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented vertically.
         *
         * @param canvas The {@link Canvas} onto which vertical dividers will be
         *               drawn
         * @param parent The RecyclerView onto which vertical dividers are being
         *               added
         */
        private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
            int parentLeft = parent.getPaddingLeft();
            int parentRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int parentTop = child.getBottom() + params.bottomMargin;
                int parentBottom = parentTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
                mDivider.draw(canvas);
            }
        }
    }

    /**
     * add by LinGuanHong below
     */
    private int scrollDyCounter = 0;

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        /** if we scroll to position 0, the scrollDyCounter should be reset */
        if (position == 0) {
            scrollDyCounter = 0;
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (scrollAlphaChangeListener == null) {
            return;
        }
        int height = scrollAlphaChangeListener.setLimitHeight();
        scrollDyCounter = scrollDyCounter + dy;
        if (scrollDyCounter <= 0) {
            scrollAlphaChangeListener.onAlphaChange(0);
        } else if (scrollDyCounter <= height && scrollDyCounter > 0) {
            float scale = (float) scrollDyCounter / height; /** 255/height = x/255 */
            float alpha = (255 * scale);
            scrollAlphaChangeListener.onAlphaChange((int) alpha);
        } else {
            scrollAlphaChangeListener.onAlphaChange(255);
        }
    }

    private ScrollAlphaChangeListener scrollAlphaChangeListener;

    public void setScrollAlphaChangeListener(
            ScrollAlphaChangeListener scrollAlphaChangeListener
    ) {
        this.scrollAlphaChangeListener = scrollAlphaChangeListener;
    }

    public interface ScrollAlphaChangeListener {
        void onAlphaChange(int alpha);

        /**
         * you can handle the alpha insert it
         */
        int setLimitHeight(); /** set a height for the begging of the alpha start to change */
    }
}