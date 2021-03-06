package com.max.navigation.utils;

import java.util.Calendar;

/**
 * @author: maker
 * @date: 2020/12/8 17:52
 * @description:
 */
public class TimeUtils {
    public static String calculate(long time) {
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        long diff = (timeInMillis - time) / 1000;
        if (diff < 60) {
            return diff + "秒前";
        } else if (diff < 3600) {
            return diff / 60 + "分钟前";
        } else if (diff < 3600 * 24) {
            return diff / 3600 + "小时前";
        } else {
            return diff / (3600 * 24) + "天前";
        }
    }


}