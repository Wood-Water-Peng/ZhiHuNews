package com.example.pj.news_demo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.pj.news_demo.app.MyApp;

/**
 * Created by pj on 2016/4/14.
 */
public class DeviceUtil {
    /**
     * 拿到屏幕的高度和宽度
     * 分辨率
     */
    private static DisplayMetrics sMetrics = new DisplayMetrics();
    private static WindowManager sWindowManager;

    /**
     * 0为width,1为height
     */
    public static int[] getDevicePixs() {
        sWindowManager = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
        sWindowManager.getDefaultDisplay().getMetrics(sMetrics);
        return new int[]{sMetrics.widthPixels, sMetrics.heightPixels};
    }


}
