package com.example.pj.news_demo.manager;

import com.example.pj.news_demo.ui.activity.BaseActivity;

import java.util.ArrayList;

/**
 * Created by pj on 2016/4/8.
 */
public class ActivityManager {
    static ArrayList<BaseActivity> mActivities = new ArrayList<>();
    private static ActivityManager sActivityManager;

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {
        if (sActivityManager == null) {
            synchronized (ActivityManager.class) {
                if (sActivityManager == null) {
                    sActivityManager = new ActivityManager();
                }
                return sActivityManager;
            }
        }
        return sActivityManager;
    }

    public void addActivity(BaseActivity activity) {
        mActivities.add(activity);
    }

    public void clear() {
        mActivities.clear();
    }

    public void removeActivity(BaseActivity activity) {
        mActivities.remove(activity);
    }
}
