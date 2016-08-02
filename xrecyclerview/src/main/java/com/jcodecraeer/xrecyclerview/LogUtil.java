package com.jcodecraeer.xrecyclerview;

import android.util.Log;

/**
 * @Desc: TODO
 * @Author: Major
 * @Since: 2016/8/2 0:36
 */
public class LogUtil {

    private static final String TAG = LogUtil.class.getSimpleName();

    public static void e(String msg) {
        Log.e(TAG, "==##" + msg);
    }
}
