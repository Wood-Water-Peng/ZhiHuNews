package com.example.pj.news_demo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.callback.IHeaderCallback;
import com.example.pj.news_demo.utils.StringUtil;

import java.util.Calendar;

/**
 * Created by pj on 2016/4/12.
 * 这是我们自定义的RefreshHeader，用户可以自定义刷新头部，但是必须
 * 实现IHeaderCallback这个接口
 */
public class RefreshHeader extends LinearLayout implements IHeaderCallback {

    private ImageView mArrowImageView;
    private ImageView mOkImageView;
    private ProgressBar mProgressBar;
    private TextView mHintTextView;
    private TextView mHeaderTimeTextView;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;

    public RefreshHeader(Context context) {
        this(context, null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAnimation();
    }

    private void initAnimation() {
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.head_refresh, this);
        mArrowImageView = (ImageView) findViewById(R.id.header_arrow);
        mOkImageView = (ImageView) findViewById(R.id.refreshview_header_ok);
        mHintTextView = (TextView) findViewById(R.id.refreshview_header_hint_textview);
        mHeaderTimeTextView = (TextView) findViewById(R.id.refreshview_header_time);
        mProgressBar = (ProgressBar) findViewById(R.id.header_progressbar);
    }

    /**
     * 更新刷新的时间
     */
    public void updateRefreshTime(long lastRefreshTime) {
        //与当前时间做对比
        Calendar cal = Calendar.getInstance();
        long time_interval = cal.getTimeInMillis() - lastRefreshTime;
        //换算成分钟
        int minutes = (int) (time_interval / 1000 / 60);
        String refreshTimeText;
        Resources resources = getContext().getResources();
        if (minutes < 1) {
            refreshTimeText = resources.getString(R.string.refreshview_refresh_justnow);
        } else if (minutes < 60) {
            refreshTimeText = StringUtil.format(resources.getString(R.string.refreshview_refresh_minuites_ago), minutes);
        } else if (minutes < 60 * 24) {
            refreshTimeText = StringUtil.format(resources.getString(R.string.refreshview_refresh_hours_ago), minutes / 60);
        } else {
            refreshTimeText = StringUtil.format(resources.getString(R.string.refreshview_refresh_days_ago), minutes / 60 / 24);
        }
        mHeaderTimeTextView.setText(refreshTimeText);
    }

    @Override
    public void onRefreshStart() {

    }

    @Override
    public void onRefreshing() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.GONE);
        mOkImageView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mHintTextView.setText(R.string.refreshview_header_hint_loading);
    }

    @Override
    public void onRefreshCompleted() {
        mArrowImageView.setVisibility(View.GONE);
        mOkImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mHintTextView.setText(R.string.refreshview_header_hint_loaded);
        mHeaderTimeTextView.setVisibility(View.GONE);
    }

    @Override
    public void onRefreshFailed() {

    }

    @Override
    public void updateRefreshDate() {

    }

    @Override
    public void onRefreshReady() {
        mProgressBar.setVisibility(View.GONE);
        mOkImageView.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        mHintTextView.setText(R.string.refreshview_header_hint_ready);
        mHeaderTimeTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStateNormal() {
        mProgressBar.setVisibility(View.GONE);
        mArrowImageView.setVisibility(View.VISIBLE);
        mOkImageView.setVisibility(View.GONE);
        mArrowImageView.startAnimation(mRotateDownAnim);
        mHintTextView.setText(R.string.refreshview_header_hint_normal);
    }
}
