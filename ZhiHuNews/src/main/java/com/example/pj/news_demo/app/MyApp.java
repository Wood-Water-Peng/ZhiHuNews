package com.example.pj.news_demo.app;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by pj on 2016/4/7.
 */
public class MyApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
