package com.max.navigation.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author: maker
 * @date: 2020/12/8 16:23
 * @description:
 */
public class StatusBar {
    public static void fitSystemBar(Activity activity) {
        // 沉浸式状态栏小于6.0，不予适配
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        // SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN :布局延申到状态栏下方，状态栏不消失只是遮盖在布局上方
        // SYSTEM_UI_FLAG_FULLSCREEN：布局延时到状态栏下方，同时隐藏状态栏
//        WindowManager.LayoutParams.FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }
} 