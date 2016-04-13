package com.example.pj.news_demo.callback;

/**
 * Created by pj on 2016/4/12.
 * 下拉刷新头部回调接口
 * 实现该接口的类有责任更改头部的状态
 */
public interface IHeaderCallback {
    void onRefreshStart();  //开始刷新

    void onRefreshing(); //刷新中

    void onRefreshCompleted(); //刷新完成

    void onRefreshFailed(); //刷新失败

    void updateRefreshDate();  //更新刷新时间

    void onRefreshReady();  //准备刷新

    void onStateNormal();  //正常状态


}
