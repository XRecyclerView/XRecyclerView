package com.jcodecraeer.xrecyclerview;

/**
 * Created by Beshoy Samy on 7/8/2018.
 */
public interface GridLayoutSpanPositionListener {
    /**
     * Used if you want to have custom span count for different positions
     * @param position position to get the span count for
     * @return span count for this position
     */
    int getSpanCountForPosition(int position);
}
