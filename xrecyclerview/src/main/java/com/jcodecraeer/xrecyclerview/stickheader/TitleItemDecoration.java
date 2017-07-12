package com.jcodecraeer.xrecyclerview.stickheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 有分类title的 ItemDecoration
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class TitleItemDecoration extends RecyclerView.ItemDecoration {
    private StickyRecyclerHeadersAdapter mAdapter;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private LayoutInflater mInflater;

    private int mTitleHeight;//title的高
    private static int COLOR_TITLE_BG = Color.parseColor("#FFDFDFDF");
    private static int COLOR_TITLE_FONT = Color.parseColor("#FF000000");
    private static int mTitleFontSize;//title字体大小


    public TitleItemDecoration(Context context, StickyRecyclerHeadersAdapter adapter) {
        super();
        mAdapter = adapter;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (mAdapter.isShowHeader(position) && mAdapter.isChange(position)) {
                //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                drawTitleArea(c, left, right, child, params, position);
            } else {
                //none
            }

        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     *
     * @param c
     * @param left
     * @param right
     * @param child
     * @param params
     * @param position
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
/*
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;*/

        mPaint.getTextBounds(mAdapter.getTag(position), 0, mAdapter.getTag(position).length(), mBounds);
        c.drawText(mAdapter.getTag(position), child.getPaddingLeft(), child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, final RecyclerView parent, RecyclerView.State state) {//最后调用 绘制在最上层
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();

        if(!mAdapter.isShowHeader(pos)) return;

        String tag = mAdapter.getTag(pos);
        //View child = parent.getChildAt(pos);
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;//出现一个奇怪的bug，有时候child为空，所以将 child = parent.getChildAt(i)。-》 parent.findViewHolderForLayoutPosition(pos).itemView

        boolean flag = false;//定义一个flag，Canvas是否位移过的标志
        if (mAdapter.isShowHeader(pos+1)) {//防止数组越界（一般情况不会出现）
            if (null != tag && !tag.equals(mAdapter.getTag(pos + 1))) {//当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
                Log.d("zxt", "onDrawOver() called with: c = [" + child.getTop());//当getTop开始变负，它的绝对值，是第一个可见的Item移出屏幕的距离，
                if (child.getHeight() + child.getTop() < mTitleHeight) {//当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，我们也该开始做悬浮Title的“交换动画”
                    c.save();//每次绘制前 保存当前Canvas状态，
                    flag = true;

                    //一种头部折叠起来的视效，个人觉得也还不错~
                    //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() < mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
                    //c.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + child.getHeight() + child.getTop());

                    //类似饿了么点餐时,商品列表的悬停头部切换“动画效果”
                    //上滑时，将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft(),
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        if (flag)
            c.restore();//恢复画布到之前保存的状态


/*        Button button = new Button(parent.getContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parent.getContext(), "啊哈", Toast.LENGTH_SHORT).show();//即使给View设置了点击事件，也是无效的，它仅仅draw了
            }
        });
        ViewGroup.LayoutParams params = button.getLayoutParams();
        if (params == null){
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        button.setLayoutParams(params);
        button.setBackgroundColor(Color.RED);
        button.setText("无哈");
        *//*button.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.EXACTLY));
*//*
        //必须经过 测量 和 布局，View才能被正常的显示出来
        button.measure(View.MeasureSpec.makeMeasureSpec(9999,View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(9999,View.MeasureSpec.UNSPECIFIED));
        button.layout(parent.getPaddingLeft(),parent.getPaddingTop(),
                parent.getPaddingLeft()+button.getMeasuredWidth(),parent.getPaddingTop()+button.getMeasuredHeight());
        button.draw(c);*/

        //inflate一个复杂布局 并draw出来
/*        View toDrawView = mInflater.inflate(R.layout.header_complex, parent, false);
        int toDrawWidthSpec;//用于测量的widthMeasureSpec
        int toDrawHeightSpec;//用于测量的heightMeasureSpec
        //拿到复杂布局的LayoutParams，如果为空，就new一个。
        // 后面需要根据这个lp 构建toDrawWidthSpec，toDrawHeightSpec
        ViewGroup.LayoutParams lp = toDrawView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//这里是根据复杂布局layout的width height，new一个Lp
            toDrawView.setLayoutParams(lp);
        }
        if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);
        } else {
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        }
        //高度同理
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
        } else {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        }
        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        toDrawView.measure(toDrawWidthSpec, toDrawHeightSpec);
        toDrawView.layout(parent.getPaddingLeft(), parent.getPaddingTop(),
                parent.getPaddingLeft() + toDrawView.getMeasuredWidth(), parent.getPaddingTop() + toDrawView.getMeasuredHeight());
        toDrawView.draw(c);*/

    }

/*    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        super.getItemOffsets(outRect, itemPosition, parent);
    }*/

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
        if (position < 0) return;
        if (!mAdapter.isShowHeader(position)) {
            outRect.set(0, 0, 0, 0);
        } else {//其他的通过判断
            Log.e("xiayu", "position");
            if (mAdapter.isChange(position)) {
                outRect.set(0, mTitleHeight, 0, 0);//不为空 且跟前一个tag不一样了，说明是新的分类，也要title
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }

    }


}
