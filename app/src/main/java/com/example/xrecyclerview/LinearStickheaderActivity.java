package com.example.xrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.stickheader.TitleItemDecoration;

import java.util.ArrayList;

public class LinearStickheaderActivity extends AppCompatActivity {
    private XRecyclerView mRecyclerView;
    private StickyHeaderAdapter mAdapter;
    private ArrayList<String> listData;
    private int refreshTime = 0;
    private int times = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (XRecyclerView)this.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);

      //  View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
     //   mRecyclerView.addHeaderView(header);


        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                refreshTime ++;
                times = 0;
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        listData.clear();
                        for(int i = 0; i < 3 ;i++){
                            listData.add("a item" + i + "after " + refreshTime + " times of refresh");
                        }
                        for(int i = 3; i < 7 ;i++){
                            listData.add("b item" + i + "after " + refreshTime + " times of refresh");
                        }
                        for(int i = 7; i < 15 ;i++){
                            listData.add("c item" + i + "after " + refreshTime + " times of refresh");
                        }

                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }

                }, 1000);            //refresh data here
            }

            @Override
            public void onLoadMore() {
                if(times < 1){
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            for(int i = 0; i < 4 ;i++){
                                listData.add("d item" + (1 + listData.size() ) );
                            }
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for(int i = 0; i < 4 ;i++){
                                listData.add("e item" + (1 + listData.size() ) );
                            }
                            mRecyclerView.setNoMore(true);
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                }
                times ++;
            }
        });

        listData = new  ArrayList<String>();
        mAdapter = new StickyHeaderAdapter(listData);
        TitleItemDecoration titleItemDecoration = new TitleItemDecoration(this,mAdapter);
        mRecyclerView.addItemDecoration(titleItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
