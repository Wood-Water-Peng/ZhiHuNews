package com.example.pj.news_demo.utils;

/**
 * Created by pj on 2016/4/12.
 * 该类专门用来记录WrapperView的状态
 */
public class WrapperViewState {

    private int offsetY;//Y轴偏移量
    private boolean hasMovedDownVertically;//判断在Y轴上是否下拉
    private boolean hasMovedUpVertically;//判断在Y轴上是否上移
    private boolean hasOffsetYBeyondHeader;//Y轴的下拉偏移是否超过了头部高度

    public void setOffsetY(int disY) {
        offsetY += disY;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public boolean isHasMovedDownVertically() {
        return offsetY > 0;
    }

    public boolean isHasMovedUpVertically() {
        return offsetY < 0;
    }

    public boolean isHasOffsetYBeyondHeader() {
        return hasOffsetYBeyondHeader;
    }

    public static final int STATE_NORMAL = 100; //正常状态
    public static final int STATE_REFRESHING = 101;//头部正在状态
    public static final int STATE_REFRESH_COMPLETED = 102;//头部刷新完成状态
    public static final int STATE_REFRESH_READY = 103;//头部准备刷新状态
    private boolean isHeadVisible = false;  //头部是否可见

    public boolean isHeadVisible() {
        return isHeadVisible;
    }

    public void setIsHeadVisible(boolean isHeadVisible) {
        this.isHeadVisible = isHeadVisible;
    }

    private boolean isFooterTotallyVisible;

    public boolean isFooterTotallyVisible() {
        return isFooterTotallyVisible;
    }

    public void setIsFooterTotallyVisible(boolean isFooterTotallyVisible) {
        this.isFooterTotallyVisible = isFooterTotallyVisible;
    }
}
