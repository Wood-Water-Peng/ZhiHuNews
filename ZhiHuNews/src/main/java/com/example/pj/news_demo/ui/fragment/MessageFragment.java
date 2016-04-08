package com.example.pj.news_demo.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.adapter.DividerLine;
import com.example.pj.news_demo.adapter.MessageAdapter;
import com.example.pj.news_demo.domain.StoryModel;
import com.example.pj.news_demo.http.HttpResultParser;

import java.util.ArrayList;

/**
 * Created by pj on 2016/4/6.
 */
public class MessageFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String MESSAGE_DATE = "date";
    private ViewGroup mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ArrayList<StoryModel> mStoryModels = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private String mDate;
    private FetchMsgWithDataTask mFetchTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_message, null);
        initView();
        initData();
        initListener();
        return mRootView;
    }

    private void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        mDate = getArguments().getString(MESSAGE_DATE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mMessageAdapter = new MessageAdapter(mContext, mStoryModels);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerLine(mContext, DividerLine.VERTICAL_LIST));
        mRecyclerView.setAdapter(mMessageAdapter);
        //开启一个异步加载任务
        mFetchTask = new FetchMsgWithDataTask();
        mFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.container);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.msg_list);
    }

    @Override
    public void onRefresh() {
        mFetchTask = new FetchMsgWithDataTask();
        mFetchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class FetchMsgWithDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<StoryModel> messageList = HttpResultParser.getMessageList(mDate);
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
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

