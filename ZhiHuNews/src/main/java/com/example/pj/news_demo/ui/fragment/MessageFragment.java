package com.example.pj.news_demo.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.adapter.DividerLine;
import com.example.pj.news_demo.adapter.MessageAdapter;
import com.example.pj.news_demo.domain.StoryModel;
import com.example.pj.news_demo.http.HttpResultParser;
import com.example.pj.news_demo.utils.DeviceUtil;
import com.example.pj.news_demo.widget.RefreshFooter;
import com.example.pj.news_demo.widget.RefreshHeader;
import com.example.pj.news_demo.widget.WrapperView;

import java.util.ArrayList;

/**
 * Created by pj on 2016/4/6.
 * 给RecycleView增加一个加载更多的尾巴
 */
public class MessageFragment extends BaseFragment {
    public static final String MESSAGE_DATE = "date";
    private static final String TAG = "MessageFragment";
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private ArrayList<StoryModel> mStoryModels = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private String mDate;
    private FetchMsgWithDataTask mFetchTask;
    private LinearLayoutManager mLinearLayoutManager;
    private WrapperView mWrapperView;
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_message, null);
        initView();
        initData();
        Log.i(TAG, "屏幕Width:" + DeviceUtil.getDevicePixs()[0] + "---屏幕Height:" + DeviceUtil.getDevicePixs()[1]);
        return mRootView;
    }


    private void initData() {
        mDate = getArguments().getString(MESSAGE_DATE);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mMessageAdapter = new MessageAdapter(mContext, mStoryModels);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerLine(mContext, DividerLine.VERTICAL_LIST));
        mRecyclerView.setAdapter(mMessageAdapter);
        //开启一个异步加载任务
        mFetchTask = new FetchMsgWithDataTask();
        mFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.msg_list);
        mWrapperView = (WrapperView) mRootView.findViewById(R.id.fragment_msg_wrapper_view);
//        Log.i(TAG, "child:" + mWrapperView.getChildAt(0).getClass().getCanonicalName() + "------height:" + mWrapperView.getChildAt(0).getMeasuredHeight());
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


    }

    private class FetchMsgWithDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<StoryModel> messageList = HttpResultParser.getInstance().getMessageList(mDate);
            Log.i(TAG, "messageList.size:" + messageList.size());
            if (messageList.size() > 0) {
                if (mStoryModels.size() > 0) {
                    mStoryModels.clear();
                }
                mStoryModels.addAll(messageList);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mStoryModels.size() > 0) {
                mMessageAdapter.notifyDataSetChanged();
            }
        }
    }

    private class LoadMoreTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<StoryModel> messageList = HttpResultParser.getInstance().getMessageList(mDate);
            mStoryModels.addAll(messageList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mStoryModels.size() > 0) {
                mMessageAdapter.onLoadMoreSuccess();
            } else {
                mMessageAdapter.onLoadMoreFailed();
            }
        }
    }
}

