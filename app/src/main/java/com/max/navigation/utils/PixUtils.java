package com.max.navigation.utils;

import android.util.DisplayMetrics;

import com.max.common.AppGlobals;

/**
 * @author: maker
 * @date: 2020/12/2 16:05
 * @description:
 */
public class PixUtils {
    public static int dp2px(int dp) {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density * dp + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }


    public static int getScreenHeight() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
} 