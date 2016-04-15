package com.example.pj.news_demo.callback;

/**
 * Created by pj on 2016/4/14.
 */
public interface IFooterCallback {
    void onLoading();

    void onLoadingCompleted();

    void onLoadingFailed();

    void onLoadingReady();
}
