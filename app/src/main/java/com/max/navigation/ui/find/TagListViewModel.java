package com.max.navigation.ui.find;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.max.navigation.abs.AbsViewModel;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;

import java.util.Collections;
import java.util.List;

/**
 * @author: maker
 * @date: 2021/1/15 16:31
 * @description:
 */
public class TagListViewModel  extends AbsViewModel<Feed> {
    private String feedType;

    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }


    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    private class DataSource extends ItemKeyedDataSource<Integer,Feed>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey,callback);

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key,callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());

        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }

        private void loadData(Integer feedId, ItemKeyedDataSource.LoadCallback<Feed> callback) {
            ApiResponse<List<Feed>> response = ApiService.get("/feeds/queryHotFeedsList")
                    .addParam("userId", UserManager.get().getUserId())
                    .addParam("pageCount", 10)
                    .addParam("feedType", feedType)
                    .addParam("feedId", feedId)
                    .execute();

            List<Feed> result = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(result);


        }
    }
}