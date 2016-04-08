package com.example.pj.news_demo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.pj.news_demo.R;
import com.example.pj.news_demo.adapter.MessageFragmentAdapter;
import com.example.pj.news_demo.utils.DateGenerator;

import java.util.ArrayList;

/**
 * 先在主界面将最新的消息展现出来
 */
public class MainActivity extends BaseActivity {
    private ViewPager mViewPager;
    private MessageFragmentAdapter mMessageFragmentAdapter;
    private ArrayList<String> mDates = new ArrayList<String>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initTitle();
    }

    private void initTitle() {
        mToolbar.setTitle("Home");
    }

    private void initData() {
        mDates = DateGenerator.getWeekDate();
        mMessageFragmentAdapter = new MessageFragmentAdapter(getSupportFragmentManager(), mDates);
        mViewPager.setAdapter(mMessageFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);//setupWithViewPager必须在ViewPager.setAdapter()之后调用
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_message_list);
        mToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTabLayout = (TabLayout) findViewById(R.id.tl_date);
    }
}
