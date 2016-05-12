package com.example.pj.news_demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.callback.IFooterCallback;

/**
 * Created by pj on 2016/4/13.
 * 两种模式
 * 1.松开加载更多
 * 2.点击加载更多
 */
public class RefreshFooter extends LinearLayout implements IFooterCallback {
    private static final String TAG = "RefreshFooter";
    private View mLoadingView;
    private Context mContext;
    private TextView mClickView;
    private boolean showing = false;
    private LOAD_MODE mLoadMode = LOAD_MODE.RELEASE_LOAD; //默认值

    public enum LOAD_MODE {
        RELEASE_LOAD, CLICK_LOAD
    }

    public RefreshFooter(Context context) {
        this(context, null);
    }

    public void setLoadMode(LOAD_MODE loadMode) {
        mLoadMode = loadMode;
    }

    public RefreshFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int new_heightMode = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED);
        super.onMeasure(widthMeasureSpec, new_heightMode);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.footer_refresh, this);
        mLoadingView = findViewById(R.id.refreshview_footer_loading);
        mClickView = (TextView) findViewById(R.id.refreshview_footer_click_textview);
    }

    @Override
    public void onLoading() {
        mLoadingView.setVisibility(VISIBLE);
        mClickView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCompleted() {
        mLoadingView.setVisibility(GONE);
        mClickView.setText(R.string.refreshview_header_hint_loaded);
        mClickView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed() {
        mLoadingView.setVisibility(GONE);
        mClickView.setVisibility(View.VISIBLE);
        mClickView.setText(R.string.refreshview_footer_hint_failed);
    }

    @Override
    public void onLoadingReady() {
        mLoadingView.setVisibility(GONE);
        mClickView.setVisibility(View.VISIBLE);
        mClickView.setText(R.string.refreshview_footer_hint_ready);
    }

    @Override
    public void onLoadingNormal() {
        mLoadingView.setVisibility(GONE);
        mClickView.setVisibility(View.VISIBLE);
        mClickView.setText(R.string.refreshview_footer_hint_normal);
    }
}
