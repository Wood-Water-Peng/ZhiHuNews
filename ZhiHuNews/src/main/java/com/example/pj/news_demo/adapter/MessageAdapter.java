package com.example.pj.news_demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.domain.StoryModel;
import com.example.pj.news_demo.ui.activity.MessageDetailActivity;
import com.example.pj.news_demo.utils.ImageLoaderTools;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by pj on 2016/4/7.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private Context mContext;
    private ArrayList<StoryModel> mStoryModels;
    private ImageLoaderTools mImageLoaderTools;

    public MessageAdapter(Context context, ArrayList<StoryModel> storyModels) {
        this.mContext = context;
        this.mStoryModels = storyModels;
        mImageLoaderTools = ImageLoaderTools.getInstance(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_list, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //给控件设置数据
        holder.mTextView.setText(mStoryModels.get(position).getTitle());
        String img_url = mStoryModels.get(position).getImages().get(0);
        Log.i(TAG, "img_url:" + img_url);
        holder.mImageView.setImageURI(Uri.parse(img_url));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putExtra("id", mStoryModels.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStoryModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public SimpleDraweeView mImageView;
        public View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.tv_message_title);
            mImageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_message_img);
        }
    }
}