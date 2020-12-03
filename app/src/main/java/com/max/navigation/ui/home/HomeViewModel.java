package com.max.navigation.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.max.navigation.abs.AbsViewModel;
import com.max.navigation.model.Feed;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;
import com.max.network.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeViewModel extends AbsViewModel<Feed> {
    private volatile boolean withCache = true;

    @Override
    public DataSource createDataSource() {
        return dataSource;
    }

    ItemKeyedDataSource<Integer, Feed> dataSource = new ItemKeyedDataSource<Integer, Feed>() {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            // 加载初始化
            loadData(0, callback);
            withCache = false;

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            // 加载分页数据
            loadData(params.key, callback);

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            // 向前加载
            callback.onResult(Collections.emptyList());

        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    };

    private void loadData(int i, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        Request request = ApiService.get("/feed/queryHostFeedsList")
                .addParam("feedType", null)
                .addParam("userId", 0)
                .addParam("feedId", i)
                .addParam("pageCount", 10)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if(withCache){
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e("onCacheSuccess", "onCacheSuccess: " + response.body.size() );
                }
            });
        }


        try {
            Request netRequest = withCache ? request.clone():request;
            netRequest.cacheStrategy(i == 0 ? Request.NET_ONLY : Request.CACHE_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);

            if(i > 0){
                getBoundaryPageData().postValue(data.size() > 0);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }
}