package com.example.pj.news_demo.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.pj.news_demo.domain.MsgDetailModel;
import com.example.pj.news_demo.domain.ReturnObj;
import com.example.pj.news_demo.domain.StoryModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pj on 2016/4/7.
 */
public class HttpResultParser {
    /**
     * 将返回的String转换为我们需要的model
     */
    public static ArrayList<StoryModel> getMessageList(String date) {
        ArrayList<StoryModel> mStoriesModels = new ArrayList<>();
        String url = HttpConstants.ZHIHU_DAILY_BEFORE_MESSAGE + date;
        try {
            String result = HttpTools.fetchData(url, new HashMap<String, String>());
            if (TextUtils.isEmpty(result)) {
                return mStoriesModels;
            } else {
                ReturnObj returnObj = JSONObject.parseObject(result, ReturnObj.class);
                mStoriesModels.addAll(JSONArray.parseArray(returnObj.getStories(), StoryModel.class));
                return mStoriesModels;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mStoriesModels;
    }

    /**
     * get the message detail
     */
    public static MsgDetailModel getMsgDetail(String id) {
        String url = HttpConstants.ZHIHU_DAILY_BEFORE_MESSAGE_DETAIL + id;
        MsgDetailModel msgDetailModel = null;
        try {
            String result = HttpTools.fetchData(url, new HashMap<String, String>());
            if (TextUtils.isEmpty(result)) {
                return msgDetailModel;
            } else {
                msgDetailModel = JSONObject.parseObject(result, MsgDetailModel.class);
                return msgDetailModel;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msgDetailModel;
    }

}
