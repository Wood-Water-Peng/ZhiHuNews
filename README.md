#概论  
参考了Github上的一个关于RecycleView的开源库，自己封装了下拉头部和尾部，增加了文件缓存功能 <br/>
##说明
开始折腾这个库主要是为了RecycleView的使用，后面发现它缺少了点东西。</br>
虽然下拉刷新和上拉加载已经有很多成熟的案例了，但是自己再重新造一个轮子可以帮助自己更好的理解这一过程，特别是自己对细节的处理，会提高自己的水平</br>
直接借用别人的开源库固然没有什么大问题，可以使用在真实的项目上，但是我们还是需要一个SideProject来帮助自己查漏补缺，不是吗？</br>
##思路
* 对于下拉头和上拉尾我们都是可以自定义的，暂时接受填充后的View，而不接收布局文件。
* 我们外层嵌套一个WrapView，内部包含三个控件
    * RefreshHeader
    * RefreshContentView  这里是布局文件中的RecycleView
    * RefreshFooter
* 修改测量方法</br>
  不管用户是否添加下拉头或尾，我们在初次显示时都需要全屏显示RefreshContentView
* 修改布局方法</br>
  根据不同的子孩子数，我们使用不同的布局策略，初始时要把头部隐藏起来
* DiskLru进行缓存  

##相关代码
```java
  mWrapperView.addHeaderView(new RefreshHeader(getContext()));
        mWrapperView.addFooterView(new RefreshFooter(getContext()));
        mWrapperView.setRefreshStateListener(new WrapperView.OnWrapperViewStateListener() {
            @Override
            public void onRefreshing() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWrapperView.stopRefresh();
                    }
                }, 1000);
            }

            @Override
            public void onLoadingMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWrapperView.stopLoading();
                    }
                }, 1000);
            }
        });
```

##使用
对于AndroidStudio，建议以module的方式导入，然后添加依赖
注意权限：
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```


