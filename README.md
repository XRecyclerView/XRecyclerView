# XRecyclerView
a RecyclerView that implements pullrefresh and loadingmore featrues.you can use it like a standard RecyclerView.
you don't need to implement a special adapter .
Screenshots
-----------

Usage
-----
just like a standard RecyclerView
```java
LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
mRecyclerView.setLayoutManager(layoutManager);
mRecyclerView.setAdapter(mAdapter);
```
##pull to refresh and load more
the pull to refresh and load more featrue is enabled by default. we provide a callback to trigger the refresh and LoadMore event.
```java
 mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
    @Override
    public void onRefresh() {
       //refresh data here
    }

    @Override
    public void onLoadMore() {
       // load more data here
    }
});
```
and of course you have to tell our RecyclerView when the refreshing or loading more work is done.
you can use
```java
mRecyclerView.loadMoreComplate();
```
to notify that the loading more work is done.
and

```java
 mRecyclerView.refreshComplate();
```

to notify that the refreshing work is done.

here is what we get:

![default](https://github.com/jianghejie/XRecyclerView/blob/master/art/default.gif)

###custom refresh and loading more style
pull refresh and loading more style is highly customizable.
#### custom loading style
the loading effect we use the AVLoadingIndicatorView. and it is built in(make a little change).
we provide all the effect in AVLoadingIndicatorView library besides we add a system style.
you can call 
```java
mRecyclerView.setRefreshProgressStyle(ProgressStyle.SysProgress);
```
and 
```java
mRecyclerView.setLaodingMoreProgressStyle(ProgressStyle.BallClipRotate);
```
to set the RefreshProgressStyle and  LaodingMoreProgressStyle respectively.
#### refresh arrow icon
we provide a default arrow icon:

but if you don't like it,you can replace it to any other icon  you want.
just call
```java
mRecyclerView.setArrowImageView(R.drawable.iconfontarrowdown);
```

###disable refresh and load more featrue
if you don't want the refresh and load more featrue(in that case,you probably dont'n need the lib neither),you can call
```java
mRecyclerView.setPullRefreshEnabled(false);
```
and
```java
mRecyclerView.setPullRefreshEnabled(true);
```
in which false means disabled ,true means enabled.
