package com.jcodecraeer.xrecyclerview;

import android.view.View;

/**
 * Author: Lin Guanhong
 * <p>
 * My GitHub : https://github.com/af913337456/
 * <p>
 * My Blog   : http://www.cnblogs.com/linguanh/
 * <p>
 * on 2017/11/8.
 */

public interface CustomFooterViewCallBack {

    void onLoadingMore(View yourFooterView);
    void onLoadMoreComplete(View yourFooterView);
    void onSetNoMore(View yourFooterView,boolean noMore);

}
