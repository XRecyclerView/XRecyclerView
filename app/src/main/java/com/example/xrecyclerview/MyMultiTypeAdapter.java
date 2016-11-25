package com.example.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jianghejie on 15/11/26.
 */
public class MyMultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<String> datas = null;
    private enum ITEM_TYPE{
        ITEM_TYPE_ONE,
        ITEM_TYPE_TWO
    }
    public MyMultiTypeAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == ITEM_TYPE.ITEM_TYPE_ONE.ordinal()){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image,viewGroup,false);
            return new ItemOneViewHolder(view);
        }else if(viewType == ITEM_TYPE.ITEM_TYPE_TWO.ordinal()){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
            return new ItemTwoViewHolder(view);
        } else
            return null;
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof ItemOneViewHolder){
            ((ItemOneViewHolder)viewHolder).image.setImageResource(R.drawable.headimg);
        }else if(viewHolder instanceof ItemTwoViewHolder){
            ((ItemTwoViewHolder)viewHolder).title.setText(datas.get(position));
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position==0?ITEM_TYPE.ITEM_TYPE_ONE.ordinal():ITEM_TYPE.ITEM_TYPE_TWO.ordinal();
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ItemOneViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public ItemOneViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.img);
        }
    }

    public class ItemTwoViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public ItemTwoViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.text);
        }
    }
}
