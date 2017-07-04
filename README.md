# XRecyclerView

A RecyclerView that implements `pullrefresh`, `loadingmore` and `header` features. You can use it like a standard RecyclerView.
You don't need to implement a special adapter. 

qq 群478803619

# Table of Content

* [Screenshots](#screenshots)
* [Usage](#usage)
  * [Gradle](#gradle)
* [Features](#features)
  * [Pull to Refresh and Load More](#pull-to-refresh-and-load-more)
  * [Call Refresh Manually](#call-refresh()-manually)
  * [Custom Refresh and Loading More Style](#custom-refresh-and-loading-more-style)
  * [Disable Refresh and Load More Feature](#disable-refresh-and-load-more-feature)
  * [Header](#header)
* [License](#license)

## Screenshots

![demo](https://github.com/jianghejie/XRecyclerView/blob/master/art/demo.gif)

On real device it is much more smoother. 

## Usage

### Gradle

```groovy
compile 'com.jcodecraeer:xrecyclerview:1.3.2'
```

Just like a standard RecyclerView

```java
LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
mRecyclerView.setLayoutManager(layoutManager);
mRecyclerView.setAdapter(mAdapter);
```

## Features

### Pull to Refresh and Load More

The pull to refresh and load more feature is enabled by default. We provide a callback to trigger the refresh and LoadMore event.

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

to notify that the loading more work is done. And

```java
 mRecyclerView.refreshComplete();
```

to notify that the refreshing work is done.

Here is what we get:

![default](https://github.com/jianghejie/XRecyclerView/blob/master/art/default.gif)

### Call refresh() manually

(I change the previous setRefreshing() method to refresh())

```java
mRecyclerView.refresh();
```

### Custom Refresh and Loading More Style

Pull refresh and loading more style is highly customizable.

#### Custom Loading Style

The loading effect we use the  [AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView) and it is built in (make a little change). We provide all the effect in AVLoadingIndicatorView library besides we add a system style. You can call 

```java
mRecyclerView.setRefreshProgressStyle(int style);
```

and 

```java
mRecyclerView.setLoadingMoreProgressStyle(int style);
```

to set the `RefreshProgressStyle` and `LoadingMoreProgressStyle` respectively.

#### Style Example

##### Ball Spin

```java
mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
```

![refreshloadingballspinfade](https://github.com/jianghejie/XRecyclerView/blob/master/art/refreshloadingballspinfade.gif)

##### Square Spin

```java
mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
```

![loadingmoresquarespin](https://github.com/jianghejie/XRecyclerView/blob/master/art/loadingmoresquarespin.gif)

##### BallPulse effect

![BallPulse](https://github.com/jianghejie/XRecyclerView/blob/master/art/ballpulse.gif)

#### All the Effect

All the effect can be get in the ProgressStyle class.

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

#### Refresh Arrow Icon

We provide a default arrow icon:

![ic_pulltorefresh_arrow](https://github.com/jianghejie/XRecyclerView/blob/master/art/ic_pulltorefresh_arrow.png)

but if you don't like it, you can replace it with any other icon you want. Just call

```java
mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
```

![customarrow](https://github.com/jianghejie/XRecyclerView/blob/master/art/customarrow.gif)

### Disable Refresh and Load More Feature

If you don't want the refresh and load more feature (in that case, you probably don't need the lib neither), you can call

```java
mRecyclerView.setPullRefreshEnabled(false);
```

and

```java
mRecyclerView.setPullRefreshEnabled(true);
```

in which `false` means disabled, `true` means enabled.

## Header

You can add header to `XRecyclerView`，just call addHeaderView().

```java
View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
mRecyclerView.addHeaderView(header);
```

If you like, you can add two header

```java
View header =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
View header1 =   LayoutInflater.from(this).inflate(R.layout.recyclerview_header1, (ViewGroup)findViewById(android.R.id.content),false);
mRecyclerView.addHeaderView(header);
mRecyclerView.addHeaderView(header1);
```

# License

```
    Copyright 2017 jianghejie

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
