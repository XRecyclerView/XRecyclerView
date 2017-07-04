package com.example.xrecyclerview;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ChenRui on 2017/7/4 0004 18:26.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
