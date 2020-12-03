package com.max.navigation;

import android.app.Application;

import com.max.network.ApiService;

/**
 * @author: maker
 * @date: 2020/12/3 15:37
 * @description:
 */
public class MaxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo",null);
    }
}