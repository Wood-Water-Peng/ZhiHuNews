package com.example.pj.news_demo.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by pj on 2016/4/7.
 */
public class DateGenerator {
    public static ArrayList<String> getWeekDate() {
        ArrayList<String> dates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1 - i);
            dates.add(simpleDateFormat.format(calendar.getTime()));
        }
        return dates;
    }
}
