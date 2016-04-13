package com.example.pj.news_demo.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * Created by pj on 2016/4/12.
 */
public class ViewUtil {
    public static void removeViewFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
    }
}
