package com.example.pj.news_demo.http;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pj on 2016/4/7.
 */
public class HttpTools {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "HttpTools";

    //利用okhttp从网络抓取数据
    public static String fetchData(String url, Map<String, String> params) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request.Builder request_builder = new Request.Builder();
        Request request = request_builder.url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.i(TAG, "result:" + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
