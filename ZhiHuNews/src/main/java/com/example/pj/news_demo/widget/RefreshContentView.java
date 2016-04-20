package com.example.pj.news_demo.widget;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by pj on 2016/4/13.
 * 作为内部ContentView的代理类
 */
public class RefreshContentView {
    /**
     * 迫切需要解决的一个问题
     * 1.当前的contentView是否处于顶部，是否可以下滑或者上滑
     */
    private View mContentView;

    public void setContentView(View contentView) {
        if (contentView == null) {
            throw new IllegalArgumentException("contentView must not be null");
        }
        this.mContentView = contentView;
    }

    public boolean canScrollUp() {
        /**
         * contentView暂时只考虑RecycleView
         */
        return ViewCompat.canScrollVertically(mContentView, 1);
    }

    /**
     * 判断RecycleView是否在顶部
     * 1.
     */
    public boolean isAtTop() {
        if (mContentView instanceof RecyclerView) {
            RecyclerView contentView = (RecyclerView) mContentView;
            RecyclerView.LayoutManager layoutManager = contentView.getLayoutManager();
            int dis = layoutManager.getChildAt(0).getTop() - layoutManager.getPaddingTop();
            if (dis == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void offsetTopAndBottom(int offsetY) {
        mContentView.offsetTopAndBottom(offsetY);
    }

    public void invalidate() {
        mContentView.invalidate();
    }

    public boolean isAtBottom() {
        if (mContentView instanceof RecyclerView) {
            RecyclerView contentView = (RecyclerView) mContentView;
            RecyclerView.LayoutManager layoutManager = contentView.getLayoutManager();
            int dis = layoutManager.getChildAt(layoutManager.getChildCount() - 1).getBottom();
            if (dis == contentView.getMeasuredHeight()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean canScrollDown() {
        return ViewCompat.canScrollVertically(mContentView, -1);
    }
}
