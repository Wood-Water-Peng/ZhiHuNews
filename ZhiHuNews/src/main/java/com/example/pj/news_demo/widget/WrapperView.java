package com.example.pj.news_demo.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
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
        mCurState = mWrapperViewState.STATE_REFRESH_NORMAL;
        mContentView = new RefreshContentView();
        this.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        //考虑没有header和footer的情况
                        if (getChildCount() > 1) {
                            mContentView.setContentView(getChildAt(1));
                        } else {
                            mContentView.setContentView(getChildAt(0));
                        }
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
    private IFooterCallback mFooterCallback;
    private View mFooterView;

    public void addFooterView(RefreshFooter footerView) {
        if (footerView == null) {
            throw new IllegalArgumentException("FooterView must not be null");
        }
        ViewUtil.removeViewFromParent(footerView);
        mFooterView = footerView;
//        Log.i(TAG, "FooterView添加之前的子孩子数量:" + getChildCount());
        addView(footerView, getChildCount());
        if (mFooterView instanceof IFooterCallback) {
            mFooterCallback = (IFooterCallback) footerView;
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int widthSpec_head = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int heightSpec_head = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);

        int heightSpec_content = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        int widthSpec_content = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);

        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof RefreshHeader) {  //头部要多高给多高
                view.measure(widthSpec_head, heightSpec_head);
                mHeaderHeight = view.getMeasuredHeight();
            } else if (view instanceof RecyclerView) {  //中间内容和父类一样高
                view.measure(widthSpec_content, heightSpec_content);
            } else if (view instanceof RefreshFooter) { //尾部要多高给多高
                view.measure(widthSpec_head, heightSpec_head);
                mFooterHeight = view.getMeasuredHeight();
            }
//            Log.i(TAG, "宽------:" + view.getMeasuredWidth() + "高------:" + view.getMeasuredHeight());
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
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            int topMargin = params.topMargin;
            int leftMargin = params.leftMargin;
            int rightMargin = params.rightMargin;
            l = leftMargin;
            t += topMargin;
            r -= rightMargin;
            //考虑孩子的margin,正确的摆放子孩子
            /**
             * 偏移需要考虑的问题
             * 1.头部向上偏移了
             * 2.中间Content向上偏移后，下面会有一个头部高度的坑
             */
            if (child instanceof RefreshHeader) {
                child.layout(l, t - child.getMeasuredHeight(), r, t);
            } else if (child instanceof RecyclerView) {
                child.layout(l, t, r, t + child.getMeasuredHeight());
            } else if (child instanceof RefreshFooter) {
//                Log.i(TAG, "父亲的高度:" + getMeasuredHeight());
                child.layout(l, t + getMeasuredHeight(), r, t + getMeasuredHeight() + child.getMeasuredHeight());
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
    GestureDetector mGestureListener = new GestureDetector(getContext(), new GestureListener(), null, true);

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.i(TAG, "distanceY:" + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureListener.onTouchEvent(ev);
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
//                Log.i(TAG, "disY:" + disY + "---" + "mTouchSlop:" + mTouchSlop);
                if (disY > 0) {   //下滑
//                    Log.i(TAG, "ScrollY:" + getScrollY());
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
//                    Log.i(TAG, "canScrollUp:" + mContentView.canScrollUp() + "---isFooterTotallyVisible:" + mWrapperViewState.isFooterTotallyVisible());
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
                    if (mWrapperViewState.getOffsetY() > mHeaderHeight) {  //超过了头部
                        //处理回调，更新状态
                        mCurState = mWrapperViewState.STATE_REFRESHING;
                        if (null != mHeaderCallback) {
                            mHeaderCallback.onRefreshing(); //更改head状态
                        }
                        mListener.onRefreshing(); //可以刷新数据
                    }
                    rollbackHead();
                    /**
                     * 如果不消费掉该事件
                     * 那么该事件会被分发给RecycleView，然后被当成一个点击事件处理
                     */
                    return true;
                }
                if (mWrapperViewState.isHasMovedUpVertically() && null != mFooterCallback) { //被上拉了
                    //根据上拉的距离改变状态，继而根据状态来判断究竟执行哪些动作
                    if (mWrapperViewState.isFooterTotallyVisible()) {
                        updateFootState(WrapperViewState.STATE_LOADING);
                        mListener.onLoadingMore();   //此时可以取数据
                    }
                    return true;
                }
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
    private void rollbackHead() {
        if (getScrollY() == 0) { //头部已经完全隐藏
            mCurState = mWrapperViewState.STATE_REFRESH_NORMAL;
            return;
        }

        int roll_DisY = 0; //在Y轴上的回滚距离
        if (mCurState == mWrapperViewState.STATE_REFRESHING) { //刷新状态,下拉距离超过了头部高度
            roll_DisY = mHeaderHeight + getScrollY();
        } else if (mCurState == mWrapperViewState.STATE_REFRESH_NORMAL || mCurState == mWrapperViewState.STATE_REFRESH_COMPLETED) { //正常状态，下拉距离没有达到头部的高度
            roll_DisY = getScrollY();
        }
//        Log.i(TAG, "roll_DisY:" + roll_DisY + "---" + "scrollY:" + getScrollY());
        startScroll(roll_DisY, SCROLL_DURATION);
    }

    private void rollbackFoot() {
        Log.i(TAG, "mCurState:" + mCurState);
        if (mCurState == WrapperViewState.STATE_LOAD_COMPLETED) {
            startScroll(-mWrapperViewState.getOffsetY(), SCROLL_DURATION);
        }
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
        if (Math.abs(mWrapperViewState.getOffsetY()) >= mFooterHeight) {
            mWrapperViewState.setIsFooterTotallyVisible(true);
            updateFootState(WrapperViewState.STATE_LOAD_READY);
        } else {
            Log.i(TAG, "---updateFooterHeight---");
            mWrapperViewState.setIsFooterTotallyVisible(false);
            updateFootState(WrapperViewState.STATE_LOAD_NORMAL);
        }
    }

    /**
     * 在该方法中需要回调头部状态的回调接口
     */
    private void updateHeaderHeight(int disY) {
        moveView(disY);
        if (mWrapperViewState.getOffsetY() > mHeaderHeight && null != mHeaderCallback) {
            updateHeadState(WrapperViewState.STATE_REFRESH_READY);
        } else if (mWrapperViewState.getOffsetY() < mHeaderHeight && null != mHeaderCallback) {
            updateHeadState(WrapperViewState.STATE_REFRESH_NORMAL);
        }
    }

    /**
     * 改进：
     * 将滑动和回滚分为两个函数实现，避免互相影响
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int offsetY = mScroller.getCurrY() + mWrapperViewState.getOffsetY();
            moveView(-offsetY);

        } else {
//            Log.i(TAG, "回滚完后的状态:" + mCurState);
            Log.i(TAG, "当前的滚动距离:" + getScrollY());
            /**
             *处理回滚完成后的状态之后的状态
             *1.头部必须设置为初始状态---下来刷新状态
             *2.尾部必须设置为初始状态---加载更多状态
             */
            if (mCurState == mWrapperViewState.STATE_LOAD_COMPLETED && getScrollY() == 0) {
                updateFootState(WrapperViewState.STATE_LOAD_NORMAL);
                mWrapperViewState.setIsFooterTotallyVisible(false);
            }
            if (mCurState == mWrapperViewState.STATE_REFRESH_COMPLETED && getScrollY() == 0) {
                updateHeadState(WrapperViewState.STATE_REFRESH_NORMAL);
            }
        }
    }

    /**
     * 该方法只管滚动界面，并不担心状态的问题
     *
     * @param offsetY
     */
    private void moveView(int offsetY) {
        mWrapperViewState.setOffsetY(offsetY);//改变WrapperView状态
        scrollTo(0, -mWrapperViewState.getOffsetY());
        ViewCompat.postInvalidateOnAnimation(this);

    }

    private static Handler mHandler = new Handler();
    private int mPinnedTime;//刷新完成之后头部停留的时间

    /**
     * 回调接口，暴露给外部控制类刷新完成之后进行回调
     * 此时将头部回滚,直到完全隐藏
     */
    public void stopRefresh() {
        if (mCurState == WrapperViewState.STATE_REFRESHING) {
            /**
             *更新该控件的各种状态,处理回调
             * 1.比如，头部的状态需要更新,那么，我只要定义一个头部状态的回调接口
             * 我调用接口的方法，具体的实现由具体的类去做，这样就是很好的面向接口
             * 编程了
             */
            updateHeadState(WrapperViewState.STATE_REFRESH_COMPLETED);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rollbackHead();
                }
            }, 1000);
        }
    }

    private OnWrapperViewStateListener mListener;

    public void setRefreshStateListener(OnWrapperViewStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        this.mListener = listener;
    }

    /**
     * 更新头部状态
     *
     * @param curState 实时状态
     */
    private void updateHeadState(int curState) {
        mCurState = curState;
        if (null == mHeaderCallback) {
            return;
        }
        switch (curState) {
            case WrapperViewState.STATE_REFRESH_NORMAL:
                mHeaderCallback.onStateNormal();
                break;
            case WrapperViewState.STATE_REFRESH_READY:
                mHeaderCallback.onRefreshReady();
                break;
            case WrapperViewState.STATE_REFRESH_COMPLETED:
                mHeaderCallback.onRefreshCompleted();
                break;
            case WrapperViewState.STATE_REFRESHING:
                mHeaderCallback.onRefreshing();
                break;
        }
    }

    /**
     * 更新尾部状态
     *
     * @param curState 实时状态
     */
    private void updateFootState(int curState) {
        Log.i(TAG, "curState:" + curState);
        mCurState = curState;
        if (null == mFooterCallback) {
            return;
        }
        switch (curState) {
            case WrapperViewState.STATE_LOAD_NORMAL:
                mFooterCallback.onLoadingNormal();
                break;
            case WrapperViewState.STATE_LOAD_READY:
                mFooterCallback.onLoadingReady();
                break;
            case WrapperViewState.STATE_LOAD_COMPLETED:
                mFooterCallback.onLoadingCompleted();
                break;
            case WrapperViewState.STATE_LOADING:
                mFooterCallback.onLoading();
                break;
        }
    }

    public void stopLoading() {
        Log.i(TAG, "当前状态:" + mCurState);
        if (mCurState == WrapperViewState.STATE_LOADING) {
            updateFootState(WrapperViewState.STATE_LOAD_COMPLETED);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rollbackFoot();
                }
            }, 1000);
        }
    }

    public interface OnWrapperViewStateListener {

        void onRefreshing();

        void onLoadingMore();
    }


}
