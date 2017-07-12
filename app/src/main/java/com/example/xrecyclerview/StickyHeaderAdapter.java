package com.example.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.stickheader.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * Created by jianghejie on 15/11/26.
 */
public class StickyHeaderAdapter extends RecyclerView.Adapter<StickyHeaderAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter{
    public ArrayList<String> datas = null;
    public StickyHeaderAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(datas.get(position));
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public Boolean isChange(int position) {
        position -= start;
        if(position == 0){
            return true;
        }
        return datas.get(position).charAt(0) !=  datas.get(position-1).charAt(0);
    }

    private final static int start=1 ;//因为有下来刷新，所以为1..如果有addHeaderView，可能会再加1，没有测啊。
    @Override
    public String getTag(int position) {
        return datas.get(position -start).substring(0,1);
    }


    @Override
    public Boolean isShowHeader(int position) {

        if((position >= start) && position < (datas.size()+1)){
            return true;
        }else {
            return false;
        }
    }





    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
        }
    }
}
