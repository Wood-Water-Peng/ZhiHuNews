package com.example.pj.news_demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.domain.StoryModel;
import com.example.pj.news_demo.ui.activity.MessageDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by pj on 2016/4/7.
 * 1.增加一个Footer
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    public static final int STATE_LOADING = 100; //正在加载
    public static final int STATE_LOAD_MORE_SUCCESS = 101; //加载更多——成功
    public static final int STATE_LOAD_MORE_FAILED = 102; //加载更多——失败
    private Context mContext;
    private ArrayList<StoryModel> mStoryModels;
    private static final int FOOTER_VIEW = 1;
    private static final int NORMAL_VIEW = 2;
    private static final int HEADER_VIEW = 3;
    private NormalViewHolder mNormalViewHolder;
    private FooterViewHolder mFooterViewHolder;

    public MessageAdapter(Context context, ArrayList<StoryModel> storyModels) {
        this.mContext = context;
        this.mStoryModels = storyModels;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mStoryModels.size()) {
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == FOOTER_VIEW) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_footer_msg_list, null);
            mFooterViewHolder = new FooterViewHolder(view);
            return mFooterViewHolder;
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_list, null);
            mNormalViewHolder = new NormalViewHolder(view);
            return mNormalViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //给控件设置数据
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder viewHolder = (NormalViewHolder) holder;
            viewHolder.bindView(position);
        }

    }

    @Override
    public int getItemCount() {
        return mStoryModels.size() + 1;
    }

    //更新视图状态
    public void updateState(int state) {
        switch (state) {
            case STATE_LOADING:
                mFooterViewHolder.updateState(FooterViewHolder.LOADING);
                break;
            case STATE_LOAD_MORE_SUCCESS:
                mFooterViewHolder.updateState(FooterViewHolder.NORMAL);
                break;
            case STATE_LOAD_MORE_FAILED:
                mFooterViewHolder.updateState(FooterViewHolder.FAILED);
                break;
        }
        notifyDataSetChanged();
        Log.i(TAG, "---notifyDataSetChanged---");
    }

    public void onLoadMoreSuccess() {
        updateState(STATE_LOAD_MORE_SUCCESS);
    }

    public void onLoadMoreFailed() {
        updateState(STATE_LOAD_MORE_FAILED);
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public SimpleDraweeView mImageView;
        public View itemView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.tv_message_title);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_message_img);
        }

        public void bindView(final int position) {
            mTextView.setText(mStoryModels.get(position).getTitle());
            String img_url = mStoryModels.get(position).getImages().get(0);
            Log.i(TAG, "img_url:" + img_url);
            mImageView.setImageURI(Uri.parse(img_url));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MessageDetailActivity.class);
                    intent.putExtra("id", mStoryModels.get(position).getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_loadmore;
        public Button btn_reload;
        public ProgressBar pb_loading;
        private static final int LOADING = 100;
        private static final int NORMAL = 101;
        private static final int FAILED = 102;

        public FooterViewHolder(View itemView) {
            super(itemView);
            tv_loadmore = (TextView) itemView.findViewById(R.id.tv_msg_list_footer);
            btn_reload = (Button) itemView.findViewById(R.id.btn_msg_list_footer_reload);
            pb_loading = (ProgressBar) itemView.findViewById(R.id.pb_msg_list_loading);
        }

        public void bindView(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        public void updateState(int state) {
            switch (state) {
                case LOADING:
                    pb_loading.setVisibility(View.VISIBLE);
                    tv_loadmore.setVisibility(View.GONE);
                    btn_reload.setVisibility(View.GONE);
                    break;
                case NORMAL:
                    pb_loading.setVisibility(View.GONE);
                    tv_loadmore.setVisibility(View.VISIBLE);
                    btn_reload.setVisibility(View.GONE);
                    break;
                case FAILED:
                    pb_loading.setVisibility(View.GONE);
                    tv_loadmore.setVisibility(View.GONE);
                    btn_reload.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

}