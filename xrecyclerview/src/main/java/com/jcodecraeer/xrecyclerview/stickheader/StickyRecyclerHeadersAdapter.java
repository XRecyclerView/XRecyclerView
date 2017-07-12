package com.jcodecraeer.xrecyclerview.stickheader;

/**
 * Created by Administrator on 2017/7/12.
 */

public interface StickyRecyclerHeadersAdapter {

    Boolean isChange(int position);
    String getTag(int position);
    Boolean isShowHeader(int position);
}
