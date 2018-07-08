# XRecyclerView
please refer to [XRecyclerView](https://github.com/XRecyclerView/XRecyclerView) for a full wiki

Usage of this custom fork which fixes some issues in the original library
-----
## gradle
# in build.gradle project module
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
# in build.gradle app module
```groovy
	implementation 'com.github.beshoy-samy:XRecyclerView:custom.v1'
```
# GridLayoutSpanPositionListener
if you want to have custom span count for some positions 
```java
mRecyclerView.setGridLayoutSpanPositionListener(GridLayoutSpanPositionListener gridLayoutSpanPositionListener)
```

# header and footer colors
You can use these methods to just control the refresh header and loadMoreFooter icons and text color
for refresh header view
```java
mRecyclerView.setRefreshHeaderTxtColor(int color);
mRecyclerView.setRefreshHeaderArrowFilterColor(int color);
```
for loadMore footer view
```java
mRecyclerView.setLoadMoreFooterTxtColor(int color);
mRecyclerView.setLoadMoreFooterProgressIndicatorColor(int color);
```
