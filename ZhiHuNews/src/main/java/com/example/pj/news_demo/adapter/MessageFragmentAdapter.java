package com.example.pj.news_demo.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.pj.news_demo.ui.fragment.FragmentFactory;
import com.example.pj.news_demo.ui.fragment.MessageFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pj on 2016/4/6.
 */
public class MessageFragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> mDatas;

    public MessageFragmentAdapter(FragmentManager fm, ArrayList<String> datas) {
        super(fm);
        this.mDatas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(MessageFragment.MESSAGE_DATE, mDatas.get(position));
        return FragmentFactory.newInstance().createMessageFragment(bundle);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Calendar displayDate = Calendar.getInstance();
        displayDate.add(Calendar.DAY_OF_YEAR, -position);

        return DateFormat.getDateInstance().format(displayDate.getTime());

    }
}
