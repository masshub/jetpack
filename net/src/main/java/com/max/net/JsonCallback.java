package com.max.net;

/**
 * @author: maker
 * @date: 2020/11/30 16:47
 * @description:
 */
public abstract class JsonCallback<T> {


    public void onSuccess(ApiResponse<T> response){}
    public void onError(ApiResponse<T> response){}
    public void onCacheSuccess(ApiResponse<T> response){}





}