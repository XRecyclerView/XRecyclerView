package com.example.xrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void gotoLinearActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this,LinearActivity.class);
        startActivity(intent);
    }
    public void gotoGridActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this,GridActivity.class);
        startActivity(intent);
    }
    public void gotoStaggeredGridActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this,StaggeredGridActivity.class);
        startActivity(intent);
    }

    public void gotoEmptyViewActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this, EmptyViewActivity.class);
        startActivity(intent);
    }

    public void gotoCollapsingToolbarLayoutActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this, CollapsingToolbarLayoutActivity.class);
        startActivity(intent);
    }

    public void gotoDisableExampleActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this, DisableExampleActivity.class);
        startActivity(intent);
    }

    public void gotoMultiHeaderActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this, MultiHeaderActivity.class);
        startActivity(intent);
    }
    public void gotoItemDecorationActivity(View v) {
        Intent intent = new Intent();
        intent.setClass(this, ItemDecorationActivity.class);
        startActivity(intent);
    }


}
