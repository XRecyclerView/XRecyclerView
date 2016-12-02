# XRecyclerView
a RecyclerView that implements pullrefresh , loadingmore and header featrues.you can use it like a standard RecyclerView.
you don't need to implement a special adapter .qq 群478803619
Screenshots
-----------
![demo](https://github.com/jianghejie/XRecyclerView/blob/master/art/demo.gif)

on real device it is much more smoother. 
Usage
-----
##gradle
```groovy
compile 'com.jcodecraeer:xrecyclerview:1.3.2'
```
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
mRecyclerView.loadMoreComplete();
```
to notify that the loading more work is done.
and

```java
 mRecyclerView.refreshComplete();
```

to notify that the refreshing work is done.

here is what we get:

![default](https://github.com/jianghejie/XRecyclerView/blob/master/art/default.gif)

##call refresh() manually(I change the previous setRefreshing() method to refresh() )
```java
mRecyclerView.refresh();
```

###custom refresh and loading more style
pull refresh and loading more style is highly customizable.
#### custom loading style
the loading effect we use the  [AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView) . and it is built in(make a little change).
we provide all the effect in AVLoadingIndicatorView library besides we add a system style.
you can call 
```java
mRecyclerView.setRefreshProgressStyle(int style);
```
and 
```java
mRecyclerView.setLaodingMoreProgressStyle(int style);
```
to set the RefreshProgressStyle and  LaodingMoreProgressStyle respectively.

for example
```java
mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
```
![refreshloadingballspinfade](https://github.com/jianghejie/XRecyclerView/blob/master/art/refreshloadingballspinfade.gif)
```java
mRecyclerView.setLaodingMoreProgressStyle(ProgressStyle.SquareSpin);
```
![loadingmoresquarespin](https://github.com/jianghejie/XRecyclerView/blob/master/art/loadingmoresquarespin.gif)


BallPulse  effect

![BallPulse](https://github.com/jianghejie/XRecyclerView/blob/master/art/ballpulse.gif)

all the effect can be get in the ProgressStyle class

```java
public class ProgressStyle {
    public static final int SysProgress=-1;
    public static final int BallPulse=0;
    public static final int BallGridPulse=1;
    public static final int BallClipRotate=2;
    public static final int BallClipRotatePulse=3;
    public static final int SquareSpin=4;
    public static final int BallClipRotateMultiple=5;
    public static final int BallPulseRise=6;
    public static final int BallRotate=7;
    public static final int CubeTransition=8;
    public static final int BallZigZag=9;
    public static final int BallZigZagDeflect=10;
    public static final int BallTrianglePath=11;
    public static final int BallScale=12;
    public static final int LineScale=13;
    public static final int LineScaleParty=14;
    public static final int BallScaleMultiple=15;
    public static final int BallPulseSync=16;
    public static final int BallBeat=17;
    public static final int LineScalePulseOut=18;
    public static final int LineScalePulseOutRapid=19;
    public static final int BallScaleRipple=20;
    public static final int BallScaleRippleMultiple=21;
    public static final int BallSpinFadeLoader=22;
    public static final int LineSpinFadeLoader=23;
    public static final int TriangleSkewSpin=24;
    public static final int Pacman=25;
    public static final int BallGridBeat=26;
    public static final int SemiCircleSpin=27;
}
```
#### refresh arrow icon
we provide a default arrow icon:

![ic_pulltorefresh_arrow](https://github.com/jianghejie/XRecyclerView/blob/master/art/ic_pulltorefresh_arrow.png)

but if you don't like it,you can replace it with any other icon  you want.
just call
```java
mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
```
![customarrow](https://github.com/jianghejie/XRecyclerView/blob/master/art/customarrow.gif)
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
##Header
you can add header to XRecyclerView，just call addHeaderView().

```java
View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
mRecyclerView.addHeaderView(header);
```
if you like ,you can add two header

```java
View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
View header1 =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup)findViewById(android.R.id.content),false);
mRecyclerView.addHeaderView(header);
mRecyclerView.addHeaderView(header1);
```
License
-------

    Copyright 2015 jianghejie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

