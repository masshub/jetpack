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
        ApiService.init("http://192.168.20.196:9090/serverdemo",null);
    }
}