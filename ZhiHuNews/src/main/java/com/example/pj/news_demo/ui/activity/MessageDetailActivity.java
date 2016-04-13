package com.example.pj.news_demo.ui.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.constants.MsgConstants;
import com.example.pj.news_demo.domain.MsgDetailModel;
import com.example.pj.news_demo.http.HttpResultParser;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by pj on 2016/4/7.
 */
public class MessageDetailActivity extends BaseActivity {
    private static final String TAG = "MessageDetailActivity";
    private TextView mTvBody;
    private String mMsg_id;
    private MsgDetailModel mMsgDetailModel;
    private FetchMsgDetailTask mFetchMsgDetailTask;
    private SimpleDraweeView mSimpleDraweeView;
    private TextView mTvImgSource;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        initView();
        initData();
        fetchData();
    }

    private void fetchData() {
        if (mMsgDetailModel == null) {
            Log.i(TAG, "we do not have local data!");
            mFetchMsgDetailTask = new FetchMsgDetailTask();
            mFetchMsgDetailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            setData();
        }
    }

    private void setData() {
        mTvBody.setMovementMethod(LinkMovementMethod.getInstance());
        mTvBody.setText(Html.fromHtml(mMsgDetailModel.getBody()));
        mCollapsingToolbarLayout.setTitle(mMsgDetailModel.getTitle());
        mSimpleDraweeView.setImageURI(Uri.parse(mMsgDetailModel.getImage()));
        mTvImgSource.setText(mMsgDetailModel.getImage_source());
    }

    private void initData() {
        mMsg_id = getIntent().getStringExtra(MsgConstants.MSG_ID);
    }

    private void initView() {
        mTvBody = (TextView) findViewById(R.id.tv_body);
        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.iv_appbar);
        mTvImgSource = (TextView) findViewById(R.id.tv_img_source);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctl);
    }

    private class FetchMsgDetailTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            /**
             * 思路:
             * 该方法只想得到一个对象，至于这个对象是怎么解析出来的，是来自网络的数据还是来自本地
             * 他统统不care
             */
            mMsgDetailModel = HttpResultParser.getInstance().getMsgDetail(mMsg_id);
            if (mMsgDetailModel == null) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            if (flag) {
                setData();
            } else {
                Snackbar.make(mCollapsingToolbarLayout, "Please check you network", Snackbar.LENGTH_LONG)
                        .setAction("Get it", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //do something
                            }
                        })
                        .show();
            }
        }
    }
}
