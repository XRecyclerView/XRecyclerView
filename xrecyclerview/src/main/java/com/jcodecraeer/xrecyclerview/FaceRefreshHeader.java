package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Author: YuJunKui
 * Time:2017/9/22 11:56
 * Tips:
 */

public class FaceRefreshHeader extends VideoRefreshHeader {


    public FaceRefreshHeader(Context context) {
        super(context);
    }

    public FaceRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int loadingRes() {
        return R.drawable.x_anim_face_loading;
    }
}
