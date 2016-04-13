package com.example.pj.news_demo.http;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.pj.news_demo.domain.MsgDetailModel;
import com.example.pj.news_demo.domain.ReturnObj;
import com.example.pj.news_demo.domain.StoryModel;
import com.example.pj.news_demo.utils.DiskCacheUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pj on 2016/4/7.
 * 暂时在泛型的处理上遇到了问题
 * 后面会简化代码，使其更容易扩展
 */
public class HttpResultParser {
    private static final String TAG = "HttpResultParser";
    private static HttpResultParser sHttpResultParser;

    public static HttpResultParser getInstance() {
        if (sHttpResultParser == null) {
            synchronized (HttpResultParser.class) {
                if (sHttpResultParser == null) {
                    sHttpResultParser = new HttpResultParser();
                }
            }
        }
        return sHttpResultParser;
    }

    /**
     * 将返回的String转换为我们需要的model
     */

    public ArrayList<StoryModel> getMessageList(String date) {

        String url = HttpConstants.ZHIHU_DAILY_BEFORE_MESSAGE + date;
        return getData(url);
    }

    /**
     * get the message detail
     */
    public MsgDetailModel getMsgDetail(String id) {
        String url = HttpConstants.ZHIHU_DAILY_BEFORE_MESSAGE_DETAIL + id;
        MsgDetailModel msgDetailModel = null;
        /**
         * 先从缓存中拿
         */
        String result = tryGetDataFromDisk(url); //该方法可以通用,因为我们拿到的始终是字符串
        if (TextUtils.isEmpty(result)) {
            try {
                //从服务器拿
                result = HttpTools.fetchData(url, new HashMap<String, String>());
                if (TextUtils.isEmpty(result)) {
                    return msgDetailModel;
                } else {
                    //那结果保存在磁盘缓存中
                    DiskCacheUtil.getInstance().saveText(url, result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        msgDetailModel = JSONObject.parseObject(result, MsgDetailModel.class);
        return msgDetailModel;
    }

    private ArrayList<StoryModel> getData(String url) {
        /**
         * 先从缓存中拿
         */
        String result = tryGetDataFromDisk(url);
        ArrayList<StoryModel> mStoriesModels = new ArrayList<>();
        if (TextUtils.isEmpty(result)) {
            try {
                //get from server
                result = HttpTools.fetchData(url, new HashMap<String, String>());
                if (TextUtils.isEmpty(result)) {
                    return mStoriesModels;
                } else {
                    //save data to disk
                    DiskCacheUtil.getInstance().saveText(url, result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return mStoriesModels;
            }
        }
        Log.i(TAG, "result:" + result);
        ReturnObj returnObj = JSONObject.parseObject(result, ReturnObj.class);
        mStoriesModels.addAll(JSONArray.parseArray(returnObj.getStories(), StoryModel.class));
        return mStoriesModels;
    }

    private String tryGetDataFromDisk(String url) {
        return DiskCacheUtil.getInstance().getText(url);
    }
}
