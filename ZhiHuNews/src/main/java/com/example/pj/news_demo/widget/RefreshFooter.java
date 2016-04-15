package com.example.pj.news_demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.callback.IFooterCallback;

/**
 * Created by pj on 2016/4/13.
 */
public class RefreshFooter extends LinearLayout implements IFooterCallback {
    private View mContentView;
    private Context mContext;

    public RefreshFooter(Context context) {
        this(context, null);
    }

    public RefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.footer_refresh, this);
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoadingCompleted() {

    }

    @Override
    public void onLoadingFailed() {

    }

    @Override
    public void onLoadingReady() {

    }
}
