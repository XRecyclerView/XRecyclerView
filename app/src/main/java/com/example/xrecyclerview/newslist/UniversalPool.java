package com.example.xrecyclerview.newslist;

import android.support.v7.widget.RecyclerView;

/**
 * Created by joim on 2018/6/4.
 */

public final class UniversalPool {

    private static RecyclerView.RecycledViewPool sPool;

    public static RecyclerView.RecycledViewPool getUniversalPool() {
        initPoolIfNecessary();
        return sPool;
    }

    private static void initPoolIfNecessary() {
        if (sPool == null) {
            sPool = new RecyclerView.RecycledViewPool();
        }
    }
}
