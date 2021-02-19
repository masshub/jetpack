package com.max.navigation.ui.my;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.alibaba.fastjson.TypeReference;
import com.max.navigation.abs.AbsViewModel;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: maker
 * @date: 2021/2/8 17:24
 * @description:
 */
public class ProfileViewModel extends AbsViewModel<Feed> {
    private String profileType;

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    private class DataSource extends ItemKeyedDataSource<Long,Feed>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey,callback);

        }

        private void loadData(Long key, ItemKeyedDataSource.LoadCallback<Feed> callback) {
            ApiResponse<List<Feed>> response = ApiService.get("/feed/queryProfileFeeds")
                    .addParam("inId", key)
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("pageCount", 20)
                    .addParam("profileType", profileType)
                    .responseType(new TypeReference<ArrayList<Feed>>() {
                    }.getType())
                    .execute();

            List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
            if(key > 0){
                ((MutableLiveData)getBoundaryPageData()).postValue(result.size() > 0);
            }

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key,callback);

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {

        }

        @NonNull
        @Override
        public Long getKey(@NonNull Feed item) {
            return null;
        }
    }
}