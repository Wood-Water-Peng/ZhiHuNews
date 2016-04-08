package com.example.pj.news_demo.ui.fragment;

import android.os.Bundle;

/**
 * Created by pj on 2016/4/7.
 */
public class FragmentFactory {
    private static FragmentFactory mFactory;

    public static FragmentFactory newInstance() {
        if (mFactory == null) {
            mFactory = new FragmentFactory();
        }
        return mFactory;
    }

    public MessageFragment createMessageFragment(Bundle bundle) {
        MessageFragment fragment = new MessageFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }
}
