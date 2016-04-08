package com.example.pj.news_demo.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by pj on 2016/4/7.
 */
public class BaseActivity extends AppCompatActivity {
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getSupportActionBar().hide();
    }
}
