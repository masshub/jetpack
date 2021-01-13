package com.max.navigation.ui.find;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.ItemKeyedDataSource;

import com.max.navigation.abs.AbsViewModel;
import com.max.navigation.model.TagList;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: maker
 * @date: 2021/1/13 15:05
 * @description:
 */
public class FindItemViewModel extends AbsViewModel<TagList> {
    private String tagType;
    private int offset;
    private AtomicBoolean loadAfter = new AtomicBoolean();
    private MutableLiveData switchFindTab = new MutableLiveData();


    public MutableLiveData getSwitchFindTab() {
        return switchFindTab;
    }

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    public void setTagType(String tagType){
        this.tagType = tagType;
    }


    private class DataSource extends ItemKeyedDataSource<Long,TagList>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<TagList> callback) {
            loadData(params.requestedInitialKey,callback);

        }


        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            loadData(params.key,callback);

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<TagList> callback) {
            callback.onResult(Collections.emptyList());

        }


        private void loadData(Long requestedKey, LoadCallback<TagList> callback) {
            if(requestedKey > 0){
                loadAfter.set(true);
            }
            ApiResponse<List<TagList>> response = ApiService.get("/tag/queryTagList")
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("tagId", requestedKey)
                    .addParam("tagType", tagType)
                    .addParam("pageCount", 10)
                    .addParam("offset", offset)
                    .execute();

            List<TagList> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);
            if(requestedKey > 0){
                loadAfter.set(false);
                offset += result.size();
                ((MutableLiveData)getBoundaryPageData()).postValue(result);
            } else {
                offset = result.size();
            }


        }

        @NonNull
        @Override
        public Long getKey(@NonNull TagList item) {
            return Long.valueOf(item.tagId);
        }
    }


    @SuppressLint("RestrictedApi")
    public void loadData(long tagId, ItemKeyedDataSource.LoadCallback callback){
        if(tagId <= 0 || loadAfter.get()){
            callback.onResult(Collections.emptyList());
            return;
        }

        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                ((FindItemViewModel.DataSource)getDataSource()).loadData(tagId,callback);
            }
        });

    }
}