package com.example.pj.news_demo.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.pj.news_demo.callback.IFooterCallback;
import com.example.pj.news_demo.callback.IHeaderCallback;
import com.example.pj.news_demo.utils.ViewUtil;
import com.example.pj.news_demo.utils.WrapperViewState;

/**
 * Created by pj on 2016/4/11.
 * 可用来控制内部RecycleView的下拉刷新和加载更多
 * 对于ContentView
 * 暂时先设定为RecycleView
 * 需要梳理清楚的逻辑，也是bug点
 * 1.当我动态调用addView之后，会导致measure和layout的执行
 * 因为这里的父布局是Linearlayout,那么addHeader()之后再重新测量的话
 * 会导致contentView的高度比以前小一个头部的高度
 */
public class WrapperView extends LinearLayout {
    private static final String TAG = "WrapperView";
    private WrapperViewState mWrapperViewState;
    private Scroller mScroller;
    private static final float OFFSET_RADIO = 1.8f;
    private RefreshContentView mContentView;
    /**
     * 把握处理回调的几个点，如刷新开始，刷新结束
     */
    private IHeaderCallback mHeaderCallback;
    private int mCurState;
    private int mContentHeight;
    private int mInitialMotionY;

    public WrapperView(Context context) {
        this(context, null);
    }


    public WrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        initView();
    }

    /**
     * 需要思考的问题：
     * 1.什么时候将头部和尾部加载进去
     */
    private void initView() {
        mWrapperViewState = new WrapperViewState();
        mCurState = mWrapperViewState.STATE_NORMAL;
        mContentView = new RefreshContentView();
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
//                        Log.i(TAG, "---onGlobalLayout---" + getChildCount());
//                        Log.i(TAG, "---onGlobalLayout---" + getChildAt(2).getMeasuredHeight());
                        mContentView.setContentView(getChildAt(1));
                    }
                });
        mScroller = new Scroller(getContext(), new LinearInterpolator());
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 暂时的解决办法
     * 底部的显示问题还没解决
     * 1.先把Footer添加进来,再进一步分析
     */
    private int mFooterHeight;
    private IFooterCallback mIFooterCallback;
    private View mFooterView;

    public void addFooterView(RefreshFooter footerView) {
        if (footerView == null) {
            throw new IllegalArgumentException("FooterView must not be null");
        }
        ViewUtil.removeViewFromParent(footerView);
        mFooterView = footerView;
        Log.i(TAG, "FooterView添加之前的子孩子数量:" + getChildCount());
        addView(footerView, getChildCount());
        if (mFooterView instanceof IFooterCallback) {
            mIFooterCallback = (IFooterCallback) footerView;
        } else {
            throw new IllegalStateException("FooterView must implement IFooterCallback interface");
        }

    }

    /**
     * 改方法还需要改善
     */
    public void addHeaderView(RefreshHeader headerView) {
        if (headerView == null) {
            throw new IllegalArgumentException("FooterView must not be null");
        }
        ViewUtil.removeViewFromParent(headerView);
        mHeaderView = headerView;
        addView(mHeaderView, 0);
        if (mHeaderView instanceof IHeaderCallback) {
            mHeaderCallback = (IHeaderCallback) mHeaderView;
        } else {
            throw new IllegalStateException("HeadView must implement IHeaderCallback interface");
        }
    }

    /**
     * 需要自己处理的事情
     * 1.初始状态下,将头部隐藏起来
     */
    private int mHeaderHeight; //头部高度
    private View mHeaderView;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, r, t, b);
        switch (getChildCount()) {
            case 1:
                super.onLayout(changed, l, t, r, b);
                break;
            case 2:
                if (getChildAt(0) instanceof RefreshHeader) {
                    layoutWithHeader(l, t, r, b);
                }
                break;
            case 3:
//                if (getChildAt(0) instanceof RefreshHeader && getChildAt(2) instanceof RefreshFooter) {
//                }
                layoutWithHeaderAndFooter(l, t, r, b);

                break;
        }
    }

    private void layoutWithHeader(int l, int t, int r, int b) {
        int top = getPaddingTop() + mWrapperViewState.getOffsetY();
        mHeaderHeight = getChildAt(0).getMeasuredHeight();
        Log.i(TAG, "layoutWithHeader------headerHeight:" + mHeaderHeight + "---contentHeight:" + mContentHeight);
        int childCount = getChildCount();
        mContentHeight = getChildAt(1).getMeasuredHeight();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int topMargin = params.topMargin;
            int leftMargin = params.leftMargin;
            int rightMargin = params.rightMargin;
            l = leftMargin;
            top += topMargin;
            r -= rightMargin;
            //考虑孩子的margin,正确的摆放子孩子
            /**
             * 偏移需要考虑的问题
             * 1.头部向上偏移了
             * 2.中间Content向上偏移后，下面会有一个头部高度的坑
             */
            if (i == 0) {  //放置头部
                child.layout(l, top - mHeaderHeight, r, top);
            } else if (i == 1) {  //摆放Content
//                Log.i(TAG, "mContentHeight_Initial:" + mContentHeight_Initial);
                child.layout(l, top, r, top + mHeaderHeight + mContentHeight);
            }
        }
    }

    private void layoutWithHeaderAndFooter(int l, int t, int r, int b) {
        int top = getPaddingTop() + mWrapperViewState.getOffsetY();
        int childCount = getChildCount();
//        Log.i(TAG, "layoutWithHeaderAndFooter:" + childCount);
        mHeaderHeight = getChildAt(0).getMeasuredHeight();
        mContentHeight = getChildAt(1).getMeasuredHeight();
        mFooterHeight = getChildAt(2).getMeasuredHeight();
//        Log.i(TAG, "mHeaderHeight:" + mHeaderHeight + "---mContentHeight:" + mContentHeight + "---mFooterHeight:" + mFooterHeight);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int topMargin = params.topMargin;
            int leftMargin = params.leftMargin;
            int rightMargin = params.rightMargin;
            l = leftMargin;
            top += topMargin;
            r -= rightMargin;
            //考虑孩子的margin,正确的摆放子孩子
            /**
             * 偏移需要考虑的问题
             * 1.头部向上偏移了
             * 2.中间Content向上偏移后，下面会有一个头部高度的坑
             */
            if (i == 0) {  //放置头部
                child.layout(l, top - mHeaderHeight, r, top);
            } else if (i == 1) {  //摆放Content
                child.layout(l, top, r, top + mHeaderHeight + mContentHeight);
                top += mHeaderHeight + mContentHeight;
//                Log.i(TAG, "------放置内容------" + child.getClass().getCanonicalName());
            } else if (i == 2) {
//                Log.i(TAG, "------放置尾部------" + child.getClass().getCanonicalName());
                child.layout(l, top, r, top + mFooterHeight);
            }
        }
    }

    /**
     * 首先要明白为什么要重写这个方法
     * 1.如果他的内部仅仅是一个RecycleView,那自然不必重写，滑动事件全部交给他就行
     * 2.但是现在多了一个HeadView，我们需要根据头部的状态来选择究竟是否将触摸事件
     * 消费掉还是分发掉
     */
    int mLastY;
    private boolean isIntercepted = false;
    private int mTouchSlop;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getRawY();
                mInitialMotionY = mLastY;
                break;
            case MotionEvent.ACTION_MOVE:
                int curY = (int) ev.getRawY();
                int disY = curY - mLastY;  //每次移动和的距离
                mLastY = curY;
                /**
                 * 计算出下拉距离
                 * 如果一切都正常的话,那么改变头部的高度
                 * 关于头部正在刷新时，究竟怎么处理滑动事件
                 * 这个是可以灵活处理的
                 */
                if (mCurState == mWrapperViewState.STATE_REFRESHING) { //处于正在刷新状态，直接消费掉事件
                    return true;
                }
                /**
                 * 这里可以作为优化部分
                 * 使得用户的滑动体验更好
                 * 这里的mTouchSlop为24
                 */
                if (Math.abs(disY) < mTouchSlop / 12) {
                    return true;
                }
                disY = (int) (disY / OFFSET_RADIO);
                /**
                 * 如果仅仅是考虑大于0的情况，那么就无法实现回退
                 * 当用户向上滑动的时候,滑动事件会交给子孩子，即RecycleView
                 * 1.如果在初始情况下，即头部完全隐藏的情况下，用户上滑
                 *       那么该事件应该交给RecycleView
                 * 2.当我们不是在顶部向上滑时，应该讲滑动事件交给RecycleView
                 *
                 * 整理:
                 *  有序的嵌套,外层分为上滑和下滑
                 */
//                Log.i(TAG, "mWrapperViewState.isFootVisible():" + mWrapperViewState.isFootVisible());

                Log.i(TAG, "disY:" + disY + "---" + "mTouchSlop:" + mTouchSlop);
                if (disY > 0) {   //下滑
                    Log.i(TAG, "ScrollY:" + getScrollY());
                    if (!mContentView.canScrollDown() && null != mHeaderCallback) {
                        updateHeaderHeight(disY);
                        return true;
                    } else if (mWrapperViewState.isFootVisible()) {
                        updateFooterHeight(disY);
                        return true;
                    } else {
                        return super.dispatchTouchEvent(ev);
                    }
                } else if (disY < 0) { //上滑
                    if (mContentView.canScrollUp() && mWrapperViewState.isHeadVisible()) {
                        updateHeaderHeight(disY);
                        return true;
                    } else if (!mContentView.canScrollUp() && !mWrapperViewState.isFooterTotallyVisible()) {
                        updateFooterHeight(disY);
                        return true;
                    } else {
                        return super.dispatchTouchEvent(ev);
                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: //当我放手之后,如果有必要的话，做一个回滚动画、
                /**
                 * 1.下拉高度不够
                 * 2.下拉高度够,则处理刷新状态
                 */
                if (mWrapperViewState.isHasMovedDownVertically() && null != mHeaderCallback) {  //被下拉了
                    if (-getScrollY() > mHeaderHeight) {  //超过了头部
                        //处理回调，更新状态
                        mCurState = mWrapperViewState.STATE_REFRESHING;
                        mHeaderCallback.onRefreshing();
                        mListener.onRefreshing();
                    }
                    resetHeaderHeight();
                    /**
                     * 如果不消费掉该事件
                     * 那么该事件会被分发给RecycleView，然后被当成一个点击事件处理
                     */
                    return true;
                }
                if (mWrapperViewState.isHasMovedUpVertically()) { //被上拉了
                    //根据上拉的距离改变状态，继而根据状态来判断究竟执行哪些动作
//                    return true;
                }
                isIntercepted = false;

                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    private static final int SCROLL_DURATION = 300; // scroll back duration

    /**
     * 当用户手释放后，重置头部的高度
     * 1.下拉的高度不够头部的高度
     * 2.超过了头部高度
     * 这里将判断依据统一起来，以WrapperView的状态作为标准
     */
    private void resetHeaderHeight() {
        if (getScrollY() == 0) { //头部已经完全隐藏
            mCurState = mWrapperViewState.STATE_NORMAL;
            return;
        }

        int roll_DisY = 0; //在Y轴上的回滚距离
        if (mCurState == mWrapperViewState.STATE_REFRESHING) { //刷新状态,下拉距离超过了头部高度
            roll_DisY = mHeaderHeight + getScrollY();
        } else if (mCurState == mWrapperViewState.STATE_NORMAL) { //正常状态，下拉距离没有达到头部的高度
            roll_DisY = getScrollY();
        }
        Log.i(TAG, "roll_DisY:" + roll_DisY + "---" + "scrollY:" + getScrollY());
        startScroll(roll_DisY, SCROLL_DURATION);
    }

    /**
     * @param roll_disY      小于0则向上滑动
     * @param scrollDuration
     */
    private void startScroll(int roll_disY, int scrollDuration) {
        if (roll_disY != 0) {
            mScroller.startScroll(0, getScrollY(), 0, -roll_disY, scrollDuration);
            invalidate();  //结合内核剖析这本书分析一下这一方法的流程
        }

    }

    private void updateFooterHeight(int disY) {
        moveView(disY);
        if (mWrapperViewState.getOffsetY() < -mFooterHeight) {
            mWrapperViewState.setIsFooterTotallyVisible(true);
        } else {
            mWrapperViewState.setIsFooterTotallyVisible(false);
        }
    }

    /**
     * 在该方法中需要回调头部状态的回调接口
     */
    private void updateHeaderHeight(int disY) {
        moveView(disY);
        if (mWrapperViewState.getOffsetY() > mHeaderHeight && null != mHeaderCallback) {
            mHeaderCallback.onRefreshReady();
            mCurState = mWrapperViewState.STATE_REFRESH_READY;
        } else if (mWrapperViewState.getOffsetY() < mHeaderHeight && null != mHeaderCallback) {
            mHeaderCallback.onStateNormal();
            mCurState = mWrapperViewState.STATE_NORMAL;
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            /**
             * 真正的移动操作是在这个方法中完成的，自己完成
             * 存在的问题:
             *  1.在向上做动画的时候，底部总是会少一块
             */
//            int currScrollY = getScrollY();
//            int offsetY = currScrollY - mWrapperViewState.getOffsetY();
//            moveView(offsetY);
            Log.i(TAG, "currY:" + mScroller.getCurrY());
//            scrollTo(0, mScroller.getCurrY());

        } else {
            //处理滚动完成之后的状态
//            Log.i(TAG, "finalY:" + mScroller.getCurrY());
        }


    }

    private void moveView(int offsetY) {
        scrollBy(0, -offsetY);
        mWrapperViewState.setOffsetY(offsetY);//改变WrapperView状态
//        Log.i(TAG, "------" + -offsetY);
        ViewCompat.postInvalidateOnAnimation(this);
        /**
         * 当我移动的时候，需要更新状态，处理回调接口
         */

    }

    private static Handler mHandler = new Handler();
    private int mPinnedTime;//刷新完成之后头部停留的时间

    /**
     * 回调接口，暴露给外部控制类刷新完成之后进行回调
     * 此时将头部回滚,直到完全隐藏
     */
    public void stopRefresh() {
        if (mCurState == mWrapperViewState.STATE_REFRESHING) {
            /**
             *更新该控件的各种状态,处理回调
             * 1.比如，头部的状态需要更新,那么，我只要定义一个头部状态的回调接口
             * 我调用接口的方法，具体的实现由具体的类去做，这样就是很好的面向接口
             * 编程了
             */
            mCurState = mWrapperViewState.STATE_REFRESH_COMPLETED;
            mHeaderCallback.onRefreshCompleted();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetHeaderHeight();
                }
            }, 1000);

        }

    }

    private OnWrapperViewStateListener mListener;

    public void setRefreshStateListener(OnWrapperViewStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listen must not be null");
        }
        this.mListener = listener;
    }

    public interface OnWrapperViewStateListener {
        void onRefreshing();
    }

}
