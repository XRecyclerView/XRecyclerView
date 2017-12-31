package com.example.xrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 作者：林冠宏
 * <p>
 * author: LinGuanHong,lzq is my dear wife.
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 * <p>
 * on 2017/11/19.
 */

public class CustoumLayoutManager extends RecyclerView.LayoutManager {
    /**
     * Create a default <code>LayoutParams</code> object for a child of the RecyclerView.
     * <p>
     * <p>LayoutManagers will often want to use a custom <code>LayoutParams</code> type
     * to store extra information specific to the layout. Client code should subclass
     * {@link RecyclerView.LayoutParams} for this purpose.</p>
     * <p>
     * <p><em>Important:</em> if you use your own custom <code>LayoutParams</code> type
     * you must also override
     * {@link #checkLayoutParams(LayoutParams)},
     * {@link #generateLayoutParams(ViewGroup.LayoutParams)} and
     * {@link #generateLayoutParams(Context, AttributeSet)}.</p>
     *
     * @return A new LayoutParams for a child view
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }
}




































